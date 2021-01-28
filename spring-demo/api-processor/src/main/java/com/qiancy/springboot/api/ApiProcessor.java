package com.qiancy.springboot.api;

/**
 * 功能简述：
 *
 * @author qiancy
 * @create 2021/1/28
 * @since 1.0.0
 */
public interface ApiProcessor<Req> {

    /**
     * 基类方法
     * @param req
     * @param <Rsp>
     * @return
     */
    <Rsp> Rsp process(Req req);
}
