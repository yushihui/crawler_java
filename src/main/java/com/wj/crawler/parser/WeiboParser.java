package com.wj.crawler.parser;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.wj.crawler.fetcher.WeiboUserFetcher;
import org.apache.http.HttpEntity;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SYu on 3/16/2017.
 */
public class WeiboParser {
    private static final Logger Log = LoggerFactory.getLogger(WeiboParser.class);


    public WeiboParser() {

    }

    public List<Document> fromEntity2Document(HttpEntity entity) {

        List<Document> users = new ArrayList<Document>();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            Log.info("response: "+result.toString());
            Document document = Document.parse(result.toString());
            List<Document> documents = (List<Document>) document.get("cards");

            for (Document bo : documents) {
                users.add((Document) (bo.get("user")));//todo if followers < 100k we could ignore
            }

        } catch (Exception e) {

        }
        return users;

    }

}
