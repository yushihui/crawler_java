package com.wj.adaptor;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.wj.search.index.MongoIndexing;
import org.bson.Document;

import javax.inject.Inject;
import java.util.concurrent.Executors;

/**
 * Created by Syu on 4/21/2017.
 */
public class MongoBridge {

    private MongoIndexing index;
    private MongoDatabase db;

    private final int PAGE_SIZE = 1000;

    @Inject
    public MongoBridge(MongoIndexing index, MongoDatabase db) {
        this.index = index;
        this.db = db;
    }


    public boolean moving2Elastic(String collection, boolean fromBegining) {

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        MongoCollection collec = db.getCollection(collection);
        long total = 0;
        if(fromBegining){
            total = collec.count();
        }
        int page = 0;
        while(page * PAGE_SIZE < total){

            Iterable<Document> result = collec.find().skip(page * PAGE_SIZE).limit(PAGE_SIZE);

            page ++;
        }



        return false;



    }


}
