package com.dubbo.rpc;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:26
 */
public interface Invoker {
    public Object call(Invocation invocation) throws Exception;
}
