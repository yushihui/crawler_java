package com.wj.crawler.service;

import com.wj.crawler.common.ConfigModule;
import com.wj.crawler.db.DbModule;
import dagger.Module;

/**
 * Created by Administrator on 3/24/2017.
 */

@Module(includes = {
        ConfigModule.class,
        DbModule.class
})
public final class ServiceModule {


}
