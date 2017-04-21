package com.confluence;


import com.atlassian.confluence.rest.client.RestClientFactory;
import com.atlassian.confluence.rest.client.authentication.AuthenticatedWebResourceProvider;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * Created by Syu on 4/12/2017.
 */
public class ConfluencePageFetch {

    private static ImmutableMap<String, String> users = ImmutableMap.of(
            "key1", "value1"

    );

    private static final String DEV_ID = "child_ul56608911-0";
    private static final String PM_ID = "child_ul56608942-0";

    private static final String LOCATION = "C:\\summary\\";

    private static final String PM_LOCATION = "C:\\PMSummary\\";

    private static final Logger Log = LoggerFactory.getLogger(ConfluencePageFetch.class);

    public static void main(String args[]) {

        new ConfluencePageFetch().fetchPMUsers();

    }

    private void loadUserPage(String user) {
        Log.debug("fetch user {} started", user);
        Client client = RestClientFactory.newClient();
        AuthenticatedWebResourceProvider awrp = new AuthenticatedWebResourceProvider(client, "http://222.126.214.117:80/confluence/rest/api/content/", "/" + user + "?expand=body.storage");
        String paswword = "123Netbrain!";
        awrp.setAuthContext("shihui.yu", paswword.toCharArray());
        WebResource resource = awrp.newRestWebResource();
        LinkedHashMap<String, Object> o = resource.get(LinkedHashMap.class);
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) o.get("body");
        LinkedHashMap<String, Object> storage = (LinkedHashMap<String, Object>) body.get("storage");
        String content = (String) storage.get("value");
        write2Local(user, content);
    }


    private void write2Local(String id, String content) {
        Document doc = Jsoup.parse(content);//strip all html tags
        Log.debug("begin to write the page content to local file user {} started", id);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PM_LOCATION + id + ".txt"))) {
            bw.write(doc.text());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.debug("user {} done!", id);
    }


    private void fetchDevUsers() {
        try {
            Document d = Jsoup.connect("http://222.126.214.117:80/confluence/plugins/pagetree/naturalchildren.action?decorator=none&excerpt=false&sort=position&reverse=false&disableLinks=false&expandCurrent=true&hasRoot=true&pageId=491556&treeId=0&startDepth=0&mobile=false&ancestors=54498394&ancestors=2719886&ancestors=491556&treePageId=56608911")
                    .cookie("crowd.token_key", "nWM7ZwHR2GgBVfKr9idlVQ00").get();
            parseUsers(d, DEV_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void fetchPMUsers() {
        try {
            Document d = Jsoup.connect("http://confluence.netbraintech.com/confluence/plugins/pagetree/naturalchildren.action?decorator=none&excerpt=false&sort=position&reverse=false&disableLinks=false&expandCurrent=true&hasRoot=true&pageId=491524&treeId=0&startDepth=0&mobile=false&ancestors=56608940&ancestors=491524&treePageId=56608942")
                    .cookie("crowd.token_key", "nWM7ZwHR2GgBVfKr9idlVQ00").get();
            parseUsers(d, PM_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> parseUsers(Document doc, String parentId) {
        Element parent = doc.getElementById(parentId);
        Elements pages = parent.getElementsByClass("plugin_pagetree_children_container");
        // id is "children56609332-0"
        Long start = System.currentTimeMillis();
        List<String> users = pages.parallelStream().map(el ->{
            String user = el.id().substring(8, 8 + 8);
            loadUserPage(user);
            return user;

        }).collect(toList());

        Long time = System.currentTimeMillis() - start;

        Log.debug("it took {} mill seconds", time);
        return users;
    }
}
