package com.wj.crawler.parser;

import org.apache.http.HttpEntity;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 8/14/2017.
 */
public class FollowerParser {


    private static final Logger Log = LoggerFactory.getLogger(FollowerParser.class);


    public FollowerParser() {

    }


    public String parseUserAddress(HttpEntity entity) {

        String address = "";
        String content = readContent(entity);
        org.jsoup.nodes.Document doc = Jsoup.parse(content);
        Element el = doc.select(".tip").get(1).nextElementSibling();
        el.text();

//
        String patternStr = "\u5730\u533a:.+\u751f\u65e5:";
        Pattern pattern = Pattern.compile(patternStr);
        String text = el.text();
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            address = text.substring(start + 3, end - 3).trim();

        } else {

             patternStr = "\u5730\u533a:.+\u8ba4\u8bc1\u4fe1\u606f";
             pattern = Pattern.compile(patternStr);
             matcher = pattern.matcher(text);
            if(matcher.find()){
                int start = matcher.start();
                int end = matcher.end();
                address = text.substring(start + 3, end - 4).trim();
            }else{
                Log.info("can't find address" );
            }


        }

        Log.info("address:" + address);
        return address;
    }

    public List<Document> parseWeiboUser(HttpEntity entity) {
        List<Document> users = new ArrayList<>();
        String content = readContent(entity);
        org.jsoup.nodes.Document doc = Jsoup.parse(content);

        List<String> names = new ArrayList<>();
        List<Integer> followers = new ArrayList<>();

        doc.select("table").forEach(
                tb -> {
                    Element el = tb.select("td").last();
                    if (el == null) {
                        return;
                    }

                    String patternStr = "\u7c89\u4e1d\\d+\u4eba";
                    Pattern pattern = Pattern.compile(patternStr);
                    String text = el.text();
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        int start = matcher.start();
                        int end = matcher.end();
                        String followStr = text.substring(start + 2, end - 1);
                        try {
                            followers.add(Integer.parseInt(followStr));
                        } catch (Exception e) {

                        }

                    } else {
                        System.out.println("not found");
                    }
                    // Log.info(el.text());
                    names.add(el.select("a").first().text());
                }
        );


        if (names.size() < 1) {

        } else {
            String uids = doc.select("input[name=uidList]").first().attr("value");
            List<String> ids = Arrays.asList(uids.split(","));
            for (int i = 0; i < ids.size(); i++) {
                users.add(new Document().append("screen_name", names.get(i)).append("_id", ids.get(i)).append("followers_count", followers.get(i)));
            }
        }

        return users.stream().filter(u ->
                (Integer) (u.get("followers_count")) > 20000
        ).collect(Collectors.toList());


        //return users;

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
        return ret;
    }


}
