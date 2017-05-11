package com.wj.crawler.common;

import com.google.auto.value.AutoValue;

/**
 * Created by SYu on 3/22/2017.
 */

@AutoValue
public abstract class ProxyObject {
    public static ProxyObject create(String ip, int port, boolean available) {
        return new AutoValue_ProxyObject(ip, port, available);
    }

    public abstract String ip();

    public abstract int port();

    public abstract boolean IsAvailable();

    public String toString() {
        return ip() + ":" + port();
    }

}