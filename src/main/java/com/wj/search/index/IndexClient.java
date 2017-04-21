package com.wj.search.index;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by Syu on 4/20/2017.
 */
public class IndexClient {

    private TransportClient client;
    private static final Logger Log = LoggerFactory.getLogger(IndexClient.class);


    public IndexClient(Properties config){

        String elastic_ip = config.getProperty("elastic.ip");
        int port = Integer.parseInt(config.getProperty("elastic.ip","9200"));

        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elastic_ip), port));
        } catch (UnknownHostException e) {
            Log.error("initial elastic client failed.");
            e.printStackTrace();
        }


    }


    public TransportClient getClient(){
        return client;
    }

}
