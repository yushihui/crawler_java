package com.wj.crawler.fetcher;

import com.wj.crawler.db.orm.WeiboUserDAO;
import com.wj.crawler.parser.FollowerParser;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Created by shihui on 8/8/2017.
 */
public class UserFollowerFetcher extends AbstractFetcher{

    private CloseableHttpClient httpClient;
    private final WeiboUserDAO userDAO;
    private final FollowerParser parser;
    private final List<Header> headers;
    private static final Logger Log = LoggerFactory.getLogger(UserFollowerFetcher.class);

    @Inject
    public UserFollowerFetcher(CloseableHttpClient httpClient, WeiboUserDAO dao, FollowerParser parser, List<Header> headers) {
        this.httpClient = httpClient;
        this.userDAO = dao;
        this.parser = parser;
        this.headers = headers;
    }


    void fetchPage(int page) {
        httpClient = HttpClients.custom().build();
        try {
            HttpGet httpget = new HttpGet(URL_PREFIX + page);
            httpget.setHeaders(headers.toArray(new Header[headers.size()]));
            CloseableHttpResponse response = httpClient.execute(httpget);
            Log.info("going to parse page..." + page);
            List<Document> documents = parser.parseWeiboUser(response.getEntity());
            Log.info("going to save to db...");
            userDAO.bulkInsert(documents);
            Log.info("done success!!!");

        } catch (Exception e) {

            try {
                httpClient.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            httpClient = HttpClients.custom().build();
            e.printStackTrace();
        }
    }

    public void doFetchUser() {
        doFetch(1, 3, 2);
    }
}
