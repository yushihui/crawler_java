package com.wj.crawler.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Queues;
import com.wj.crawler.db.orm.CrawUserInfo;
import com.wj.crawler.db.orm.ProxyDAO;
import com.wj.crawler.db.orm.UserCrawInfoDAO;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by SYu on 3/22/2017.
 */

public class CacheManager {

    private final UserCrawInfoDAO userDao;
    private final ProxyDAO proxyDao;

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


    private PriorityBlockingQueue<CrawUserInfo> waitingUsers;

    public void initWaitingUsers() {
        waitingUsers = Queues.newPriorityBlockingQueue(userCache.asMap().values());
    }

    public void getNextWaitingUsers(Collection<CrawUserInfo> target, int size){
        this.waitingUsers.drainTo(target,size);
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
