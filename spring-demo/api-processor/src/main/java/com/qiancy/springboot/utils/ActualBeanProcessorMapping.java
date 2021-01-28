package com.qiancy.springboot.utils;

import com.qiancy.springboot.api.ActualApiProcessor;
import com.qiancy.springboot.api.ApiDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 功能简述：把打了特定注解的processor放到map容器中
 *
 * @author qiancy
 * @create 2021/1/28
 * @since 1.0.0
 */
@Component
public class ActualBeanProcessorMapping implements BeanPostProcessor {

    private Map<ApiKey, ActualApiProcessor> apiProcessorMapping = new HashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        ApiDefinition definition = clazz.getAnnotation(ApiDefinition.class);
        if (bean instanceof ActualApiProcessor && definition != null) {
            for (String s : definition.uri()) {
                ApiKey apiKey = new ApiKey(definition.method().toString(), s);
                apiProcessorMapping.putIfAbsent(apiKey, (ActualApiProcessor) bean);
            }
        }
        return bean;
    }

    public ActualApiProcessor getActualApiProcessor(RequestMappingInfo requestMappingInfo) {
        Set<String> uriPatterns = requestMappingInfo.getPatternsCondition().getPatterns();
        Set<RequestMethod> requestMethods = requestMappingInfo.getMethodsCondition().getMethods();
        ApiKey apiKey = new ApiKey(requestMethods.iterator().next().toString().toUpperCase(), uriPatterns.iterator().next());
        return Objects.requireNonNull(apiProcessorMapping.get(apiKey), "not found processor");
    }

    private class ApiKey {
        private String method;

        private String uri;

        public ApiKey(String method, String uri) {
            this.method = method;
            this.uri = uri;
        }

        @Override
        public int hashCode() {
            return method.hashCode() + uri.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ApiKey) {
                ApiKey apiKey = (ApiKey) obj;
                return Objects.equals(apiKey.method, method) && Objects.equals(apiKey.uri, uri);
            }
            return false;
        }

        @Override
        public String toString() {
            return method + "-" + uri;
        }
    }
}
