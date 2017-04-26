package com.wj.crawler.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.wj.crawler.common.CacheManager;
import com.wj.crawler.common.ConfigModule;
import com.wj.crawler.db.orm.*;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by SYu on 3/14/2017.
 */

@Module(includes = ConfigModule.class)
public final class DbModule {

    private final String DEFAULT_DB_NAME = "crawDB";
    private final String DEFAULT_USER_COLL_NAME = "weibo_user";
    private final String DEFAULT_WEIBO_COLL_NAME = "weibo";
    private final String DEFAULT_WEIBO_USER_CRAW_COLL_NAME = "weibo_user_craw";
    private final String DEFAULT_PROXY_COLL_NAME = "proxy";
    private final String DEFAULT_ELASTIC_INDEX_COLL_NAME = "elastic_search_index";

    @Provides
    @Singleton
    MongoClient providerMongoCollection(Properties config) {
        boolean auth = config.getProperty("db.auth").equalsIgnoreCase("true");
        if(auth){
            MongoCredential credential = MongoCredential.createCredential(config.getProperty("db.user"),
                    config.getProperty("db.user.location"), config.getProperty("db.user.pwd").toCharArray());
            return new MongoClient(new ServerAddress(config.getProperty("db.server"), Integer.parseInt(config.getProperty("db.port"))),
                    Arrays.asList(credential));
        }
        return new MongoClient(config.getProperty("db.server"), Integer.parseInt(config.getProperty("db.port")));
    }

    @Provides
    @Named("wb")
    @Singleton
    MongoDatabase providerMongoDB(MongoClient conn, Properties config) {
        String dbName = config.getProperty("db.name", DEFAULT_DB_NAME);
        return conn.getDatabase(dbName);
    }

    @Provides
    @Named("el")
    @Singleton
    MongoDatabase providerIndexMongoDB(MongoClient conn, Properties config) {
        String dbName = config.getProperty("elastic.mongo.db", DEFAULT_DB_NAME);
        return conn.getDatabase(dbName);
    }


    @Provides
    @Named("wb_user")
    MongoCollection provideUserCollection(@Named("wb") MongoDatabase db) {
        return getCollection(db, DEFAULT_USER_COLL_NAME);
    }

    @Provides
    @Named("weibo")
    MongoCollection provideWeiboCollection(@Named("wb") MongoDatabase db) {
        return getCollection(db, DEFAULT_WEIBO_COLL_NAME);
    }

    @Provides
    @Named("elastic_index")
    MongoCollection provideElasticIndexCollection(@Named("wb") MongoDatabase db) {
        return getCollection(db, DEFAULT_ELASTIC_INDEX_COLL_NAME);
    }


    @Provides
    @Named("proxy")
    MongoCollection provideProxyCollection(@Named("wb") MongoDatabase db) {
        return getCollection(db, DEFAULT_PROXY_COLL_NAME);
    }

    @Provides
    @Named("wb_user_craw")
    MongoCollection provideWeiboUserCrawCollection(@Named("wb") MongoDatabase db) {
        return getCollection(db, DEFAULT_WEIBO_USER_CRAW_COLL_NAME);
    }

    private MongoCollection getCollection(@Named("wb") MongoDatabase db, String coll) {
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
    ProxyDAO providerProxyDAO(@Named("proxy") MongoCollection collection) {
        return new ProxyDAO(collection);
    }

    @Provides
    IndexDAO providerElasticIndexDAO(@Named("elastic_index") MongoCollection collection) {
        return new IndexDAO(collection);
    }

    @Provides
    UserCrawInfoDAO providerUserCrawInfoDAO(@Named("wb_user_craw") MongoCollection collection) {
        return new UserCrawInfoDAO(collection);
    }

    @Provides
    @Singleton
    CacheManager providerCacheManager(UserCrawInfoDAO userDao, ProxyDAO proxyDao, IndexDAO indexDAO) {
        return new CacheManager(userDao, proxyDao, indexDAO);
    }
}


