package com.tomasis.entrance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.tomasis.model.Tag;
import com.tomasis.model.Tags;
import com.wordsimilarity.WordSimilarity;

import java.util.*;

/**
 * Created by Dreamwalker on 2015/1/22.
 */
public class WordSimilar {

    static List<DBObject> all = new ArrayList<DBObject>();
    public static void main(String args[]){
//        int dis = WordSimilarity.disPrimitive("武则天", "争斗");
//        System.out.println(" and 争斗 dis : "+ dis);
//        double simP = WordSimilarity.simPrimitive("打斗", "争斗");
//        System.out.println("雇用 and 争斗 sim : "+ simP);
//
//        String word1 = "你";
//        String word2 = "猪";
//        double sim = WordSimilarity.simWord(word2, word1);
        //System.out.println(sim);
        try{
           // calSimilarity();
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB( "tomasis" );
            DBCollection coll = db.getCollection("VideoTag");
            DBCursor cursor = coll.find();
            while(cursor.hasNext()){
                DBObject bb = cursor.next();
                all.add(bb);
            }
            for(int i=0; i < all.size(); i++){
                //List<Double> sims = new ArrayList<Double>();
                Map<String,Double> map = new HashMap<String,Double>();
                for(int j=0; j < all.size(); j++){
                    if(i==j) continue;
                    try {
                        Double sim=sim((List<DBObject>) all.get(i).get("tags"), (List<DBObject>) all.get(j).get("tags"));
                        map.put(all.get(j).get("basicId").toString(),sim);

                        //System.out.println(all.get(j).get("tags"));
                    }catch (IndexOutOfBoundsException e){
                        continue;
                    }
                }
                Map<String,Double> result = sortMapByValue(map);
                System.out.println(all.get(i).get("basicId").toString()+"||||||:"+result);
                //System.out.println(sims);
                //System.out.println();
            }
            cursor.close();
            List<DBObject> curr =  (List<DBObject>)cursor.next().get("tags");
            List<DBObject> anot = (List<DBObject>) cursor.next().get("tags");
            String s1 = cursor.next().toString();
            //System.out.println(s);
            sim(curr,anot);
            System.out.println(WordSimilarity.simWord("爱","喜欢"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void calSimilarity()throws Exception{
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB( "tomasis" );
        DBCollection coll = db.getCollection("VideoTag");
        DBCursor cursor = coll.find();
        while(cursor.hasNext()){
            System.out.println(cursor.next());
        }
    }
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
    /*
        将hashmap按值排序
     */

    public static Map<String, Double> sortMapByValue(Map<String, Double> oriMap) {
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>(oriMap.entrySet());
            Collections.sort(entryList,
                    new Comparator<Map.Entry<String, Double>>() {
                        public int compare(Map.Entry<String, Double> entry1,
                                           Map.Entry<String, Double> entry2) {

                            return (int)entry2.getValue().doubleValue() - (int)entry1.getValue().doubleValue();
                        }
                    });
            Iterator<Map.Entry<String, Double>> iter = entryList.iterator();
            Map.Entry<String, Double> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }
}
