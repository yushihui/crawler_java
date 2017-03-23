package com.wj.crawler.fetcher;

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

    private static final Logger Log = LoggerFactory.getLogger(WeiboContenFetcher.class);

    @Inject
    public CrawSyncService(UserCrawInfoDAO uciDAO, WeiboUserDAO userDAO) {
        this.uciDAO = uciDAO;
        this.userDAO = userDAO;

    }


    public void syncCrawInfoWithUser() {
        Log.debug("start merging ... ");
        List<Document> allusers = userDAO.getUserBasicInfos();
        uciDAO.UpSertDocuments(allusers);
        Log.debug("merge finished");
    }
}
