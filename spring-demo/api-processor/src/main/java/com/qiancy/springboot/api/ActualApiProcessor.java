package com.qiancy.springboot.api;

/**
 * 功能简述：
 *
 * @author qiancy
 * @create 2021/1/28
 * @since 1.0.0
 */
public interface ActualApiProcessor<Req, Rsp> extends ApiProcessor<Req> {

    /**
     * 基类默认实现
     *
     * @param req
     * @param <Rsp>
     * @return
     */
    @Override
    default <Rsp> Rsp process(Req req) {
        return (Rsp) doProcessor(req);
    }

    /**
     * 各个子类需要实现的方法
     *
     * @param req
     * @return
     */
    Rsp doProcessor(Req req);
}
