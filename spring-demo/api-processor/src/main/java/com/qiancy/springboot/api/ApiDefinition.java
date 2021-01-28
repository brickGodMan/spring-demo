package com.qiancy.springboot.api;

import org.springframework.http.HttpMethod;

import java.lang.annotation.*;

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
