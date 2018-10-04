package com.dubbo.rpc;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:32
 */
public interface ServiceDispatcher {
    Result invoke(Invocation invocation);
}
