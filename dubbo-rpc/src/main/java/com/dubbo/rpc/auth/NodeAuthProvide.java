package com.dubbo.rpc.auth;

import com.dubbo.util.annotation.RpcService;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:03
 */
public interface NodeAuthProvide {
    /**
     * ip:port
     * @param host
     * @return
     */
    @RpcService
    public boolean auth(String host);
}
