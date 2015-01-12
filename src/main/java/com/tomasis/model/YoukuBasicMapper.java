package com.tomasis.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dreamwalker on 2015/1/7.
 */
public class YoukuBasicMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet r, int i) throws SQLException {
        YoukuBasic yb =new YoukuBasic();
        yb.setId(r.getInt("id"));
        yb.setBasicUrl(r.getString("basicUrl"));
        yb.setPicUrl(r.getString("picUrl"));
        yb.setName(r.getString("name"));
        yb.setBasicTag(r.getString("basicTag"));
        yb.setJsonInfo(r.getString("jsonInfo"));
        return yb;
    }
}
