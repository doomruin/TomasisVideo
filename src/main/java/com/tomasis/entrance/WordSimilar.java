package com.tomasis.entrance;

import com.mongodb.*;
import com.tomasis.service.wordsimilarity.WordSimilarity;
import com.tomasis.util.SortUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by Dreamwalker on 2015/1/22.
 */
public class WordSimilar {
    private static int POWER_PERCENT =10;

    private static List<DBObject> all = new ArrayList<DBObject>();
    public static void main(String args[]){

        try{
//            File file = new File("src/main/resources/result.txt");
//
//            // if file doesnt exists, then create it
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            FileWriter fw = new FileWriter(file.getAbsoluteFile());
//            BufferedWriter bw = new BufferedWriter(fw);

            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB( "tomasis" );
            DBCollection coll = db.getCollection("VideoTag");
            DBCursor cursor = coll.find();
            while(cursor.hasNext()){
                DBObject bb = cursor.next();
                all.add(bb);
            }
            for(int i=0; i < all.size(); i++){
                Map<String,Double> map = new HashMap<String,Double>();
                int n =0;
                for(int j=0; j < all.size(); j++){
                    if(i==j) continue;
                    try {
                        Double sim=sim((List<DBObject>) all.get(i).get("tags"), (List<DBObject>) all.get(j).get("tags"));
                        //System.out.println(j);
                        map.put(all.get(j).get("basicId").toString(),sim);
                    }catch (IndexOutOfBoundsException e){
                        continue;
                    }
                }
                Map<String,Double> result = SortUtil.sortMapByValue(map);
                //全集推荐列表
                //System.out.println(all.get(i).get("basicId").toString()+"||||||:"+result.keySet());
                String a = SortUtil.extractByPower(result, POWER_PERCENT);
                //bw.write(all.get(i).get("basicId").toString()+"||||||:"+result.keySet()+"/n");
                //bw.newLine();//换行
                putRecommendation(coll,(Integer)all.get(i).get("basicId"),a);
                System.out.println(i);
            }
            //bw.close();
            cursor.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

//    private static void calSimilarity()throws Exception{
//        MongoClient mongoClient = new MongoClient("localhost", 27017);
//        DB db = mongoClient.getDB( "tomasis" );
//        DBCollection coll = db.getCollection("VideoTag");
//        DBCursor cursor = coll.find();
//        while(cursor.hasNext()){
//            System.out.println(cursor.next());
//        }
//    }
    /*
        计算两个视频之间的相似度
     */

    private static double sim(List<DBObject>  currTags, List<DBObject> anotherTags)throws Exception{
        double sim=0;
        if(currTags==null || anotherTags== null||currTags.size()==0 ||anotherTags.size()==0) return 0.1;
        for(DBObject tag: currTags){
            for(DBObject anotherTag : anotherTags){
                sim+= WordSimilarity.simWord((String)tag.get("tag"),(String)anotherTag.get("tag"))*(Double)tag.get("weight")*(Double)anotherTag.get("weight");
            }
        }
        //System.out.println(sim);
        return sim/(currTags.size()+anotherTags.size());

    }
    private static void putRecommendation(DBCollection coll, Integer basicId,String recommendation){
        BasicDBObject filter_dbObject = new BasicDBObject();
        filter_dbObject.put("basicId",basicId);//一定要注意basicId的类型
        DBObject updateDocument = new BasicDBObject();
        updateDocument.put("recIds",recommendation);
        DBObject updateSetValue=new BasicDBObject("$set",updateDocument);
        coll.update(filter_dbObject,updateSetValue,true, false);
    }
}
