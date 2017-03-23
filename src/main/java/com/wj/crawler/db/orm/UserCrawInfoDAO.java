package com.wj.crawler.db.orm;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SYu on 3/23/2017.
 */
public class UserCrawInfoDAO extends BaseDAO{

   public UserCrawInfoDAO(MongoCollection collection){
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
            //user.put("container_id", getContainerIdFromURL(userInfo.getString("profile_url")));

            WriteModel<Document> wd=  new UpdateOneModel<Document>(
                    filterDocument,                      // find part
                    new Document("$set",user),           // update part
                    new UpdateOptions().upsert(true)     // upsert
            );
            users.add(wd);

        }
       collection.bulkWrite(users);
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
