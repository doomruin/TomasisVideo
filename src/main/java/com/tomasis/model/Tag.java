package com.tomasis.model;

/**
 * Created by Dreamwalker on 2015/1/29.
 */
public class Tag {
    private String tag;
    private double weight;
    private String type;
    /*这个标签的类型//a,b,c//
    a为类别（电影，电视剧，音乐等），
    b为网络媒体推荐标签（youku自己给的标签），
    c为提取关键词所得的标签
    */

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
