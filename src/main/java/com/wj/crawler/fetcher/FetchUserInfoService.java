package com.wj.crawler.fetcher;

import com.wj.crawler.db.orm.UserCrawInfoDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/1/2017.
 */
public class FetchUserInfoService {


    private final UserCrawInfoDAO uciDAO;
    private final UserInfoFetcher uif;


    private static final Logger Log = LoggerFactory.getLogger(FetchFollowerService.class);

    @Inject
    public FetchUserInfoService(UserCrawInfoDAO uciDAO, UserInfoFetcher uif) {
        this.uciDAO = uciDAO;
        this.uif = uif;
    }

    public void doFetching() {
        List<String> uids = new ArrayList<>();
        uciDAO.loadNoAddrUsers().forEach(user -> {
            uids.add(user.getUserId());

        });

        Log.info("users need to fetch..." + uids.size());

        uids.forEach(id -> {
            String address = uif.fetchUserInfo(String.format(SinaUrl.USER_INFO_URL, id), false);
            if (address == null || address.isEmpty()) {
                return;
            }
            uciDAO.UpdateUserAddr(id, address);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }
}
