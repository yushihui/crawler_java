package com.wj.crawler.service;

import com.google.common.util.concurrent.Service;
import com.wj.crawler.common.CacheManager;
import com.wj.crawler.common.ConfigModule;
import com.wj.crawler.db.DbModule;
import com.wj.crawler.db.orm.WeiboDAO;
import com.wj.crawler.parser.WeiboParser;
import com.wj.crawler.scheduler.WeiboFetchService;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

import java.util.Properties;

/**
 * Created by Administrator on 3/24/2017.
 */

@Module(includes = {
        ConfigModule.class,
        DbModule.class
})
public final class ServiceModule {



    @Provides
    @IntoSet
    Service provideWeiboFetchService(Properties config, WeiboDAO dao, WeiboParser parser, CacheManager cach) {
        return new WeiboFetchService(config,dao,parser,cach);
    }

//    @Provides
//    @IntoSet
//    Service provideToutiaoFetchService() {
//        return new ToutiaoFetchService();
//    }


}
