package com.wj.crawler.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.wj.crawler.common.ConfigModule;
import com.wj.crawler.db.orm.UserCrawInfoDAO;
import com.wj.crawler.db.orm.WeiboDAO;
import com.wj.crawler.db.orm.WeiboUserDAO;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Properties;

/**
 * Created by SYu on 3/14/2017.
 */

@Module(includes = ConfigModule.class)
public class DbModule {

    private final String DEFAULT_DB_NAME = "crawDB";
    private final String DEFAULT_USER_COLL_NAME = "weibo_user";
    private final String DEFAULT_WEIBO_COLL_NAME = "weibo";
    private final String DEFAULT_WEIBO_USER_CRAW_COLL_NAME = "weibo_user_craw";

    @Provides
    @Singleton
    MongoClient providerMongoCollection(Properties config) {
        return new MongoClient(config.getProperty("db.server"), Integer.parseInt(config.getProperty("db.port")));
    }

    @Provides
    @Singleton
    MongoDatabase providerMongoDB(MongoClient conn, Properties config) {
        String dbName = config.getProperty("db.name",DEFAULT_DB_NAME);
        return conn.getDatabase(dbName);
    }


    @Provides @Named("wb_user")
    MongoCollection provideUserCollection(MongoDatabase db) {
        return getCollection(db,DEFAULT_USER_COLL_NAME);
    }

    @Provides @Named("weibo")
    MongoCollection provideWeiboCollection(MongoDatabase db) {
        return getCollection(db,DEFAULT_WEIBO_COLL_NAME);
    }

    @Provides @Named("wb_user_craw")
    MongoCollection provideWeiboUserCrawCollection(MongoDatabase db) {
        return getCollection(db,DEFAULT_WEIBO_USER_CRAW_COLL_NAME);
    }

    private MongoCollection getCollection(MongoDatabase db, String coll){
        MongoCollection collection = db.getCollection(coll);
        if (collection == null) {
            db.createCollection(coll);
            collection = db.getCollection(coll);
        }
        return collection;
    }


    @Provides
    WeiboUserDAO providerWeiboUserDao(@Named("wb_user") MongoCollection collection) {
        return new WeiboUserDAO(collection);
    }

    @Provides
    WeiboDAO providerWeiboDao(@Named("weibo") MongoCollection collection) {
        return new WeiboDAO(collection);
    }

    @Provides
    UserCrawInfoDAO providerUserCrawInfoDAO(@Named("wb_user_craw") MongoCollection collection) {
        return new UserCrawInfoDAO(collection);
    }
}


