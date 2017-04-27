package com.wj.crawler.scheduler;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

/**
 * Created by Syu on 4/4/2017.
 */
public class WeiboCustomerSchduler extends AbstractScheduledService.CustomScheduler {

    private static final Logger Log = LoggerFactory.getLogger(WeiboCustomerSchduler.class);

    private boolean flag = true;

    public WeiboCustomerSchduler() {

    }

    @Override
    /**
     * try best to ignore doing fetch during midnight(0 - 7:00 AM) or just fetch inactive users during this period(@todo)
     */
    protected Schedule getNextSchedule() throws Exception {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        int curHour = now.getHour();
        int next_hours = 2;
        if (curHour >= 0 && curHour < 7) {
            next_hours = 7 - curHour;
        }
        if (flag) {
            flag = false;
            return new Schedule(10, TimeUnit.SECONDS);
        }

        Log.info(" next round is going to run in {} hours later", next_hours);
        //return new Schedule(next_hours, TimeUnit.MINUTES);
        return new Schedule(next_hours, TimeUnit.HOURS);
    }
}
