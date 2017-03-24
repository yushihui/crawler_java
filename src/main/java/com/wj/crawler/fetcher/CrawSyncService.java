package com.wj.crawler.fetcher;

import com.wj.crawler.common.CacheManager;
import com.wj.crawler.db.orm.UserCrawInfoDAO;
import com.wj.crawler.db.orm.WeiboUserDAO;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by SYu on 3/23/2017.
 */
public class CrawSyncService {

    private final UserCrawInfoDAO uciDAO;
    private final WeiboUserDAO userDAO;


    private final CacheManager cacheManager;

    private static final Logger Log = LoggerFactory.getLogger(WeiboContenFetcher.class);

    @Inject
    public CrawSyncService(UserCrawInfoDAO uciDAO, WeiboUserDAO userDAO, CacheManager cacheManager) {
        this.uciDAO = uciDAO;
        this.userDAO = userDAO;
        this.cacheManager = cacheManager;

    }


    public void syncCrawInfoWithUser() {
        cacheManager.initUserCache();
        Log.debug("start merging ... ");
        List<Document> allusers = userDAO.getUserBasicInfos();
        uciDAO.UpSertDocuments(allusers);
        Log.debug("merge finished");
    }
}
