package com.dubbo.rpc.balance;

import com.dubbo.rpc.config.ProviderUrlConfig;

import java.util.List;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:34
 */
public interface Balance {
    ProviderUrlConfig select(List<ProviderUrlConfig> ls);
}

