package com.dubbo.rpc.registry;

import com.dubbo.rpc.config.ProviderUrlConfig;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/416:34
 */
public class ConnectFailedProviderUrlHolder {

    private String beanName;
    private ProviderUrlConfig pUrl;

    public ConnectFailedProviderUrlHolder(String nm, ProviderUrlConfig p) {
        beanName = nm;
        pUrl = p;
    }

    public String getBeanName() {
        return beanName;
    }

    public ProviderUrlConfig getProviderUrl() {
        return pUrl;
    }

    @Override
    public int hashCode() {
        return (beanName + pUrl.getUrl()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (hashCode() == o.hashCode()) {
            return true;
        }
        return false;
    }
}
