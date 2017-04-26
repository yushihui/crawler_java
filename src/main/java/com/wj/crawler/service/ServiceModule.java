package com.wj.crawler.service;

import com.google.common.util.concurrent.Service;
import com.mongodb.client.MongoDatabase;
import com.wj.crawler.common.CacheManager;
import com.wj.crawler.common.ConfigModule;
import com.wj.crawler.db.DbModule;
import com.wj.crawler.db.Named;
import com.wj.crawler.db.orm.UserCrawInfoDAO;
import com.wj.crawler.db.orm.WeiboDAO;
import com.wj.crawler.parser.WeiboParser;
import com.wj.crawler.scheduler.MongoAdaptorService;
import com.wj.crawler.scheduler.WeiboFetchService;
import com.wj.search.index.IndexClient;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

import java.util.Properties;

/**
 * Created by SYu on 3/24/2017.
 */

@Module(includes = {
        ConfigModule.class,
        DbModule.class
})
public final class ServiceModule {


    @Provides
    @IntoSet
    Service provideWeiboFetchService(Properties config, WeiboDAO dao, WeiboParser parser, CacheManager cache, UserCrawInfoDAO ucDao) {
        return new WeiboFetchService(config, dao, parser, cache, ucDao);
    }

    @Provides
    @IntoSet
    Service provideMongoAdaptorService(Properties config, @Named("el") MongoDatabase db, IndexClient client, CacheManager cache) {
        return new MongoAdaptorService(config, db, client, cache);
    }

//    @Provides
//    @IntoSet
//    Service provideToutiaoFetchService() {
//        return new ToutiaoFetchService();
//    }


}
