package com.tomasis.dao;

import com.tomasis.model.YoukuBasic;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * Created by Dreamwalker on 2015/1/4.
 */
public class YoukuDaoImpl extends JdbcDaoSupport implements YoukuDao{
    String  insert = "insert into YoukuBasic(name, basicUrl, picUrl,basicTag) values(?,?,?,?)";
    String findById = "select * from Youkubasic where id=?";
    String findBasicUrlById = "select basicUrl from Youkubasic where id=?";
    String updateJsonInfo = "update YoukuBasic set jsonInfo =? where id=?";
    @Override
    public int insert(YoukuBasic yb) {
        return getJdbcTemplate().update(insert, new Object[]{yb.getName(), yb.getBasicUrl(),yb.getPicUrl(), yb.getBasicTag()});
    }

    @Override
    public YoukuBasic findById(int id) {
        return getJdbcTemplate().
               queryForObject(findById, new BeanPropertyRowMapper<YoukuBasic>(YoukuBasic.class), id);
    }

    @Override
    public String findBasicUrlById(int id) {
        try {
            return getJdbcTemplate()
                    .queryForObject(findBasicUrlById, SingleColumnRowMapper.newInstance(String.class), id);
        }catch (EmptyResultDataAccessException e){
            return EMPTY;
        }
    }

    @Override
    public void updateJsonInfo(String jsonInfo, int id) {
        getJdbcTemplate().update(updateJsonInfo, new Object[]{jsonInfo,id});
    }
}
