package com.wj.crawler.fetcher;

import com.wj.crawler.common.ProxyCache;
import com.wj.crawler.common.ProxyObject;
import com.wj.crawler.db.orm.UserCrawInfoDAO;
import com.wj.crawler.parser.FollowerParser;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 9/30/2017.
 */
public class UserInfoFetcher extends AbstractFetcher{

    private CloseableHttpClient httpClient;
    private final UserCrawInfoDAO userDAO;
    private final FollowerParser parser;
    private final List<Header> headers;
    private final ProxyCache cache;
    private static final Logger Log = LoggerFactory.getLogger(UserFollowerFetcher.class);

    @Inject
    public UserInfoFetcher(CloseableHttpClient httpClient, UserCrawInfoDAO dao, FollowerParser parser, ProxyCache cache
            ,List<Header> headers) {
        this.httpClient = httpClient;
        this.userDAO = dao;
        this.parser = parser;
        this.headers = headers;
        this.URL_PREFIX = SinaUrl.FOLLOWERS_URL;
        this.cache = cache;
    }


    private void useProxy(HttpGet httpget, ProxyObject proxy) {

        RequestConfig.Builder config = RequestConfig.custom().setConnectTimeout(6 * 1000);
        if (proxy != null) {

            HttpHost pxy = new HttpHost(proxy.ip(), proxy.port(), "http");
            config.setProxy(pxy);
            Log.debug("this fetch is going to use proxy {}", proxy.toString());
        }
        httpget.setConfig(config.build());
    }

    @Override
    void fetchPage(int page) {

    }

    String fetchUserInfo(String url, boolean useProxyF){
        String address = null;
        httpClient = HttpClients.custom().build();
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.setHeaders(headers.toArray(new Header[headers.size()]));
            if(useProxyF){
                useProxy( httpget, cache.randomProxy());
            }

            //httpget.addHeader(BrowserProvider.getRandBrowserAgent());
            CloseableHttpResponse response = httpClient.execute(httpget);
            Log.info("going to parse page..." + url);
            return parser.parseUserAddress(response.getEntity());

        } catch (Exception e) {

            try {
                httpClient.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            httpClient = HttpClients.custom().build();
            e.printStackTrace();
        }
        return address;
    }

    @Override
    boolean fetchPage(String url, boolean useProxyF) {
        httpClient = HttpClients.custom().build();
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.setHeaders(headers.toArray(new Header[headers.size()]));
            if(useProxyF){
                useProxy( httpget, cache.randomProxy());
            }

            //httpget.addHeader(BrowserProvider.getRandBrowserAgent());
            CloseableHttpResponse response = httpClient.execute(httpget);
            Log.info("going to parse page..." + url);
            String address = parser.parseUserAddress(response.getEntity());
            Log.info("going to save to db...address is : "+ address);

            Log.info("done success!!!");

        } catch (Exception e) {

            try {
                httpClient.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            httpClient = HttpClients.custom().build();
            e.printStackTrace();
        }
        return true;
    }

}
