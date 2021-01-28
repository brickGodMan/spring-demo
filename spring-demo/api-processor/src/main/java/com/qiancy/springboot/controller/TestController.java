package com.qiancy.springboot.controller;

import com.qiancy.springboot.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 功能简述：
 *
 * @author qiancy
 * @create 2021/1/28
 * @since 1.0.0
 */
@Controller
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping(value = "/test/processor",method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        System.out.println(testService.hello("开始了，我的定制MVC"));
        return "success";
    }
}
