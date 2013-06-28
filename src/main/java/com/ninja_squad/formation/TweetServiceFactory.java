package com.ninja_squad.formation;

import com.google.common.reflect.Reflection;

/**
 * Factory for tweet service
 * @author JB
 */
public final class TweetServiceFactory {
    private TweetServiceFactory() {
    }

    public static TweetService create() {
        return Reflection.newProxy(TweetService.class, new LoggingInvocationHandler(new TweetServiceImpl(new TweetDAO())));
    }
}
