package com.tomasis.model;

/**
 * Created by Dreamwalker on 2015/1/4.
 */
public class YoukuBasic {
    private int id;
    private String name;
    private String basicUrl;
    private String picUrl;
    private String basicTag;
    private String jsonInfo;//从 youku api 过来的基本json

    public String getJsonInfo() {
        return jsonInfo;
    }

    public void setJsonInfo(String jsonInfo) {
        this.jsonInfo = jsonInfo;
    }

    public String getBasicTag() {
        return basicTag;
    }

    public void setBasicTag(String basicTag) {
        this.basicTag = basicTag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasicUrl() {
        return basicUrl;
    }

    public void setBasicUrl(String basicUrl) {
        this.basicUrl = basicUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
