package ua.rd.ioc;

import java.lang.reflect.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

public class ApplicationContext implements Context {
    private List<BeanDefinition> beanDefinitions;
    private Map<String, Object> beans = new HashMap<>();

    public ApplicationContext(Config config) {
        beanDefinitions = Arrays.asList(config.beanDefinitions());
        initContext(beanDefinitions);
    }

    private void initContext(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(bd -> getBean(bd.getBeanName()));
    }

    public ApplicationContext() {
        beanDefinitions = Arrays.asList(Config.EMPTY_BEANDEFINITION);
    }

    public Object getBean(String beanName) {

        BeanDefinition beanDefinition = getBeanDefinitionByName(beanName);
        return Optional
                .ofNullable(beans.get(beanName))
                .orElseGet(() ->
                    createBeanByDefinition(beanDefinition)
                );
    }

    private Object createBeanByDefinition(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        Object bean = createNewBean(beanDefinition);
        if (!beanDefinition.isPrototype()) {
            beans.put(beanName, bean);
        }
        return bean;
    }

    private Object createNewBean(BeanDefinition beanDefinition) {
        BeanBuilder beanBuilder = new BeanBuilder(beanDefinition);
        beanBuilder.createNewBeanInstance();
        beanBuilder.callPostConstructAnnotatedMethod();
        beanBuilder.callInitMethod();
        beanBuilder.createBenchmarkProxy();

        Object bean = beanBuilder.build();

        return bean;

    }

    private BeanDefinition getBeanDefinitionByName(String beanName) {
        return beanDefinitions.stream()
                .filter(bd -> Objects.equals(bd.getBeanName(), beanName))
                .findAny().orElseThrow(NoSuchBeanException::new);
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitions.stream().map(bd -> bd.getBeanName()).toArray(String[]::new);

    }

    class BeanBuilder {
        private BeanDefinition beanDefinition;
        private Object bean;

        public BeanBuilder(BeanDefinition beanDefinition) {
            this.beanDefinition = beanDefinition;
        }

        private void createNewBeanInstance() {
            Class<?> type = beanDefinition.getBeanType();
            Constructor<?> constructor = type.getDeclaredConstructors()[0];
            Object newBean;

            if (constructor.getParameterCount() == 0) {
                newBean = createBeanWithDefaultConstructor(type);
            } else {
                newBean = createNewBeanWithConstructorWithParams(type);
            }
            bean = newBean;
        }

        private void callPostConstructAnnotatedMethod() {
            Class<?> beanType = bean.getClass();

            try {
                Method[] methods = beanType.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(MyPostConstruct.class)) {
                        method.invoke(bean);
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        private void callInitMethod() {
            Class<?> beanType = bean.getClass();
            try {
                Method initMethod = beanType.getMethod("init");
                initMethod.invoke(bean);
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private Object createBeanWithDefaultConstructor(Class<?> type) {

            Object newBean;
            try {
                newBean = type.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return newBean;
        }

        private Object createNewBeanWithConstructorWithParams(Class<?> type) {
            Constructor<?> constructor = type.getDeclaredConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();


            List<Object> parameters = new ArrayList<>();

            for (Class<?> parameterType : parameterTypes) {
                String name = Character.toLowerCase(parameterType.getSimpleName().charAt(0)) +
                        parameterType.getSimpleName().substring(1);
                parameters.add(getBean(name));
            }

            Object object = null;
            try {
                object = type.getConstructor(parameterTypes).newInstance(parameters.toArray());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return object;
        }

        private void createBenchmarkProxy() {
            Class<?> beanType = bean.getClass();
            Object newBean = bean;

            for (Method method : beanType.getMethods()) {
                if (method.isAnnotationPresent(Benchmark.class)) {
                    bean = Proxy.newProxyInstance(
                            beanType.getClassLoader(),
                            beanType.getInterfaces(),
                            new InvocationHandler() {
                                @Override
                                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                    Method m = beanType.getMethod(method.getName(), method.getParameterTypes());
                                    if (m.isAnnotationPresent(Benchmark.class) && m.getAnnotation(Benchmark.class).enabled()) {
                                        Long start = System.nanoTime();
                                        LocalDateTime before = LocalDateTime.now();
                                        Object result = method.invoke(newBean, args);
                                        Long stop = System.nanoTime();
                                        LocalDateTime after = LocalDateTime.now();
                                        //System.out.println("Duration: " + (stop - start));
                                        //System.out.println(Duration.between(before, after).getNano());
                                        return result;
                                    } else {
                                        return method.invoke(newBean, args);
                                    }
                                }
                            });
                    break;
                }
            }

        }


        public Object build() {
            return bean;
        }
    }
}
