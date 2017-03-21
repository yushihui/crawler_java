package com.wj.crawler.db.orm;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;

/**
 * Created by SYu on 3/14/2017.
 */
public class WeiboDAO extends Document {

    private final MongoCollection collection;

    public WeiboDAO(MongoCollection collection) {
        this.collection = collection;
    }

    public void bulkInsert(List<Document> documents) {
        collection.insertMany(documents);
    }

}
