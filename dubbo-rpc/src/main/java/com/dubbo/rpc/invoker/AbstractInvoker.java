package com.dubbo.rpc.invoker;

import com.dubbo.rpc.Invoker;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:28
 */
public abstract class AbstractInvoker implements Invoker {


    protected long ConnectTimeout = 20000;
    protected long ReadTimeout = 20000;

    public long getConnectTimeout() {
        return ConnectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        ConnectTimeout = connectTimeout;
    }

    public long getReadTimeout() {
        return ReadTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        ReadTimeout = readTimeout;
    }

}

