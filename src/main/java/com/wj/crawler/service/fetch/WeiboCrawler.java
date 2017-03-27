package com.wj.crawler.service.fetch;

import com.wj.crawler.db.orm.CrawUserInfo;
import com.wj.crawler.db.orm.WeiboDAO;
import com.wj.crawler.parser.WeiboParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by SYu on 3/24/2017.
 */
public class WeiboCrawler implements Callable<Boolean> {

    private int retry = 3;
    private int MAX_PAGE = 3;
    private CrawUserInfo user;
    private int page = 1;
    private final WeiboDAO dao;
    private final WeiboParser parser;

    private static final Logger Log = LoggerFactory.getLogger(WeiboCrawler.class);
    private List<Document> weibos;


    public WeiboCrawler(WeiboDAO dao, WeiboParser parser, CrawUserInfo user) {
        this.dao = dao;
        this.parser = parser;
        this.user = user;
        weibos = new ArrayList<Document>();
    }



    void doFetch() {
        if (page > MAX_PAGE) {
            return;
        }
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {
            HttpGet httpget = new HttpGet(user.weiboUrl() + page);
            //httpget.setHeaders(headers.toArray(new Header[headers.size()]));
            CloseableHttpResponse response = httpClient.execute(httpget);
            Log.debug("going to parse page..." + page);
            List<Document> documents = parser.parserWeiboContent(response.getEntity());
            boolean synced = syncWithCache(documents);
            if (!synced) {
                page++;
                doFetch();
            } else {
                return;
            }
        } catch (Exception e) {
            Log.error(" fetch fail for user " + user.getUserId());
            if (retry == 0) {
                return;
            }
            retry--;
            doFetch();
        }
    }


    public boolean syncWithCache(List<Document> documents) {
        boolean found = false;
        for (Document d : documents) {
            if (d.getString("id") == user.getLastPostId()) {
                found = true;
                break;
            } else {
                weibos.add(d);
            }
        }

        if (documents.get(0).getString("id") == user.getLastPostId()) {

        } else {
            user.setLastPostId(documents.get(0).getString("id")); //todo update cache and crawUserInfo
        }
        user.setLastFetchTime(Calendar.getInstance().getTime());
        return found;
    }


    public Boolean call() throws Exception {
        doFetch();
        if (weibos.size() > 0) {
            dao.bulkInsert(weibos);
        }
        return true;//todo when we should return false;
    }
}
