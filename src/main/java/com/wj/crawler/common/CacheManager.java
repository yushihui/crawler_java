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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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

    private static final String PROXY_URL = "http://dev.kuaidaili.com/api/getproxy/?orderid=909394638192958&num=10&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=2&an_an=1&an_ha=1&sp2=1&quality=1&format=json&sep=1";

    @Inject
    public CacheManager(UserCrawInfoDAO userDao, ProxyDAO proxyDao, IndexDAO indexDAO) {
        this.userDao = userDao;
        this.proxyDao = proxyDao;
        this.indexDao = indexDAO;
        initUserCache();
        initIndexCache();
    }

    private Cache<String, List<ProxyObject>> proxyCache =  CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            //.removalListener(MY_LISTENER)
            .build(
                    new CacheLoader<String, List<ProxyObject>>() {
                        public List<ProxyObject> load(String key) throws Exception {
                            return createProxise(key);
                        }
                    });


    private List<ProxyObject> createProxise(String url){
        List<ProxyObject> result = new ArrayList<>();
        InputStream input = null;
        try {
            input = new URL(url).openStream();
            Reader reader = new InputStreamReader(input, "UTF-8");
            ProxyKuaidaili content = new Gson().fromJson(reader,ProxyKuaidaili.class);
            ProxyContent pContent = content.getData();
            List<String> proxties_str = pContent.getProxy_list();
            proxties_str.forEach(ps -> {
                String pair[] = ps.split(":");
                result.add(ProxyObject.create(pair[0],Integer.parseInt(pair[1]),true));
            });
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


    public List<ProxyObject> getProxies(){
        return proxyCache.getIfPresent(PROXY_URL);
    }


    public Cache<String, Date> getIndexCache(){
        return indexCache;
    }


    public PriorityBlockingQueue<CrawUserInfo> getWaitingUsers() {
        Log.debug("cached users size:{}", userCache.size());
        return Queues.newPriorityBlockingQueue(userCache.asMap().values());
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
