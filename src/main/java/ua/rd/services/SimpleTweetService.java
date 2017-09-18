package ua.rd.services;

import ua.rd.domain.Tweet;
import ua.rd.repository.TweetRepository;

/*
LazyTweetProxy extends Tweet{
    String bean;
    Tweet getInstance(){
        appContext.getBean(bean);
        }
    }



    tweet = LazyTweetProxy("tweet").getInstance;
 */

public class SimpleTweetService implements TweetService {
    private final TweetRepository tweetRepository;
    private Tweet tweet;

    public SimpleTweetService(TweetRepository tweetRepository, Tweet tweet) {
        this.tweetRepository = tweetRepository;
        this.tweet = tweet;
    }



    @Override
    public Iterable<Tweet> allTweets() {
        return tweetRepository.allTweets();
    }

    @Override
    public TweetRepository getRepository() {
        return tweetRepository;
    }

    @Override
    public Tweet newTweet() {
        return new Tweet();
    }
}
