package com.wj.crawler.parser;

import com.wj.crawler.common.Exceptions.FetchNotFoundException;
import org.apache.http.HttpEntity;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by SYu on 3/16/2017.
 */
public class WeiboParser {
    private static final Logger Log = LoggerFactory.getLogger(WeiboParser.class);


    public WeiboParser() {

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


    public List<Document> parserWeiboContent(HttpEntity entity) throws FetchNotFoundException {
        List<Document> wbs = new ArrayList<Document>();
        String content = readContent(entity);
        Document document = Document.parse(content);
        List<Document> documents = (List<Document>) document.get("cards");
        Stream<Document> parallelStream = documents.parallelStream();
        parallelStream.forEach(d -> {
            Document tweet = (Document) (d.get("mblog"));
            if (tweet == null) {
            } else {
                tweet.append("f_time", new Date());
                //tweet.remove("user");// todo add fetch time and remove user info
                wbs.add(tweet);
            }

        });
        if (wbs.size() == 0) {
            throw new FetchNotFoundException("page not found.");
        }
        return wbs;

    }

}
