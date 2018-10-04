package com.dubbo.rpc;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:26
 */
public interface Invoker {
    public Object call(Invocation invocation) throws Exception;
}
