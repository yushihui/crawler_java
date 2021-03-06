package com.wj.crawler;


import com.wj.adaptor.MongoBridge;
import com.wj.crawler.common.CacheManager;
import com.wj.crawler.common.NetModule;
import com.wj.crawler.db.DbModule;
import com.wj.crawler.fetcher.*;
import com.wj.crawler.parser.ParserModule;
import com.wj.crawler.scheduler.SchedulerServiceManager;
import com.wj.crawler.service.ServiceModule;
import com.wj.search.index.MongoIndexing;
import com.wj.search.index.SearchModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by SYu on 3/14/2017.
 */
public class CrawlerApp {


    @Singleton
    @Component(modules = {NetModule.class, DbModule.class, ParserModule.class, ServiceModule.class, SearchModule.class})
    public interface Fetcher {
        WeiboUserFetcher getFetcher();

        // FetchWithoutCookie getWeiboFetcher();
        WeiboContenFetcher getWeiboFetcher();

        CrawSyncService getCyService();

        SchedulerServiceManager getServiceManager();

        MongoIndexing getMongoIndexing();

        MongoBridge getMongoBridge();

        CacheManager getCache();

        UserFollowerFetcher getFollowerFetcher();

        FetchFollowerService getFetchFollowerService();

    }

    public static void main(String args[]) {


        Fetcher fetcher = DaggerCrawlerApp_Fetcher.builder().build();
//        CacheManager cache = fetcher.getCache();
//        cache.testProxy();

         // removeMongo2Elastic(fetcher);
//        WeiboUserFetcher weiboUserFetcher = fetcher.getFetcher();
//        weiboUserFetcher.doFetchUser();

//        CrawSyncService cyService = fetcher.getCyService();
//        cyService.syncCrawInfoWithUser();
//        SchedulerServiceManager ssm = fetcher.getServiceManager();
//        ssm.start();

//        UserFollowerFetcher userFollowerFetcher = fetcher.getFollowerFetcher();
//        userFollowerFetcher.doFetchUser("1680990025");
        FetchFollowerService ffs = fetcher.getFetchFollowerService();
        ffs.doFetching();


    }

    private static void testMongo2Elas(Fetcher fetcher) {

        MongoBridge bridge = fetcher.getMongoBridge();

        bridge.moving2Elastic("weibo", true);

        //indexing.bulkIndexing(new ArrayList<Document>(),"weibo");

    }

    private static void removeMongo2Elastic(Fetcher fetcher) {

        MongoIndexing indexing = fetcher.getMongoIndexing();

        indexing.removeIndex("devicedata");

        //indexing.bulkIndexing(new ArrayList<Document>(),"weibo");

    }

    public static void testService() {


    }


}
