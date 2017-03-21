package com.wj.crawler.fetcher;

/**
 * Created by SYu on 3/15/2017.
 */
public abstract class AbstractFetcher {

    protected  String URL_PREFIX ="";

    abstract void fetchPage(int page);


    public void doFetch(int from, int to, int freq) {
        for (int i = from; i < to; i++) {
            fetchPage(i);
            try {
                Thread.sleep(freq * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
