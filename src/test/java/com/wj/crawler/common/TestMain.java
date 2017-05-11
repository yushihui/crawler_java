package com.wj.crawler.common;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 5/5/2017.
 */
public class TestMain {


    public static void main(String args[]){
        createProxise("http://dev.kuaidaili.com/api/getproxy/?orderid=909394638192958&num=10&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=2&an_an=1&an_ha=1&sp2=1&quality=1&format=json&sep=1");
    }

    private static List<ProxyObject> createProxise(String url){
        List<ProxyObject> result = new ArrayList<>();
        InputStream input = null;
        try {
            input = new URL(url).openStream();
            Reader reader = new InputStreamReader(input, "UTF-8");
            ProxyKuaidaili content = new Gson().fromJson(reader,ProxyKuaidaili.class);
            ProxyContent pContent = content.getData();
            List<String> proxties_str = pContent.getProxy_list();
            proxties_str.forEach(ps -> {
                String pair[] = ps.split(":");
                result.add(ProxyObject.create(pair[0],Integer.parseInt(pair[1]),true));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
}
