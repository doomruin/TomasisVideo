package com.tomasis.dao;

import com.tomasis.model.YoukuBasic;

/**
 * Created by Dreamwalker on 2015/1/4.
 */
public interface YoukuDao {
    public static String EMPTY = "empty";
    public int insert(YoukuBasic yb);
    public YoukuBasic findById(int id);
    public String findBasicUrlById(int id);
    public void updateJsonInfo(String jsonInfo, int id);
}
