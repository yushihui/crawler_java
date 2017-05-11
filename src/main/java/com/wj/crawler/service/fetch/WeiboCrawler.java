package com.wj.crawler.service.fetch;

import com.wj.crawler.common.BrowserProvider;
import com.wj.crawler.common.CacheManager;
import com.wj.crawler.common.ProxyObject;
import com.wj.crawler.common.Tuple;
import com.wj.crawler.db.orm.CrawUserInfo;
import com.wj.crawler.db.orm.WeiboDAO;
import com.wj.crawler.parser.WeiboParser;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
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
public class WeiboCrawler implements Callable<Tuple<Integer, CrawUserInfo>> {

    private int retry = 3;
    private int MAX_PAGE = 1;
    private CrawUserInfo user;
    private int page = 1;
    private final WeiboDAO dao;
    private final WeiboParser parser;
    private final CacheManager cache;

    private static final Logger Log = LoggerFactory.getLogger(WeiboCrawler.class);
    private List<Document> weibos;

    public WeiboCrawler(WeiboDAO dao, WeiboParser parser, CrawUserInfo user, CacheManager cache) {
        this.dao = dao;
        this.parser = parser;
        this.user = user;
        weibos = new ArrayList<>();
        this.cache = cache;
    }


    private void useProxy(ProxyObject proxy, HttpGet httpget) {

        RequestConfig.Builder config = RequestConfig.custom().setConnectTimeout(6 * 1000);
        if (proxy != null) {

            HttpHost pxy = new HttpHost(proxy.ip(), proxy.port(), "http");
            config.setProxy(pxy);
            Log.debug("this fetch is going to use proxy {}", proxy.toString());
        }
        httpget.setConfig(config.build());
    }

    void doFetch(ProxyObject proxy) {
        if (page > MAX_PAGE) {
            return;
        }
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {
            HttpGet httpget = new HttpGet(user.weiboUrl() + page);
            useProxy(proxy, httpget);
            httpget.addHeader(BrowserProvider.getRandBrowserAgent());
            CloseableHttpResponse response = httpClient.execute(httpget);
            Log.debug("going to parse page..." + page);
            List<Document> documents = parser.parserWeiboContent(response.getEntity());
            boolean synced = syncWithCache(documents);
            if (proxy != null) {
                Log.debug("good proxy {}", proxy.toString());
            }
            if (!synced) {
                page++;
                Thread.sleep(100);
                doFetch(null);

            } else {
                return;
            }
        } catch (Exception e) {
            Log.error(" fetch {} fail {}", user.getScreenName(), user.weiboUrl() + page);
            Log.error(e.getMessage());
            if (retry == 0) {
                return;
            }
            if (proxy != null) {
                cache.removeProxy(proxy);
            }
            retry--;
            Log.info("{} retry ...", (3 - retry));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            doFetch(null);
        }
    }


    public boolean syncWithCache(List<Document> documents) {
        boolean found = false;
        for (Document d : documents) {
            if (d == null || d.getString("id") == null) {
                continue;
            }
            if (d.getString("id").equalsIgnoreCase(user.getLastPostId())) {
                found = true;
                break;
            } else {
                weibos.add(d);
            }
        }
        return found;
    }


    public Tuple<Integer, CrawUserInfo> call() throws Exception {
        Log.debug(" fetch for user " + user.getScreenName());
        //doFetch(cache.randomProxy());
        doFetch(null);
        if (weibos.size() > 0) {
            user.setLastPostId(weibos.get(0).getString("id"));
            user.setLastFetchTime(Calendar.getInstance().getTime());
            dao.bulkInsert(weibos);
        }
        return new Tuple<>(weibos.size(), user);
    }
}
