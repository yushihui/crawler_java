package com.wj.crawler.fetcher;

import com.wj.crawler.common.BrowserProvider;
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
 * Created by Administrator on 3/20/2017.
 */
public class WeiboContenFetcher extends AbstractFetcher {

    private CloseableHttpClient httpClient;
    private final WeiboDAO dao;
    private final WeiboParser parser;

    private static final Logger Log = LoggerFactory.getLogger(WeiboContenFetcher.class);

    @Inject
    public WeiboContenFetcher(WeiboDAO dao, WeiboParser parser) {
        this.dao = dao;
        this.parser = parser;
        URL_PREFIX = "http://m.weibo.cn/container/getIndex?type=uid&value=1737694433&containerid=1076031737694433&page=";
    }

    void fetchPage(int page) {
        httpClient = HttpClients.custom().build();
        try {
            HttpGet httpget = new HttpGet(URL_PREFIX + page);
            //httpget.setHeaders(headers.toArray(new Header[headers.size()]));
            httpget.setHeaders(new Header[]{BrowserProvider.getRandBrowserAgent()});
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

    @Override
    boolean fetchPage(String url, boolean useProxy) {
        return true;
    }

    private void retry(int i) {
        try {
            httpClient.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        httpClient = HttpClients.custom()
                .build();
        fetchPage(i);
    }

    public void doFetchContent() {
        doFetch(1, 2, 3);
    }

}
