package com.wj.search.index;

import com.wj.crawler.common.Tuple;
import org.bson.Document;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by Syu on 4/21/2017.
 */
public class MongoIndexing implements InIndex<Document>, Callable<Tuple<Integer, Date>> {


    private TransportClient client;
    private Iterable<Document> documents;
    private String collection;
    private String indexField;

    private static final String INDEX_PREFIX = "mongo-";
    private static final String INDEX_TYPE = "mongo";

    private static final Logger Log = LoggerFactory.getLogger(MongoIndexing.class);
    private Date date;


    @Inject
    public MongoIndexing(IndexClient client) {
        this.client = client.getClient();
    }


    public MongoIndexing(Iterable<Document> documents, String collection, IndexClient client, String indexField) {
        this.client = client.getClient();
        this.collection = collection;
        this.documents = documents;
        this.indexField = indexField;

    }


    @Override
    public Tuple<Integer, Date> bulkIndexing(Iterable<Document> documents, String collection) {
        int indexedDocuments = 0;


        BulkRequestBuilder bulkRequest = client.prepareBulk();
        documents.forEach(d -> {
                    if (date == null) {
                        date = d.getDate("f_time");
                    }
                    try {
                        bulkRequest.add(client.prepareIndex(INDEX_PREFIX + collection.toLowerCase(), INDEX_TYPE,
                                d.getObjectId("_id").toHexString()).setSource(jsonBuilder()
                                .startObject()
                                .field("content", d.getString(indexField))
                                .endObject()
                        ));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        indexedDocuments = bulkRequest.numberOfActions();
        if (indexedDocuments == 0) {
            return new Tuple(0, null);
        }

        long start = System.currentTimeMillis();
        try {
            BulkResponse bulkResponse = bulkRequest.execute().get();
            if (bulkResponse.hasFailures()) {
                Log.debug("{} :indexing failed...", collection);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.info("one bulk indexing takes {}", (System.currentTimeMillis() - start));
        return new Tuple(indexedDocuments,date);
    }

    @Override
    public boolean removeIndex(String index) {
        try {
            DeleteIndexResponse response = client.admin().indices().prepareDelete(INDEX_PREFIX + index).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return true; //todo how to check is false
    }


    @Override
    public Tuple<Integer, Date> call() throws Exception {
        return bulkIndexing(documents, collection);
    }
}
