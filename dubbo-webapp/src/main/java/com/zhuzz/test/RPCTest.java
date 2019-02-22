package com.zhuzz.test;

import com.dubbo.util.annotation.RpcService;

/**
 * @description: rpc服务类
 * @author: zhuzz
 * @date: 2019/1/8 16:17
 */
public class RPCTest {
    @RpcService
    public String hello() {
        return "hello";
    }
}
