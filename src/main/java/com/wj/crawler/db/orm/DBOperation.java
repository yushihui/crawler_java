package com.wj.crawler.db.orm;

import org.bson.Document;

import java.util.List;

/**
 * Created by SYu on 3/14/2017.
 */
public interface DBOperation<T extends Document> {
    void bulkInsert(List<T> documents);
}
