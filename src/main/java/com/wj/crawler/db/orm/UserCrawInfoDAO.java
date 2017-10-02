package com.wj.crawler.db.orm;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.ne;

/**
 * Created by SYu on 3/23/2017.
 */
public class UserCrawInfoDAO extends BaseDAO {

    public UserCrawInfoDAO(MongoCollection collection) {
        super(collection);
    }




    public void UpSertDocuments(List<Document> userCards) {

        List<WriteModel<Document>> users = new ArrayList<WriteModel<Document>>();
        for (Document userInfo : userCards) {
            Document filterDocument = new Document();
            filterDocument.append("_id", String.valueOf(userInfo.get("id")));
            Document user = new Document();
            user.put("_id", String.valueOf(userInfo.get("id")));
            user.put("screen_name", userInfo.getString("screen_name"));
            user.put("followers_count", userInfo.getInteger("followers_count"));
            WriteModel<Document> wd = new UpdateOneModel<Document>(
                    filterDocument,                      // find part
                    new Document("$set", user),           // update part
                    new UpdateOptions().upsert(true)     // upsert
            );
            users.add(wd);

        }
        collection.bulkWrite(users);
    }


    public void UpSertFollowerDocuments(List<Document> userCards) {

        List<WriteModel<Document>> users = new ArrayList<WriteModel<Document>>();
        for (Document userInfo : userCards) {
            Document filterDocument = new Document();
            filterDocument.append("_id", String.valueOf(userInfo.get("_id")));

            WriteModel<Document> wd = new UpdateOneModel<Document>(
                    filterDocument,                      // find part
                    new Document("$set", userInfo),           // update part
                    new UpdateOptions().upsert(true)     // upsert
            );
            users.add(wd);

        }
        collection.bulkWrite(users);
    }



    public void UpSertUserDocuments(List<CrawUserInfo> userCards) {

        List<WriteModel<Document>> users = new ArrayList<WriteModel<Document>>();
        for (CrawUserInfo userInfo : userCards) {
            Document filterDocument = new Document();
            filterDocument.append("_id", userInfo.getUserId());
            Document user = new Document();
            user.put("_id", userInfo.getUserId());
            user.put("screen_name", userInfo.getScreenName());
            user.put("followers_count", userInfo.getFollowersCount());
            WriteModel<Document> wd = new UpdateOneModel<Document>(
                    filterDocument,                      // find part
                    new Document("$set", user),           // update part
                    new UpdateOptions().upsert(true)     // upsert
            );
            users.add(wd);

        }
        collection.bulkWrite(users);
    }
    public void updateOneDoc(CrawUserInfo user) {
        Document filterDocument = new Document();
        filterDocument.append("_id", String.valueOf(user.getUserId()));
        Document doc = new Document();
        doc.append("last_post_id", user.getLastPostId());
        doc.append("last_fetch_time", user.getLastFetchTime());
        collection.updateOne(filterDocument, new Document("$set", doc));
    }

    public void updateFetchFollower(CrawUserInfo user){
        Document filterDocument = new Document();
        filterDocument.append("_id", String.valueOf(user.getUserId()));
        Document doc = new Document();
        doc.append("follower_fetched", user.isFollowerFetched());
        collection.updateOne(filterDocument, new Document("$set", doc));
    }

    public Iterable<CrawUserInfo> loadUnFollowUsers() {
        Iterable documents = collection.find(ne("follower_fetched", true)).limit(10000);
        Iterable<CrawUserInfo> userList = Iterables.transform(documents, new Function<Document, CrawUserInfo>() {
            @Nullable
            public CrawUserInfo apply(@Nullable Document document) {
                CrawUserInfo user = new CrawUserInfo();
                user.setUserId(document.getString("_id"));
                return user;
            }

        });
        return userList;
    }


    public Iterable<CrawUserInfo> loadNoAddrUsers() {
        Iterable documents = collection.find(exists("address", false)).limit(10000);
        Iterable<CrawUserInfo> userList = Iterables.transform(documents, new Function<Document, CrawUserInfo>() {
            @Nullable
            public CrawUserInfo apply(@Nullable Document document) {
                CrawUserInfo user = new CrawUserInfo();
                user.setUserId(document.getString("_id"));
                return user;
            }

        });
        return userList;
    }


    public void UpdateFollowFetchStatus(String id){
        Document filterDocument = new Document();
        filterDocument.append("_id", id);
        Document doc = new Document();
        doc.append("follower_fetched", true);
        collection.updateOne(filterDocument, new Document("$set", doc));
    }

    public void UpdateUserAddr(String id, String address){
        Document filterDocument = new Document();
        filterDocument.append("_id", id);
        Document doc = new Document();
        doc.append("address", address);
        collection.updateOne(filterDocument, new Document("$set", doc));
    }


    public Iterable<CrawUserInfo> loadCrawStatus() {
        Iterable documents = collection.find();
        Iterable<CrawUserInfo> userList = Iterables.transform(documents, new Function<Document, CrawUserInfo>() {
            @Nullable
            public CrawUserInfo apply(@Nullable Document document) {
                CrawUserInfo user = new CrawUserInfo();
                user.setUserId(document.getString("_id"));
                user.setFollowersCount(checkNotNull(document.getInteger("followers_count")));
                user.setScreenName(document.getString("screen_name"));
                user.setLastPostId(document.getString("last_post_id"));
                user.setLastFetchTime(document.getDate("last_fetch_time"));
                return user;
            }

        });
        return userList;
    }


    public Iterable<CrawUserInfo> loadUsersForFollowers() {
        Iterable documents = collection.find(ne("follower_fetched",true));
        Iterable<CrawUserInfo> userList = Iterables.transform(documents, new Function<Document, CrawUserInfo>() {
            @Nullable
            public CrawUserInfo apply(@Nullable Document document) {
                CrawUserInfo user = new CrawUserInfo();
                user.setUserId(document.getString("_id"));
                user.setFollowersCount(checkNotNull(document.getInteger("followers_count")));
                user.setScreenName(document.getString("screen_name"));
                return user;
            }

        });
        return userList;
    }

    //url = http://m.weibo.cn/u/1787537264?uid=1787537264&luicode=10000011&lfid=1076031787537264
    //return 1076031787537264 (or 107603)
    private String getContainerIdFromURL(String url) {
        String ret = "";
        int start = url.indexOf("&lfid=");
        if (start > -1) {
            ret = url.substring(start + 6);
        }
        return ret;
    }





}
