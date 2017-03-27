package com.wj.crawler.scheduler;

import com.google.common.util.concurrent.*;
import com.wj.crawler.common.CacheManager;
import com.wj.crawler.common.Tuple;
import com.wj.crawler.db.orm.CrawUserInfo;
import com.wj.crawler.db.orm.UserCrawInfoDAO;
import com.wj.crawler.db.orm.WeiboDAO;
import com.wj.crawler.parser.WeiboParser;
import com.wj.crawler.service.fetch.WeiboCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by SYu on 3/21/2017.
 */
public class WeiboFetchService extends AbstractScheduledService {

    private static final Logger Log = LoggerFactory.getLogger(WeiboFetchService.class);

    private ListeningExecutorService service;

    private final Properties config;
    private final WeiboDAO dao;
    private final UserCrawInfoDAO crawDao;
    private final WeiboParser parser;
    private final CacheManager cache;

    private int hours = 1;

    private Callable<Boolean> usageComputation =
            new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    Log.info("{}: this round is done",serviceName());
                    return true;
                }
            };

    @Inject
    public WeiboFetchService(Properties config, WeiboDAO dao, WeiboParser parser, CacheManager cache, UserCrawInfoDAO crawDao) {
        this.dao = dao;
        this.parser = parser;
        this.config = config;
        this.cache = cache;
        this.crawDao = crawDao;
    }

    protected void startUp() throws Exception {// get
        hours = Integer.parseInt(config.getProperty("weibo.freq", "6"));
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));
    }

    protected void runOneIteration() throws Exception {
        PriorityBlockingQueue<CrawUserInfo> users = cache.getWaitingUsers();
        int maxCount = 10;
        int count = 0;
        List<ListenableFuture<Tuple<Integer, CrawUserInfo>>> futures = new ArrayList();

        while (true) {
            if (users.isEmpty()) {
                Log.info("all fetch worker started");
                break;
            }
            if (count == maxCount) {
                count = 0;
                Thread.sleep(100 * 1000);
            }
            CrawUserInfo user = users.take();
            if (user != null) {
                ListenableFuture<Tuple<Integer, CrawUserInfo>> fetcher = service.submit(new WeiboCrawler(dao, parser, user));
                futures.add(fetcher);
                Futures.addCallback(fetcher, new FutureCallback<Tuple<Integer, CrawUserInfo>>() {
                    public void onSuccess(Tuple<Integer, CrawUserInfo> result) {
                        //it might be better handle (parse/save to db) here
                        Log.info("{}: crawler fetched --{} tweets.", result.n.getScreenName(), result.t);

                        if (result.t > 0) {
                            cache.addUser(result.n);//update cache
                            crawDao.updateOneDoc(result.n);

                        }
                    }

                    public void onFailure(Throwable thrown) {
                        Log.info(" fetch worker fail");
                    }
                });

                count++;
            }

        }

         Futures.whenAllComplete(futures).call(usageComputation);
    }

    protected void shutDown() throws Exception {
        service.shutdown();
    }

    protected Scheduler scheduler() {

        return Scheduler.newFixedRateSchedule(0, hours, TimeUnit.HOURS);
    }

    protected String serviceName() {
        return "Fetch_Weibo_Content_Service";
    }

}