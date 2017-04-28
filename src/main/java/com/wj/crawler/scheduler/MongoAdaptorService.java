package com.wj.crawler.scheduler;

import com.google.common.util.concurrent.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.wj.crawler.common.CacheManager;
import com.wj.crawler.common.TimeUtils;
import com.wj.crawler.common.Tuple;
import com.wj.crawler.db.orm.IndexDAO;
import com.wj.search.index.IndexClient;
import com.wj.search.index.MongoIndexing;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;

/**
 * Created by Syu on 4/25/2017.
 */
public class MongoAdaptorService extends AbstractScheduledService {

    private static final Logger Log = LoggerFactory.getLogger(MongoAdaptorService.class);

    private final Properties config;
    private final MongoDatabase db;
    private final IndexClient client;
    private final int PAGE_SIZE = 500;
    private final CacheManager cache;
    private long roundStartTime;
    private long docsCount = 0;
    private String collection;
    private final IndexDAO indexDAO;

    public MongoAdaptorService(Properties config, MongoDatabase db, IndexClient client, CacheManager cache, IndexDAO indexDAO) {
        this.config = config;
        this.db = db;
        this.client = client;
        this.cache = cache;
        this.indexDAO = indexDAO;
        this.collection = this.config.getProperty("elastic.mongo.collection");
    }

    private Callable<Boolean> usageComputation = () -> {
        try {
            Log.info("This iteration moved {} documents to Elastic, takes {}", docsCount, timeConsume());
        } catch (Exception e) {
            Log.error("usage error {}", e.getMessage());
        }

        return true;
    };

    private FutureCallback callBack = new FutureCallback<Tuple<Integer, Date>>() {
        public void onSuccess(Tuple<Integer, Date> result) {
            //Log.info("{} documents indexed.", result);
            docsCount += result.t;
            if (result.t > 0 && result.n != null) {
                //update cache
                Date d = cache.getIndexCache().getIfPresent(collection);
                if (d == null) {
                    updateCache(result.n);
                } else {
                    if (d.compareTo(result.n) < 0) {
                        updateCache(result.n);
                    }
                }

                Log.info("{} cache updated to {}.", collection, result.n);

            }
        }

        public void onFailure(Throwable thrown) {
            Log.info(" index failed");
        }
    };

    private void updateCache(Date d) {
        if (d != null) {
            cache.getIndexCache().put(collection, d);
        }
    }

    private String timeConsume() {

        //save to db
        indexDAO.add(collection, cache.getIndexCache().getIfPresent(collection));
        long millis = System.currentTimeMillis() - roundStartTime;
        return TimeUtils.Mills2PrettyString(millis);
    }


    public long moving2Elastic() {

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));
        MongoCollection collec = db.getCollection(collection);
        List<ListenableFuture<Tuple<Integer, Date>>> futures = new ArrayList();
        long total = 0;
        Date d = cache.getIndexCache().getIfPresent(collection);
        String sortBy = config.getProperty("elastic.mongo.collection.index.sort", "f_time");
        String indexField = config.getProperty("elastic.mongo.collection.index.field", "text");
        int page = 0;
        if (d == null) {
            total = collec.count();
            while ((page - 1) * PAGE_SIZE < total) {
                Iterable<Document> docs = collec.find().projection(Projections.include(indexField, sortBy)).sort(Sorts.descending(sortBy)).skip(page * PAGE_SIZE).limit(PAGE_SIZE);
                ListenableFuture<Tuple<Integer, Date>> indexFuture = service.submit(new MongoIndexing(docs, collection, client, indexField));
                futures.add(indexFuture);
                Futures.addCallback(indexFuture, callBack);
                page++;
            }
        } else {
            Date now = new Date();
            total = collec.count(and(gt(sortBy, d),lt(sortBy,now)));
            Log.debug("indexing from {} to {}", d.toString(), now.toString());
            while ((page - 1) * PAGE_SIZE < total) {
                Iterable<Document> docs = collec.find(and(gt(sortBy, d),lt(sortBy,now))).projection(Projections.include(indexField, sortBy)).sort(Sorts.descending(sortBy)).skip(page * PAGE_SIZE).limit(PAGE_SIZE);
                ListenableFuture<Tuple<Integer, Date>> indexFuture = service.submit(new MongoIndexing(docs, collection, client, indexField));
                futures.add(indexFuture);
                Futures.addCallback(indexFuture, callBack);
                page++;
            }

        }

        Futures.whenAllComplete(futures).call(usageComputation);
        return total;

    }

    @Override
    protected void runOneIteration() throws Exception {
        docsCount = 0;
        roundStartTime = System.currentTimeMillis();
        moving2Elastic();
    }

    @Override
    protected Scheduler scheduler() {
        int freq = Integer.parseInt(config.getProperty("elastic.mongo.index.freq"));
        return Scheduler.newFixedRateSchedule(0, freq, TimeUnit.MINUTES);
    }
}
