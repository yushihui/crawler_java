package com.wj.crawler.common;

import com.google.auto.value.AutoValue;

/**
 * Created by Administrator on 3/22/2017.
 */

@AutoValue
public abstract class ProxyObject {
    static ProxyObject create(String ip, int port) {
        return new AutoValue_ProxyObject(ip, port);
    }

    abstract String ip();
    abstract int port();

}