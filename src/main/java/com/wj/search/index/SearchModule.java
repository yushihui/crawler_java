package com.wj.search.index;

import com.wj.crawler.common.ConfigModule;
import com.wj.crawler.db.DbModule;
import dagger.Module;
import dagger.Provides;

import java.util.Properties;

/**
 * Created by Administrator on 4/20/2017.
 */

@Module(includes = {
        ConfigModule.class, DbModule.class
})
public final class SearchModule {


    @Provides
    IndexClient providerIndexClient(Properties config) {
        return new IndexClient(config);
    }

    @Provides
    MongoIndexing providerMongoIndexing(IndexClient client) {
        return new MongoIndexing(client);
    }



}