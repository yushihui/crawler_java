package com.wj.crawler.db.orm;


import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.gt;

/**
 * Created by SYu on 3/14/2017.
 */
public class WeiboUserDAO extends BaseDAO {

    private final static int FOLLOWERS_LIMIT = 100000; // 100K

    public WeiboUserDAO(MongoCollection collection) {
        super(collection);
    }

    public List<Document> getUserBasicInfos() {
        List<Document> documents = new ArrayList<Document>();
        Iterator it = this.collection.find(gt("followers_count", FOLLOWERS_LIMIT)).iterator();
        while (it.hasNext()) {
            Document d = (Document) it.next();
            documents.add(d);
        }
        return documents;
    }


}
