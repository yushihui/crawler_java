package com.wj.crawler.fetcher;

import com.wj.crawler.db.orm.WeiboDAO;
import com.wj.crawler.parser.WeiboParser;
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
 * Created by SYu on 3/20/2017.
 */
public class FetchWithoutCookie {

    private CloseableHttpClient httpClient;
    private final WeiboDAO dao;
    private final WeiboParser parser;
    private final List<Header> headers;
    private static final Logger Log = LoggerFactory.getLogger(FetchWithoutCookie.class);

    @Inject
    public FetchWithoutCookie(CloseableHttpClient httpClient, WeiboDAO dao, WeiboParser parser, List<Header> headers) {
        this.httpClient = httpClient;
        this.dao = dao;
        this.parser = parser;
        this.headers = headers;
    }
    private void fetchPage(int page) {

        try {
            HttpGet httpget = new HttpGet("http://m.weibo.cn/container/getIndex?type=uid&value=1787537264&containerid=1076031787537264&page="+page);
            //httpget.setHeaders(headers.toArray(new Header[headers.size()]));
            CloseableHttpResponse response = httpClient.execute(httpget);
            Log.info("going to parse page..." + page);
            List<Document> documents = parser.parserWeiboContent(response.getEntity());
            Log.info("going to save to db...");
            dao.bulkInsert(documents);
            Log.info("done success!!!");

        } catch (Exception e) {
            Log.error(" fail !!!");

            retry(page);
        }
    }

    private void retry(int i){
        try {
            httpClient.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        httpClient = HttpClients.custom()
                .build();
        fetchPage(i);
    }

    public void doFetch() {
        int page = 2;
        int max = 5;
        for (int i = page; i < max; i++) {
            httpClient = HttpClients.custom()
                    .build();
            fetchPage(i);
            try {
                Thread.sleep(5*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
