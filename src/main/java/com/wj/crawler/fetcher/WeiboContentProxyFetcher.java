package com.wj.crawler.fetcher;

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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by SYu on 3/22/2017.
 */
public class WeiboContentProxyFetcher {

    private CloseableHttpClient httpClient;
    private final WeiboDAO dao;
    private final WeiboParser parser;
    private int retry = 3;
    private int MAX_PAGE = 3;

    private static final Logger Log = LoggerFactory.getLogger(WeiboContentProxyFetcher.class);

    @Inject
    public WeiboContentProxyFetcher(WeiboDAO dao, WeiboParser parser) {
        this.dao = dao;
        this.parser = parser;
    }

    void doFetch(CrawUserInfo user, int page) {
        if(page > MAX_PAGE){
           return;
        }
        httpClient = HttpClients.custom().build();
        try {
            HttpGet httpget = new HttpGet(user.weiboUrl() + page);
            //httpget.setHeaders(headers.toArray(new Header[headers.size()]));
            CloseableHttpResponse response = httpClient.execute(httpget);
            Log.debug("going to parse page..." + page);
            List<Document> documents = parser.parserWeiboContent(response.getEntity());
            boolean synced = syncWithDb(documents,user);
            if(!synced){
                page ++ ;
                doFetch(user,page);
            }else{
                return ;
            }
        } catch (Exception e) {
            Log.error(" fetch fail for user " + user.getUserId());
            if (retry == 0) {
                return;
            }
            retry--;
            doFetch(user, page);
        }
    }

    private boolean syncWithDb(List<Document> documents, CrawUserInfo user) {
        boolean found = false;
        List<Document> newTweets = new ArrayList<Document>();
        for (Document d : documents) {
            if (d.getString("id") == user.getLastPostId()) {
                found = true;
                break;
            } else {
                newTweets.add(d);
            }
        }
        dao.bulkInsert(newTweets);
        if(documents.get(0).getString("id") == user.getLastPostId()){

        }else{
            user.setLastPostId(documents.get(0).getString("id")); //todo update cache and crawUserInfo
        }
        user.setLastFetchTime(Calendar.getInstance().getTime());
        return found;
    }


}
