package com.wj.crawler.db.orm;

import java.util.Date;

/**
 * Created by Administrator on 3/22/2017.
 */
public class CrawUserInfo {

    private String userId;
    private String containerId;
    private Date lastFetchTime;
    private Date lastPostTime;
    private String lastPostId;

    private final static String URL_FORMAT = "http://m.weibo.cn/container/getIndex?type=uid&value=%s&containerid=%s&page=";


    public CrawUserInfo(String userId, String containerId, Date lastFetchTime, Date lastPostTime, String lastPostId) {
        this.userId = userId;
        this.containerId = containerId;
        this.lastFetchTime = lastFetchTime;
        this.lastPostTime = lastPostTime;
        this.lastPostId = lastPostId;
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
}
