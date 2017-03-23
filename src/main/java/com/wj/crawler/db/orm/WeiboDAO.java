package com.wj.crawler.db.orm;

import com.mongodb.client.MongoCollection;

/**
 * Created by SYu on 3/14/2017.
 */
public class WeiboDAO extends BaseDAO{

    public WeiboDAO(MongoCollection collection) {
        super(collection);
    }


}
