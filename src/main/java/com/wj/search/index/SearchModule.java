package com.wj.search.index;

import com.wj.crawler.common.ConfigModule;
import dagger.Module;
import dagger.Provides;

import java.util.Properties;

/**
 * Created by Administrator on 4/20/2017.
 */

@Module(includes = {
        ConfigModule.class
})
public final class SearchModule {


    @Provides
    IndexClient providerIndexClient(Properties config) {
        return new IndexClient(config);
    }




}