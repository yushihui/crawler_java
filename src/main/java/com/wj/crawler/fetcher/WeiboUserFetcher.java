package com.wj.crawler.fetcher;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.wj.crawler.common.HttpUtil;
import com.wj.crawler.db.orm.WeiboUserDAO;
import com.wj.crawler.parser.WeiboParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by SYu on 3/14/2017.
 */
public class WeiboUserFetcher {
    private final CloseableHttpClient httpClient;
    private final WeiboUserDAO userDAO;
    private final WeiboParser parser;
    private static final Logger Log = LoggerFactory.getLogger(WeiboUserFetcher.class);

    @Inject
    public WeiboUserFetcher(CloseableHttpClient httpClient, WeiboUserDAO dao, WeiboParser parser) {
        this.httpClient = httpClient;
        this.userDAO = dao;
        this.parser = parser;
    }

    private void fetchPage(int page) {

        try {
            HttpGet httpget = new HttpGet("http://m.weibo.cn/container/getSecond?containerid=1005055676212874_-_FOLLOWERS&page=2");
            CloseableHttpResponse response = httpClient.execute(httpget);
            Log.info("going to parse page..." + page);
            List<Document> documents = parser.fromEntity2Document(response.getEntity());
            Log.info("going to save to db...");
            userDAO.bulkInsert(documents);
            Log.info("done success!!!");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public void doFetch() {
        int page = 3;
        int max = 4;
        for (int i = 3; i < max; i++) {
            fetchPage(i);
        }
    }


}
