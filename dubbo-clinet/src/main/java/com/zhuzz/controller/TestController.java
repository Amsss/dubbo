package com.zhuzz.controller;

import com.dubbo.rpc.Client;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: zhuzz
 * @date: 2019-01-08 16:49
 */
@RestController
@RequestMapping(value = "alarm", method = RequestMethod.GET)
public class TestController {

    @RequestMapping("queryPre")
    public String aaa() {
        String beanName = "dubbo.rpcCallService";
        String methodName = "hello";
        try {
            Client.rpcInvoke(beanName,methodName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "111";
    }
}
