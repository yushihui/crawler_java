package com.wj.crawler.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wj.crawler.db.orm.CrawUserInfo;
import com.wj.crawler.db.orm.UserCrawInfoDAO;

import javax.inject.Inject;
import java.util.Iterator;

/**
 * Created by SYu on 3/22/2017.
 */

public class CacheManager {

    private final UserCrawInfoDAO userDao;

    @Inject
    public CacheManager(UserCrawInfoDAO userDao) {
        this.userDao = userDao;
        initUserCache();
    }

    private Cache<String, CrawUserInfo> proxyCache = CacheBuilder.newBuilder()
            .maximumSize(200000)
            .build();


    private Cache<String, CrawUserInfo> userCache = CacheBuilder.newBuilder()
            .maximumSize(200000)
            .build();


    public void initUserCache() {
        Iterable<CrawUserInfo> users = userDao.loadCrawStatus();
        Iterator<CrawUserInfo> it = users.iterator();
        while (it.hasNext()) {
            CrawUserInfo user = it.next();
            userCache.put(user.getUserId(), user);
        }
    }


}
