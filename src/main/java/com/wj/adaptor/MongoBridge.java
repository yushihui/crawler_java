package com.wj.adaptor;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.wj.crawler.db.Named;
import com.wj.search.index.IndexClient;
import com.wj.search.index.MongoIndexing;
import org.bson.Document;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by Syu on 4/21/2017.
 */
public class MongoBridge extends AbstractScheduledService {


    private MongoDatabase db;
    private IndexClient client;
    private final int PAGE_SIZE = 1000;

    @Inject
    public MongoBridge(@Named("el") MongoDatabase db, IndexClient client) {

        this.db = db;
        this.client = client;
    }


    public void moving2Elastic(String collection, boolean fromBegining) {

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        MongoCollection collec = db.getCollection(collection);
        List<ListenableFuture<Integer>> futures = new ArrayList();
        long total = 0;
        if (fromBegining) {
            total = collec.count();
            int page = 0;
            while (page * PAGE_SIZE < total) {
                Iterable<Document> docs = collec.find().skip(page * PAGE_SIZE).limit(PAGE_SIZE);
                ListenableFuture<Integer> indexFuture = service.submit(new MongoIndexing(docs, collection, client, "text"));
                futures.add(indexFuture);
                page++;
            }
        } else {

        }

    }


    @Override
    protected void runOneIteration() throws Exception {

    }

    @Override
    protected Scheduler scheduler() {
        return null;
    }
}
