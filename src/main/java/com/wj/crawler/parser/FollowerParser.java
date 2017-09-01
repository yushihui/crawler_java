package com.wj.crawler.parser;

import org.apache.http.HttpEntity;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/14/2017.
 */
public class FollowerParser {


    private static final Logger Log = LoggerFactory.getLogger(WeiboParser.class);


    public FollowerParser() {

    }

    public List<Document> parseWeiboUser(HttpEntity entity) {
        List<Document> users = new ArrayList<Document>();
        String content = readContent(entity);
        Document document = Document.parse(content);
        List<Document> documents = (List<Document>) document.get("cards");
        documents.forEach(
                bo -> users.add((Document) (bo.get("user")))
        );
        return users;

    }

    private String readContent(HttpEntity entity) {
        StringBuffer result = new StringBuffer();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String ret = result.toString();
        Log.debug("response content: " + ret);
        return ret;
    }




}
