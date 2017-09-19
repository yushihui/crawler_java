package com.wj.crawler.fetcher;

/**
 * Created by SYu on 3/15/2017.
 */
public abstract class AbstractFetcher {

    protected String URL_PREFIX = "";

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

    abstract boolean fetchPage(String url, boolean useProxy);

    public void doFetchFollower(String uid, int from, int to, int freq) {
        for (int i = from; i < to; i++) {
            String url = String.format(SinaUrl.FOLLOWERS_URL, uid, i);
            boolean flag = fetchPage(url, i % 4 == 1);
            if (!flag) {
                break;
            }
            try {
                Thread.sleep(freq * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
