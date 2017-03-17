package com.wj.crawler.db.orm;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.wj.crawler.db.QualifierUser;
import org.bson.Document;

import javax.inject.Inject;

import java.util.List;

/**
 * Created by SYu on 3/14/2017.
 */
public class WeiboUserDAO {

    private final MongoCollection collection;



    public WeiboUserDAO(MongoCollection collection){
        this.collection = collection;
    }

    public void bulkInsert(List<Document> documents) {

        collection.insertMany(documents);

    }

}
