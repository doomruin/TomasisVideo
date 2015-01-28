package com.tomasis.entrance;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.wordsimilarity.WordSimilarity;

import java.util.List;

/**
 * Created by Dreamwalker on 2015/1/22.
 */
public class WordSimilar {

    public static void main(String args[]){
//        int dis = WordSimilarity.disPrimitive("武则天", "争斗");
//        System.out.println(" and 争斗 dis : "+ dis);
//        double simP = WordSimilarity.simPrimitive("打斗", "争斗");
//        System.out.println("雇用 and 争斗 sim : "+ simP);
//
//        String word1 = "武则天";
//        String word2 = "猪";
//        double sim = WordSimilarity.simWord(word2, word1);
//        System.out.println(sim);
        try{
            calSimilarity();
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
            List<String> des = (List<String>)cursor.next().get("tags");
            System.out.println(des);
        }
    }
}
