package com.wj.crawler.common;

import java.util.concurrent.TimeUnit;

/**
 * Created by Syu on 3/28/2017.
 */
public class TimeUtils {


    //todo add an example here
    public static String Mills2PrettyString(long millis) {
        long hours = TimeUnit.HOURS.convert(millis, TimeUnit.MILLISECONDS);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        return String.format("%02d hour, %02d min, %02d sec",
                hours, minutes, seconds);
    }
}
