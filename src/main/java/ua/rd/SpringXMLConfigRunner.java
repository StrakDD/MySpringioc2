package ua.rd;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.rd.services.TweetService;

import java.util.Arrays;

/**
 * Created by Denys_Starovoitenko on 9/15/2017.
 */
public class SpringXMLConfigRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext repoContext = new ClassPathXmlApplicationContext("repoContext.xml");

        ConfigurableApplicationContext serviceContext = new ClassPathXmlApplicationContext(
                new String[]{"serviceContext.xml"}, repoContext);

        System.out.println(Arrays.toString(repoContext.getBeanDefinitionNames()));
        System.out.println(Arrays.toString(serviceContext.getBeanDefinitionNames()));

        TweetService tweetService = (TweetService) serviceContext.getBean("tweetService");
        System.out.println(tweetService.allTweets());

        TweetService repoService = (TweetService) repoContext.getBean("tweetRepository");
        System.out.println(repoService.allTweets());

        System.out.println(repoContext.getBean("tweet"));
        System.out.println(repoContext.getBean("tweetParent"));

        serviceContext.close();
        repoContext.close();
    }
}
