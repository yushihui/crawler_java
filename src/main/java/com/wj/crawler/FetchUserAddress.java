package com.wj.crawler;

import com.wj.crawler.common.CacheManager;
import com.wj.crawler.common.NetModule;
import com.wj.crawler.db.DbModule;
import com.wj.crawler.fetcher.FetchUserInfoService;
import com.wj.crawler.fetcher.WeiboUserFetcher;
import com.wj.crawler.parser.ParserModule;
import com.wj.crawler.service.ServiceModule;
import com.wj.search.index.SearchModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by Administrator on 10/1/2017.
 */
public class FetchUserAddress {



    @Singleton
    @Component(modules = {NetModule.class, DbModule.class, ParserModule.class, ServiceModule.class, SearchModule.class})
    public interface Fetcher {
        WeiboUserFetcher getFetcher();
        FetchUserInfoService getUserInfoFetcherService();
        CacheManager getCache();


    }
    public static void main (String args[]){

        Fetcher fetcher = DaggerFetchUserAddress_Fetcher.builder().build();
        FetchUserInfoService fuis = fetcher.getUserInfoFetcherService();
        fuis.doFetching();

    }
}
