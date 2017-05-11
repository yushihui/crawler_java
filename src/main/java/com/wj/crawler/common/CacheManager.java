package com.wj.crawler.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Queues;
import com.google.gson.Gson;
import com.wj.crawler.db.orm.CrawUserInfo;
import com.wj.crawler.db.orm.IndexDAO;
import com.wj.crawler.db.orm.ProxyDAO;
import com.wj.crawler.db.orm.UserCrawInfoDAO;
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

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by SYu on 3/22/2017.
 */

public class CacheManager {

    private final UserCrawInfoDAO userDao;
    private final ProxyDAO proxyDao;
    private final IndexDAO indexDao;

    private static final Logger Log = LoggerFactory.getLogger(CacheManager.class);

    private static final String PROXY_URL = "http://dev.kuaidaili.com/api/getproxy/?orderid=909394638192958&num=100&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=2&an_an=1&an_ha=1&sp2=1&quality=1&format=json&sep=1";

    @Inject
    public CacheManager(UserCrawInfoDAO userDao, ProxyDAO proxyDao, IndexDAO indexDAO) {
        this.userDao = userDao;
        this.proxyDao = proxyDao;
        this.indexDao = indexDAO;
        initUserCache();
        initIndexCache();
    }

    private Cache<String, List<ProxyObject>> proxyCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .refreshAfterWrite(100, TimeUnit.MINUTES)
            //.removalListener(MY_LISTENER)
            .build(
                    new CacheLoader<String, List<ProxyObject>>() {
                        public List<ProxyObject> load(String key) throws Exception {
                            return createProxise(key);
                        }
                    });


    private List<ProxyObject> createProxise(String url) {
        List<ProxyObject> result = new ArrayList<>();
        InputStream input = null;
        try {
            input = new URL(url).openStream();
            Reader reader = new InputStreamReader(input, "UTF-8");
            ProxyKuaidaili content = new Gson().fromJson(reader, ProxyKuaidaili.class);
            ProxyContent pContent = content.getData();
            List<String> proxties_str = pContent.getProxy_list();
            proxties_str.forEach(ps -> {
                String pair[] = ps.split(":");
                result.add(ProxyObject.create(pair[0], Integer.parseInt(pair[1]), true));
            });
            Log.debug("{} proxy had been created", result.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }


    private Cache<String, CrawUserInfo> userCache = CacheBuilder.newBuilder()
            .maximumSize(200000)
            .build();

    private Cache<String, Date> indexCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build();


    private void updateProxy(List<ProxyObject> proxyObjects) {
        int i = 0;
        do {
            proxyObjects.add(null);
            i++;
        } while (i < 3);
        proxyCache.put(PROXY_URL, proxyObjects);
    }


    public List<ProxyObject> getProxies() {
        List<ProxyObject> proxyObjects = null;
        try {
            return proxyCache.get(PROXY_URL, new Callable<List<ProxyObject>>() {
                @Override
                public List<ProxyObject> call() throws Exception {

                    return createProxise(PROXY_URL);
                }
            });
        } catch (ExecutionException e) {

        }
        return proxyObjects;
    }

    public void removeProxy(ProxyObject proxy) {
        List<ProxyObject> ps = getProxies();
        ps.removeIf(proxyObject -> proxyObject.ip() == proxy.ip());
    }

    public void removeProxy(List<ProxyObject> proxy) {
        List<ProxyObject> ps = getProxies();
        ps.removeAll(proxy);
    }

    public ProxyObject randomProxy() {
        List<ProxyObject> ps = getProxies();
        if (ps == null || ps.size() == 0) {
            return null;
        }
        return ps.get(new Random().nextInt(ps.size()));
    }


    public Cache<String, Date> getIndexCache() {
        return indexCache;
    }


    public PriorityBlockingQueue<CrawUserInfo> getWaitingUsers() {
        Log.debug("cached users size:{}", userCache.size());
        return Queues.newPriorityBlockingQueue(userCache.asMap().values());
    }

    public void testProxy() {
        List<ProxyObject> ps = getProxies();
        List<ProxyObject> goodPx = new ArrayList<>();
        for (ProxyObject proxy : ps) {
            CloseableHttpClient httpClient = HttpClients.custom().build();
            try {
                HttpGet httpget = new HttpGet("http://m.weibo.cn/container/getIndex?type=uid&value=1748520075&containerid=1076031748520075&page=1");
                if (proxy != null) {
                    useProxy(proxy, httpget);
                    Log.debug("this fetch is going to use proxy {}", proxy.toString());
                }
                httpget.addHeader(BrowserProvider.getRandBrowserAgent());
                CloseableHttpResponse response = httpClient.execute(httpget);
                List<Document> documents = new WeiboParser().parserWeiboContent(response.getEntity());
                if (documents.size() == 0) {
                    Log.debug(" proxy {} test fail", proxy.toString());

                } else {
                    Log.debug(" {} :success ", proxy.toString());
                    goodPx.add(proxy);
                    if (goodPx.size() > 12) {
                        Log.debug(" OK, enough proxy.");
                        updateProxy(goodPx);
                        return;
                    }
                }

            } catch (Exception e) {
                Log.debug(" proxy {} test fail", proxy.toString());


            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.debug("good proxy size: {}", getProxies().size());

    }

    private void useProxy(ProxyObject proxy, HttpGet httpget) {
        HttpHost pxy = new HttpHost(proxy.ip(), proxy.port(), "http");
        RequestConfig config = RequestConfig.custom()
                .setProxy(pxy)
                .build();
        httpget.setConfig(config);
    }


    public void initUserCache() {
        Iterator<CrawUserInfo> it = userDao.loadCrawStatus().iterator();
        while (it.hasNext()) {
            CrawUserInfo user = it.next();
            userCache.put(user.getUserId(), user);
        }
    }


    public void initIndexCache() {
        Iterable<Tuple<String, Date>> it = indexDao.loadMongoIndex();
        it.forEach(t -> indexCache.put(t.t, t.n));
    }

    public void addUser(CrawUserInfo user) {
        userCache.put(user.getUserId(), user);
    }


}
