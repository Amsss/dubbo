package com.dubbo.rpc;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:32
 */
public interface ServiceDispatcher {
    Result invoke(Invocation invocation);
}
