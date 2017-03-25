package com.wj.crawler.service;

import com.wj.crawler.parser.WeiboParser;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by Administrator on 3/24/2017.
 */

@Module
public final class ServiceModule {

    @Provides
    @Singleton
    WeiboParser providerWeiboParser() {
        return new WeiboParser();
    }
}
