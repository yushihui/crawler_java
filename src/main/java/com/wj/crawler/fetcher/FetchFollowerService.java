package com.wj.crawler.fetcher;

import com.wj.crawler.db.orm.UserCrawInfoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shihui on 9/2/2017.
 */
public class FetchFollowerService {

    private final UserCrawInfoDAO uciDAO;
    private final UserFollowerFetcher uff;


    private static final Logger Log = LoggerFactory.getLogger(FetchFollowerService.class);

    @Inject
    public FetchFollowerService(UserCrawInfoDAO uciDAO ,UserFollowerFetcher uff) {
        this.uciDAO = uciDAO;
        this.uff = uff;
    }

    public void doFetching(){
        List<String> uids = new ArrayList<>();
        uciDAO.loadUnFollowUsers().forEach(user -> {
            uids.add(user.getUserId());

        });

        Log.info("users need to fetch..." + uids.size());
        uids.forEach(id ->{
            uff.doFetchUser(id);
            uciDAO.UpdateFollowFetchStatus(id);

        });
    }
}
