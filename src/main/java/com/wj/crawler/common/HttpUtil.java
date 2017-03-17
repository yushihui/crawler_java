package com.wj.crawler.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.http.HttpEntity;
import org.bson.Document;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SYu on 3/15/2017.
 */
public class HttpUtil {


    static Gson gson = new Gson();

    public static JsonObject fromString2Json(HttpEntity entity) {
        JsonObject json = null;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            json = gson.fromJson(result.toString(), JsonObject.class);

        } catch (Exception e) {

        }
        return json;

    }

    public static List<BasicDBObject> fromString2Document(HttpEntity entity) {

        List<BasicDBObject> users = new ArrayList<BasicDBObject>();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            DBObject dbObject = (DBObject)JSON.parse(result.toString());
            BasicDBList list = (BasicDBList) (dbObject.get("cards"));
            for (Object bo: list
                 ) {
                BasicDBObject bdo =(BasicDBObject) bo;
                users.add((BasicDBObject)(bdo.get("user")));

            }

        } catch (Exception e) {

        }
        return users;

    }
}
