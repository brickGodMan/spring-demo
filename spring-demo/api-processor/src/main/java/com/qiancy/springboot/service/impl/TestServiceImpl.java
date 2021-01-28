package com.qiancy.springboot.service.impl;

import com.qiancy.springboot.api.ApiProcessor;
import com.qiancy.springboot.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能简述：
 *
 * @author qiancy
 * @create 2021/1/28
 * @since 1.0.0
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private ApiProcessor<Object> processor;

    @Override
    public String hello(String req) {
        return processor.process(req);
    }
}
