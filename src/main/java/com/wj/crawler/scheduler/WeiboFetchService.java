package com.wj.crawler.scheduler;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by SYu on 3/21/2017.
 */
public class WeiboFetchService extends AbstractScheduledService {

    private static final Logger Log = LoggerFactory.getLogger(WeiboFetchService.class);

    @Inject Properties config;

    private int hours = 1;

    protected void startUp() throws Exception {// get
        hours = Integer.parseInt(config.getProperty("weibo.freq", "6"));
    }

    protected void runOneIteration() throws Exception {

    }

    protected void shutDown() throws Exception {

    }

    protected Scheduler scheduler() {

        return Scheduler.newFixedRateSchedule(0, hours, TimeUnit.HOURS);
    }

    protected String serviceName() {
        return "Fetch_Weibo_Content_Service";
    }

}