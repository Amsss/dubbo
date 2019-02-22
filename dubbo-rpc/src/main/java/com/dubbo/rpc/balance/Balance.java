package com.dubbo.rpc.balance;

import com.dubbo.rpc.config.ProviderUrlConfig;

import java.util.List;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:34
 */
public interface Balance {
    ProviderUrlConfig select(List<ProviderUrlConfig> ls);
}

