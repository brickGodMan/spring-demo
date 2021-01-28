package com.qiancy.springboot.processor;

import com.qiancy.springboot.api.ActualApiProcessor;
import com.qiancy.springboot.api.ApiDefinition;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * 功能简述：
 *
 * @author qiancy
 * @create 2021/1/28
 * @since 1.0.0
 */
@ApiDefinition(method = HttpMethod.GET,uri = "/test/processor")
@Component
public class TestProcessor implements ActualApiProcessor<String,String> {

    @Override
    public String doProcessor(String s) {
        return s + "processor is worked";
    }
}
