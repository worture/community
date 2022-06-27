package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("data")
public class AlphaDaoImpl implements AlphaDao{

    @Override
    public String select() {
        return "data";
    }
}
