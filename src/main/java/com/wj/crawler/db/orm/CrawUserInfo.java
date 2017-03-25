package com.wj.crawler.db.orm;

import com.wj.crawler.fetcher.CrawSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by SYu on 3/22/2017.
 */
public class CrawUserInfo implements Comparable {

    private String userId;
    private String containerId;
    private Date lastFetchTime;
    private Date lastPostTime;
    private String lastPostId;
    private String screenName;
    private int followersCount;

    private final static String URL_FORMAT = "http://m.weibo.cn/container/getIndex?type=uid&value=%s&containerid=%s&page=";

    private static final Logger Log = LoggerFactory.getLogger(CrawSyncService.class);

    public CrawUserInfo() {

    }


    public CrawUserInfo(String userId, String containerId, Date lastFetchTime, Date lastPostTime,
                        String lastPostId, String screenName, int followersCount) {
        this.userId = userId;
        this.containerId = containerId;
        this.lastFetchTime = lastFetchTime;
        this.lastPostTime = lastPostTime;
        this.lastPostId = lastPostId;
        this.screenName = screenName;
        this.followersCount = followersCount;
    }

    public CrawUserInfo(String userId, String screenName, int followersCount) {
        this.userId = userId;
        this.screenName = screenName;
        this.followersCount = followersCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String weiboUrl() {

        return String.format(URL_FORMAT, this.userId, this.containerId + this.userId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Date getLastFetchTime() {
        return lastFetchTime;
    }

    public void setLastFetchTime(Date lastFetchTime) {
        this.lastFetchTime = lastFetchTime;
    }

    public Date getLastPostTime() {
        return lastPostTime;
    }

    public void setLastPostTime(Date lastPostTime) {
        this.lastPostTime = lastPostTime;
    }

    public String getLastPostId() {
        return lastPostId;
    }

    public void setLastPostId(String lastPostId) {
        this.lastPostId = lastPostId;
    }

    public int compareTo(Object o) {

        if (o instanceof CrawUserInfo) {
            CrawUserInfo that = (CrawUserInfo) o;
            return this.followersCount - that.getFollowersCount();
        } else {
            Log.error("this is not a valid comparision");
        }

        return 0; //todo this might be a problem
    }
}
