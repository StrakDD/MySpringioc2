package ua.rd.ioc;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ApplicationContextTest {
    @Test(expected = NoSuchBeanException.class)
    public void getBeanWithEmptyContext() throws Exception {
        Context context = new ApplicationContext();
        context.getBean("abc");
    }

    @Test
    public void getBeanDefinitionNamesWithEmptyContext() throws Exception {
        //given
        Context context = new ApplicationContext();

        //when
        String[] actual = context.getBeanDefinitionNames();

        //then
        String[] expected = {};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getBeanDefinitionNamesWithOneBeanDefinition() throws Exception {
        String beanName = "FirstBean";

        Class<TestBean> beanType = TestBean.class;

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put(beanName, new HashMap<String, Object>(){{
                                put("type", beanType);
                            }}
                    );
                }};
        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        String[] actual = context.getBeanDefinitionNames();

        String[] expected = {beanName};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getBeanDefinitionNamesWithEmptyBeanDefinition() throws Exception {
        Map<String, Map<String, Object>> beanDescriptions = Collections.emptyMap();
        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        String[] actual = context.getBeanDefinitionNames();

        String[] expected = {};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getBeanDefinitionNamesWithSeveralBeanDefinition() throws Exception {
        String beanName1 = "FirstBean";
        String beanName2 = "SecondBean";
        String beanName3 = "ThirdBean";

        Class<TestBean> beanType1 = TestBean.class;
        Class<TestBean> beanType2 = TestBean.class;
        Class<TestBean> beanType3 = TestBean.class;


        Map<String, Map<String, Object>> beanDescriptions =
                new LinkedHashMap<String, Map<String, Object>>(){{
                    put(beanName1, new HashMap<String, Object>(){{
                        put("type", beanType1);
                    }});

                    put(beanName2, new HashMap<String, Object>(){
                        {
                            put("type", beanType2);
                        }});

                    put(beanName3, new HashMap<String, Object>(){{
                        put("type", beanType3);
                    }});
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        String[] actual = context.getBeanDefinitionNames();

        String[] expected = {beanName1, beanName2, beanName3};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getBeanWithOneBeanDefinitionIsNotNull() throws Exception {
        String beanName = "FirstBean";

        Class<TestBean> beanType = TestBean.class;

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put(beanName, new HashMap<String, Object>(){{
                                put("type", beanType);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        Object bean = context.getBean(beanName);

        assertNotNull(bean);
    }

    @Test
    public void getBeanWithOneBeanDefinition() throws Exception {
        String beanName = "FirstBean";

        Class<TestBean> beanType = TestBean.class;

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put(beanName, new HashMap<String, Object>(){{
                                put("type", beanType);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanInterface bean = (TestBeanInterface) context.getBean(beanName);
        assertNotNull(bean);
    }

    @Test
    public void getBeanWithSingleton() throws Exception {
        String beanName = "FirstBean";

        Class<TestBean> beanType = TestBean.class;

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put(beanName, new HashMap<String, Object>(){{
                                put("type", beanType);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanInterface bean1 = (TestBeanInterface) context.getBean(beanName);
        TestBeanInterface bean2 = (TestBeanInterface) context.getBean(beanName);
        assertSame(bean1, bean2);
    }

    @Test
    public void getBeanNotSameInstanceWithSameType() throws Exception {
        String beanName1 = "FirstBean";
        String beanName2 = "SecondBean";

        Class<TestBean> beanType = TestBean.class;

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put(beanName1, new HashMap<String, Object>(){{
                                put("type", beanType);
                            }}
                    );
                    put(beanName2, new HashMap<String, Object>(){{
                                put("type", beanType);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanInterface bean1 = (TestBeanInterface) context.getBean(beanName1);
        TestBeanInterface bean2 = (TestBeanInterface) context.getBean(beanName2);
        assertNotSame(bean1, bean2);
    }

    @Test
    public void getBeanIsPrototype() throws Exception {
        String beanName = "FirstBean";

        Class<TestBean> beanType = TestBean.class;

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put(beanName, new HashMap<String, Object>(){{
                                put("type", beanType);
                                put("isPrototype", true);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanInterface bean1 = (TestBeanInterface) context.getBean(beanName);
        TestBeanInterface bean2 = (TestBeanInterface) context.getBean(beanName);

        assertNotSame(bean1, bean2);
    }

    @Test
    public void getBeanWithDependedBeans() throws Exception {

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put("testBeanInterface", new HashMap<String, Object>(){{
                                put("type", TestBean.class);
                                put("isPrototype", false);
                            }}
                    );

                    put("testBeanWithConstructor", new HashMap<String, Object>(){{
                                put("type", TestBeanWithConstructors.class);
                                put("isPrototype", false);
                            }}
                    );

                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanInterface bean =
                (TestBeanInterface) context.getBean("testBeanWithConstructor");

        assertNotNull(bean);
    }

    @Test
    public void getBeanCallInitMethod() throws Exception {

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put("testBean", new HashMap<String, Object>(){{
                                put("type", TestBean.class);
                                put("isPrototype", false);
                            }}
                    );

                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanInterface bean = (TestBeanInterface) context.getBean("testBean");

        assertEquals("initialized", TestBean.intiValue);
    }

    @Test
    public void getBeanCallPostConstructAnnotatedMethod() throws Exception {

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put("testBean", new HashMap<String, Object>(){{
                                put("type", TestBean.class);
                                put("isPrototype", false);
                            }}
                    );

                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanInterface bean = (TestBeanInterface) context.getBean("testBean");

        assertEquals("initializedByPostConstructor",  TestBean.postConstructValue);
    }

    @Test
    public void TestBenchMark(){

        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>(){{
                    put("testBean", new HashMap<String, Object>(){{
                                put("type", TestBean.class);
                                put("isPrototype", false);
                            }}
                    );

                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanInterface bean = (TestBeanInterface) context.getBean("testBean");

        assertEquals("yyr",  bean.methodToBenchmark("ryy"));

    }

    public interface TestBeanInterface{
        String methodToBenchmark(String str);
        void postConstruct();
    }

    public static class TestBean implements TestBeanInterface{
        public static String intiValue;
        public static String postConstructValue;

        public void init(){
            intiValue = "initialized";
        }

        @Override
        @MyPostConstruct
        public void postConstruct(){
            postConstructValue = "initializedByPostConstructor";
        }

        @Override
        @Benchmark()
        public String methodToBenchmark(String str){
            return new StringBuilder(str).reverse().toString();
        }
    }

    public static class TestBeanWithConstructors implements TestBeanInterface{
        private final TestBeanInterface testBean;

        public TestBeanWithConstructors(TestBeanInterface testBean) {
            this.testBean = testBean;
        }

        @Override
        public String methodToBenchmark(String str) {
            return null;
        }

        @Override
        public void postConstruct() {

        }
    }

    public static class TestBeanWithConstructorsTwoParams implements TestBeanInterface{
        private final TestBeanInterface testBean1;
        private final TestBeanInterface testBean2;

        public TestBeanWithConstructorsTwoParams(TestBean testBean1, TestBean testBean2) {
            this.testBean1 = testBean1;
            this.testBean2 = testBean2;
        }

        @Override
        public String methodToBenchmark(String str) {
            return null;
        }

        @Override
        public void postConstruct() {

        }
    }





}