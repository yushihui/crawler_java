package com.wj.crawler.parser;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by SYu on 3/16/2017.
 */

@Module
public class ParserModule {

    @Provides
    @Singleton
    WeiboParser providerWeiboParser() {
        return new WeiboParser();
    }

    @Provides
    @Singleton
    FollowerParser providerFollowerParser() {
        return new FollowerParser();
    }
}
