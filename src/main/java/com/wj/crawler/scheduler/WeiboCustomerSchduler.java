package com.wj.crawler.scheduler;

import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 4/4/2017.
 */
public class WeiboCustomerSchduler extends AbstractScheduledService.CustomScheduler {

    private int nextHour;


    public WeiboCustomerSchduler(int hours){
        nextHour = hours;
    }

    @Override
    protected Schedule getNextSchedule() throws Exception {
        return new Schedule(nextHour, TimeUnit.HOURS);
    }
}
