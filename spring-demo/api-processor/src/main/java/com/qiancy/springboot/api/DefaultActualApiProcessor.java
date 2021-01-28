package com.qiancy.springboot.api;

import com.qiancy.springboot.utils.ActualBeanProcessorMapping;
import com.qiancy.springboot.utils.RequestMappingInfoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能简述：
 *
 * @author qiancy
 * @create 2021/1/28
 * @since 1.0.0
 */
@Component
public class DefaultActualApiProcessor implements ApiProcessor<Object> {


    @Autowired
    private RequestMappingInfoProcessor requestMappingInfoProcessor;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ActualBeanProcessorMapping actualBeanProcessorMapping;

    @Override
    public <Rsp> Rsp process(Object req) {
        RequestMappingInfo requestMappingInfo = requestMappingInfoProcessor.getRequestMappingInfo(request);
        return (Rsp) actualBeanProcessorMapping.getActualApiProcessor(requestMappingInfo).process(req);
    }
}
