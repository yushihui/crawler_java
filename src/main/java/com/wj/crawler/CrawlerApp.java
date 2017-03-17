package com.wj.crawler;


import com.wj.crawler.common.NetModule;
import com.wj.crawler.db.DbModule;
import com.wj.crawler.fetcher.WeiboUserFetcher;
import com.wj.crawler.parser.ParserModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by SYu on 3/14/2017.
 */
public class CrawlerApp {


    @Singleton
    @Component(modules = {NetModule.class, DbModule.class, ParserModule.class})
    public interface Fetcher {
        WeiboUserFetcher getFetcher();
    }

    public static void main(String args[]) {

        Fetcher fetcher = DaggerCrawlerApp_Fetcher.builder().build();
        WeiboUserFetcher weiboUserFetcher = fetcher.getFetcher();
        weiboUserFetcher.doFetch();


    }


}
