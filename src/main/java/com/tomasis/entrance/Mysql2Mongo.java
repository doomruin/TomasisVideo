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

    private static double TYPE_A_WEIGHT=1.0;
    private static double TYPE_B_WEIGHT=3.5;
    private static double TYPE_C_WEIGHT=1.0;

    private static ObjectMapper om = new ObjectMapper();
    static ApplicationContext ac = new ClassPathXmlApplicationContext("/config/Spring.xml");
    static YoukuDao youkuDao=(YoukuDao)ac.getBean("youkuDao");



    public static void main(String Args[]){
        MongoClient mongoClient =null;
        try {
            mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB( "tomasis" );
            DBCollection col = db.getCollection("VideoTag");
            col.drop();
            DBCollection coll = db.createCollection("VideoTag",null);

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
        JsonNode jnDescription = jn.get("description");
        JsonNode jnYoukuTag = jn.get("tags");
        BasicDBObject doc = new BasicDBObject("basicId", yb.getId());//将要插入的记录
        List<BasicDBObject> tags = new ArrayList<BasicDBObject>();
        BasicDBObject a = new BasicDBObject(); //a类标签
        a.put("tag",yb.getBasicTag());
        a.put("weight",TYPE_A_WEIGHT);
        a.put("type","a");

        tags.add(a);
        /*
        b类标签,形式："tags": "天后,型男",
         */
        if(jnYoukuTag!=null){
            String[] bTags = jnYoukuTag.asText().split(",");
            for(String bTag: bTags){
                BasicDBObject b = new BasicDBObject();
                b.put("tag",bTag);
                b.put("weight",TYPE_B_WEIGHT);
                b.put("type","b");
                tags.add(b);
            }
        }
        /*
        c类标签，分词
         */
        if(jnDescription!=null){
            List<String> keywords = new TextRankKeyword().getKeyword("", jnDescription.asText(), 2,true);
            //System.out.println(new TextRankKeyword().getKeyword("", jn1.asText(),2));
            for(String s: keywords){
                BasicDBObject c = new BasicDBObject();
                c.put("tag",s);
                c.put("weight",TYPE_C_WEIGHT);
                c.put("type","c");
                tags.add(c);
            }
            doc.append("youkuDescription",jnDescription.asText());
        }
        doc.append("tags",tags);
        coll.insert(doc);
    }
}
