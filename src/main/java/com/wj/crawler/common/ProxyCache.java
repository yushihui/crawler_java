package com.wj.crawler.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 9/18/2017.
 */
public class ProxyCache {

    private final Properties config;

    private static final Logger Log = LoggerFactory.getLogger(ProxyCache.class);
    private static final String PROXY_STABLE = "stable_proxy";

    public ProxyCache(Properties config){
        this.config = config;
    }


    private Cache<String, List<ProxyObject>> proxyCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .refreshAfterWrite(100, TimeUnit.MINUTES)
            //.removalListener(MY_LISTENER)
            .build(
                    new CacheLoader<String, List<ProxyObject>>() {
                        public List<ProxyObject> load(String key) throws Exception {
                            return createProxise();
                        }
                    });



    private List<ProxyObject> createProxise() {
        List<ProxyObject> result = new ArrayList<>();
        String proxy_str = config.getProperty("weibo.proxy");
        String []pxys = proxy_str.split(";");
        if(pxys.length > 0){

        }
        for(String ps : pxys){
            String pair[] = ps.split(":");
            result.add(ProxyObject.create(pair[0], Integer.parseInt(pair[1]), true));
        }
        return result;

    }


    public List<ProxyObject> getProxies() {
        List<ProxyObject> proxyObjects = null;
        try {
            return proxyCache.get(PROXY_STABLE, new Callable<List<ProxyObject>>() {
                @Override
                public List<ProxyObject> call() throws Exception {

                    return createProxise();
                }
            });
        } catch (ExecutionException e) {

        }
        return proxyObjects;
    }

    public ProxyObject randomProxy() {
        List<ProxyObject> ps = getProxies();
        if (ps == null || ps.size() == 0) {
            return null;
        }
        return ps.get(new Random().nextInt(ps.size()));
    }
}
