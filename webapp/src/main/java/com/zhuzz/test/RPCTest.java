package com.zhuzz.test;


import com.dubbo.util.annotation.RpcService;

/**
 * @Author: ZhuZeZhao
 * @Description:
 * @Date: Create in 15:21 2018/9/24
 * @Modified By:
 */
public class RPCTest {
    @RpcService
    public String hello() {
        return "hello";
    }
}
