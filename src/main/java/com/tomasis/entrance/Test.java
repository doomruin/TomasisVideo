package com.tomasis.entrance;

import com.tomasis.service.wordsimilarity.WordSimilarity;

/**
 * Created by Dreamwalker on 2015/1/30.
 */
public class Test {
    public static void main(String args[]){

        double sim = WordSimilarity.simWord("武则天", "武则天");
        System.out.println(sim);
        }
}
