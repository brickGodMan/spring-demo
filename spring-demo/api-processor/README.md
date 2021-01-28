## 定制MVC Service实现类行为Demo

### 总体结构

> 1. 定义一个自定义注解ApiDefinition
>
> 2. 定义一个存放RequestMappingInfo类的容器
>
> 3. 定义一个存放自定义实现类的容器（ActualApiProcessor）



#### 第一部分实现

ApiDefinition

```java
/**
 * @author qiancy
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiDefinition {

    HttpMethod method();

    String[] uri();
}
```

ApiProcessor

```java
/**
 * @author qiancy
 * @create 2021/1/22
 * @since 1.0.0
 */
public interface ApiProcessor<Req> {

    <Rsp> Rsp process(Req req);
}
```

ActualApiProcessor

```java
/**
 * @author qiancy
 * @create 2021/1/22
 * @since 1.0.0
 */
public interface ActualApiProcessor<Req, Rsp> extends ApiProcessor<Req> {

    Rsp doProcess(Req req);

    @Override
    default <Rsp> Rsp process(Req req) { 
        return (Rsp) doProcess(req);
    }
}
```

DefaultActualApiProcessor

```java
/**

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
```

#### 第二部分实现

RequestMappingInfoProcessor 里面存放RequestMappingInfo

```java
import org.springframework.beans.factory.annotation.Autowired;
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
```

#### 第三部分实现

ActualBeanProcess 里面存放实现了ApiDefinition注解的实例

```java
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
```

#### 应用

在原来Service实现类添加成员变量ApiProcessor<Object> 调用其process方法将得到正则processor的结果返回

```java
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private ApiProcessor<Object> processor;

    @Override
    public String hello(String req) {
        return processor.process(req);
    }
}
```
