package com.tomasis.entrance;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;

import java.util.List;

/**
 * Created by Dreamwalker on 2015/1/14.
 */
public class Fenci {
    public static void main(String args[]){

        List<Term> parse = BaseAnalysis.parse("让战士们过一个欢乐祥和的新春佳节。");
        System.out.println(parse);
        for(Term t :parse){
            System.out.println(t.getNatrue().natureStr);
            System.out.println(t.getName());
        }
    }

}
