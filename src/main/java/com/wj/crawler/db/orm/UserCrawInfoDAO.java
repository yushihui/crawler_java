package com.wj.crawler.db.orm;

import com.mongodb.client.MongoCollection;

/**
 * Created by Administrator on 3/23/2017.
 */
public class UserCrawInfoDAO extends BaseDAO{

   public UserCrawInfoDAO(MongoCollection collection){
        super(collection);
    }


}
