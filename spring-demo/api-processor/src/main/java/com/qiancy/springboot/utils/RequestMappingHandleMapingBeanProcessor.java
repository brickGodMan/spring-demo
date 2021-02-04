package com.qiancy.springboot.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 功能简述：
 *
 * @author qiancy
 * @create 2021/2/3
 * @since 1.0.0
 */
@Component
public class RequestMappingHandleMapingBeanProcessor implements BeanPostProcessor {


    private Map<Method, List<RequestMappingInfo>> requestMapping = new HashMap<>();

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAssignableFrom(RequestMappingHandlerMapping.class)) {
            requestMappingHandlerMapping = (RequestMappingHandlerMapping) bean;
            Map<RequestMappingInfo, HandlerMethod> requestMethodMap = requestMappingHandlerMapping.getHandlerMethods();
            requestMethodMap.forEach((key, value) -> {
                List<RequestMappingInfo> requestMappingInfos = requestMapping.getOrDefault(value, new ArrayList<>());
                requestMappingInfos.add(key);
                requestMapping.putIfAbsent(value.getMethod(), requestMappingInfos);
            });
        }
        return bean;
    }

    /**
     * 根据reques 获取相关信息
     * @param request
     * @return
     * @throws Exception
     */
    public RequestMappingInfo getRequestMappingInfo(HttpServletRequest request) throws Exception {
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
        HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
        return Objects.requireNonNull(requestMapping.get(handlerMethod.getMethod()).get(0), "not found");
    }
}
