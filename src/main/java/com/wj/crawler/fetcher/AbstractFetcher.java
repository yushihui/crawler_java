package com.wj.crawler.fetcher;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by SYu on 3/15/2017.
 */
public abstract class AbstractFetcher {

    @Inject
    protected Gson gson;

    abstract void doFetch();


}
