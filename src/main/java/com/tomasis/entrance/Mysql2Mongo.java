package com.tomasis.entrance;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.textrank.TextRankKeyword;
import com.tomasis.dao.YoukuDao;
import com.tomasis.model.WordTag;
import com.tomasis.model.YoukuBasic;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dreamwalker on 2015/1/16.
 */
public class Mysql2Mongo{
    private static int mySqlIndex =85;
    private static ObjectMapper om = new ObjectMapper();
    static ApplicationContext ac = new ClassPathXmlApplicationContext("/config/Spring.xml");
    static YoukuDao youkuDao=(YoukuDao)ac.getBean("youkuDao");
    // 连接到 mongodb 服务


    public static void main(String Args[]){
        MongoClient mongoClient =null;
        try {
            mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB( "tomasis" );
            DBCollection coll = db.getCollection("VideoTag");
            while(true){
                try {
                    transform(mySqlIndex, coll);
                    mySqlIndex++;
                    System.out.println("done: "+mySqlIndex);
                }catch(IndexOutOfBoundsException e) {
                    mySqlIndex++;
                    continue;
                }catch(JsonParseException e){
                    mySqlIndex++;
                    continue;//description不正确
                }catch (EmptyResultDataAccessException e){
                    mySqlIndex++;
                    continue;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void transform(int mysqlIndex,DBCollection coll)throws Exception{
        YoukuBasic yb = youkuDao.findById(mysqlIndex);
        JsonNode jn = om.readTree(yb.getJsonInfo());
        JsonNode jn1 = jn.get("description");
        if(jn1==null){
            BasicDBObject doc = new BasicDBObject("basicId", yb.getId());
            coll.insert(doc);
        }else {
            List<String> keywords = new TextRankKeyword().getKeyword("", jn1.asText(), 2,true);
            //System.out.println(new TextRankKeyword().getKeyword("", jn1.asText(),2));
            List<BasicDBObject> tags = new ArrayList<BasicDBObject>();
            for(String s: keywords){
                BasicDBObject b = new BasicDBObject();
                b.put("tag",s);
                b.put("weight",1.0);
                tags.add(b);
            }
            System.out.println(keywords);
            BasicDBObject doc = new BasicDBObject("basicId", yb.getId()).append("tags", tags).append("youkudDescription",jn1.asText());
            coll.insert(doc);
        }
    }
}
