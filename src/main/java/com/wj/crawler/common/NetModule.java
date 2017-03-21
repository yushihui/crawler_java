package com.wj.crawler.common;

import com.google.common.collect.Lists;
import dagger.Module;
import dagger.Provides;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;

import javax.inject.Singleton;
import java.util.List;
import java.util.Properties;

/**
 * Created by SYu on 3/14/2017.
 */
@Module(includes = ConfigModule.class)
public class NetModule {

    @Provides
    @Singleton
    RequestConfig providerHttpClientConfig() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setExpectContinueEnabled(true)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        return defaultRequestConfig;
    }

    @Provides
    @Singleton
    HttpClientContext providerHttpClientContext(CookieStore cookie) {
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookie);
        return context;
    }

    @Provides
    @Singleton
    List<Header> providerHttpHeaders(Properties config) {

        Header header = new BasicHeader(
                HttpHeaders.CONTENT_TYPE, "application/json");
        Header headerConnection = new BasicHeader(
                HttpHeaders.CONNECTION, "keep-alive");
        Header headerHost = new BasicHeader(
                HttpHeaders.HOST, "m.weibo.cn");
        Header headerSecure = new BasicHeader(
                HttpHeaders.REFERER, "http://m.weibo.cn/");

        String cookie = config.getProperty("weibo.cookie");
        Header headerCookie = new BasicHeader(
                "Cookie", cookie);

        Header headerAgent = new BasicHeader(
                HttpHeaders.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");

        return Lists.newArrayList(header,headerAgent,headerCookie);

    }

    @Provides
    @Singleton
    public CloseableHttpClient providerHttpClient(CookieStore cookieStore, RequestConfig defaultRequestConfig) {

        CloseableHttpClient httpclient = HttpClients.custom()
                //.setDefaultCookieStore(cookieStore)
                //.setDefaultHeaders(headers)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        return httpclient;
    }


    @Provides
    @Singleton
    CookieStore providerCookieStore(Properties config) {
        CookieStore cookies = new BasicCookieStore();
        String cookieString = config.getProperty("weibo.cookie");
        String[] cookieArray = cookieString.split(";");
        for (String ck : cookieArray) {
            String[] pare = ck.split("=");
            BasicClientCookie cook = new BasicClientCookie(pare[0].trim(), pare[1]);
            cook.setDomain(".weibo.cn");
            cook.setPath("/");

            cookies.addCookie(cook);
        }
        return cookies;

    }

}
