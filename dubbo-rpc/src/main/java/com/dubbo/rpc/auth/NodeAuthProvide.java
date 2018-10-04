package com.dubbo.rpc.auth;

import com.dubbo.util.annotation.RpcService;

/**
 * @author: zhuzz
 * @description:
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
