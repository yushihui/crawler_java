package com.wj.crawler.common;

import java.util.List;

/**
 * Created by Syu on 5/5/2017.
 */
public class ProxyKuaidaili {
    private String msg;
    private int code;
    private ProxyContent data;

    public ProxyKuaidaili(String msg, int code, ProxyContent data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ProxyContent getData() {
        return data;
    }

    public void setData(ProxyContent data) {
        this.data = data;
    }
}

class ProxyContent{

    private int count;
    private List<String> proxy_list;


    public ProxyContent(int count, List<String> proxy_list) {
        this.count = count;
        this.proxy_list = proxy_list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getProxy_list() {
        return proxy_list;
    }

    public void setProxy_list(List<String> proxy_list) {
        this.proxy_list = proxy_list;
    }
}
