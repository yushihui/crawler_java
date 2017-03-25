package com.wj.crawler.db.orm;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.mongodb.client.MongoCollection;
import com.wj.crawler.common.ProxyObject;
import org.bson.Document;

import javax.annotation.Nullable;

/**
 * Created by SYu on 3/24/2017.
 */
public class ProxyDAO extends BaseDAO {

    public ProxyDAO(MongoCollection collection) {
        super(collection);
    }

    public Iterable<ProxyObject> loadProxy() {
        Iterable documents = collection.find();
        Iterable<ProxyObject> userList = Iterables.transform(documents, new Function<Document, ProxyObject>() {
            @Nullable
            public ProxyObject apply(@Nullable Document document) {

                String ip = document.getString("_id");
                int port = document.getInteger("port");
                boolean available = document.getBoolean("available", true);
                ProxyObject proxy = ProxyObject.create(ip, port, available);
                return proxy;
            }

        });
        return userList;
    }

    public void add(ProxyObject proxy){
        Document d = new Document();
        d.append("_id", proxy.ip());
        d.append("port", proxy.port());
        d.append("available", proxy.IsAvailable());
        collection.insertOne(d);
    }

}
