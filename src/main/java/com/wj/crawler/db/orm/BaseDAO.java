package com.wj.crawler.db.orm;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;

/**
 * Created by Administrator on 3/23/2017.
 */
public abstract class BaseDAO {

    protected final MongoCollection collection;

    public BaseDAO(MongoCollection collection){
        this.collection = collection;
    }
    public void bulkInsert(List<Document> documents) {
        collection.insertMany(documents);
    }

}
