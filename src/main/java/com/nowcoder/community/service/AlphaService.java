package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {
    public AlphaService() {
        System.out.println("实例化。。。");
    }
    @PostConstruct
    public void init() {
        System.out.println("init AlphaService");
    }
    @PreDestroy
    public void destroy() {
        System.out.println("destroy...");
    }
    @Autowired
    @Qualifier("data")
    private AlphaDao alphaDao;

    public String find() {
        return alphaDao.select();
    }
}
