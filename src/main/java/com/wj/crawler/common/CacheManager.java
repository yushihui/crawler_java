package com.wj.crawler.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Queues;
import com.wj.crawler.db.orm.CrawUserInfo;
import com.wj.crawler.db.orm.ProxyDAO;
import com.wj.crawler.db.orm.UserCrawInfoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by SYu on 3/22/2017.
 */

public class CacheManager {

    private final UserCrawInfoDAO userDao;
    private final ProxyDAO proxyDao;

    private static final Logger Log = LoggerFactory.getLogger(CacheManager.class);

    @Inject
    public CacheManager(UserCrawInfoDAO userDao, ProxyDAO proxyDao) {
        this.userDao = userDao;
        this.proxyDao = proxyDao;
        initUserCache();
    }

    private Cache<String, ProxyObject> proxyCache = CacheBuilder.newBuilder()
            .maximumSize(200000)
            .build();


    private Cache<String, CrawUserInfo> userCache = CacheBuilder.newBuilder()
            .maximumSize(200000)
            .build();


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


    public void initProxyCache() {
        Iterator<ProxyObject> it = proxyDao.loadProxy().iterator();
        while (it.hasNext()) {
            ProxyObject proxy = it.next();
            proxyCache.put(proxy.ip(), proxy);
        }
    }

    public void removeUser(String userId) {
        userCache.invalidate(userId);
    }

    public void addUser(CrawUserInfo user) {
        userCache.put(user.getUserId(), user);
    }


}
