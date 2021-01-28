package com.qiancy.springboot.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 功能简述：把controller相关的信息存到map容器中
 *
 * @author qiancy
 * @create 2021/1/28
 * @since 1.0.0
 */
@Component
public class RequestMappingInfoProcessor extends RequestMappingHandlerMapping {

    private Map<Method, RequestMappingInfo> requestMapping = new HashMap<>();

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * 用于保存处理方法和RequestMappingInfo的映射关系，这个方法在解析@RequestMapping时就会被调用
     * @param handler
     * @param method
     * @param mapping
     */
    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        HandlerMethod handlerMethod = super.createHandlerMethod(handler, method);
        requestMapping.putIfAbsent(handlerMethod.getMethod(), mapping);
        super.registerHandlerMethod(handler, method, mapping);
    }

    /**
     * 根据请求获取具体的实现类
     * @param request
     * @return
     */
    public RequestMappingInfo getRequestMappingInfo(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
            HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
            return Objects.requireNonNull(requestMapping.get(handlerMethod.getMethod()), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
