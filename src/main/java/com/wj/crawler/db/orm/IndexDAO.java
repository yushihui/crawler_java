package com.wj.crawler.db.orm;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.wj.crawler.common.Tuple;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by Syu on 4/24/2017.
 */
public class IndexDAO extends BaseDAO {

    public IndexDAO(MongoCollection collection) {
        super(collection);
    }

    public Iterable<Tuple<String, Date>> loadMongoIndex() {
        Iterable documents = collection.find();
        Iterable<Tuple<String, Date>> collectList = Iterables.transform(documents, new Function<Document, Tuple<String, Date>>() {
            @Nullable
            public Tuple<String, Date> apply(@Nullable Document document) {

                String collection = document.getString("_id");
                Date create_date = document.getDate("date");

                return new Tuple(collection, create_date);
            }

        });
        return collectList;
    }

    public void add(String name, Date date) {
        Document d = new Document();
        d.append("_id", name);
        d.append("date", date);
        collection.updateOne(new BasicDBObject("_id",name),new Document("$set", d),
                new UpdateOptions().upsert(true)
        );
    }
}
