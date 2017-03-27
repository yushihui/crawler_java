package com.wj.crawler;


import com.wj.crawler.common.NetModule;
import com.wj.crawler.db.DbModule;
import com.wj.crawler.fetcher.CrawSyncService;
import com.wj.crawler.fetcher.WeiboContenFetcher;
import com.wj.crawler.fetcher.WeiboUserFetcher;
import com.wj.crawler.parser.ParserModule;
import com.wj.crawler.scheduler.SchedulerServiceManager;
import com.wj.crawler.service.ServiceModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by SYu on 3/14/2017.
 */
public class CrawlerApp {


    @Singleton
    @Component(modules = {NetModule.class, DbModule.class, ParserModule.class, ServiceModule.class})
    public interface Fetcher {
        WeiboUserFetcher getFetcher();
       // FetchWithoutCookie getWeiboFetcher();
        WeiboContenFetcher getWeiboFetcher();
        CrawSyncService getCyService();
        SchedulerServiceManager getServiceManager();
    }

    public static void main(String args[]) {

        Fetcher fetcher = DaggerCrawlerApp_Fetcher.builder().build();
//        WeiboUserFetcher weiboUserFetcher = fetcher.getFetcher();
//        weiboUserFetcher.doFetchUser();

//        WeiboContenFetcher weiboFetcher = fetcher.getWeiboFetcher();
//        weiboFetcher.doFetchContent();
//        CrawSyncService cyService = fetcher.getCyService();
//        cyService.syncCrawInfoWithUser();
        SchedulerServiceManager ssm = fetcher.getServiceManager();
        ssm.start();

    }

    public static void  testService(){



    }


}
