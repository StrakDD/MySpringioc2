package ua.rd.services;

import ua.rd.domain.Tweet;
import ua.rd.ioc.Context;
import ua.rd.repository.TweetRepository;

/*
class PrototypeTweetProxy implements TweetService {

    private Context context;
    private TweetService tweetService;

    public PrototypeTweetProxy(TweetService tweetService, Context context) {
        this.context = context;
    }

    @Override
    public Iterable<Tweet> allTweets() {
        return tweetService.allTweets();
    }

    @Override
    public TweetRepository getRepository() {
        return tweetService.getRepository();
    }

    @Override
    public Tweet newTweet() {
        return (Tweet) context.getBean("tweet");
    }
}
*/

public class SimpleTweetService implements TweetService {
    private final TweetRepository tweetRepository;

    public SimpleTweetService(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }



    @Override
    public Iterable<Tweet> allTweets() {
        return tweetRepository.allTweets();
    }

    @Override
    public TweetRepository getRepository() {
        return tweetRepository;
    }


}
