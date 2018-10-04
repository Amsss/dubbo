package com.dubbo.util.store.listener;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/415:39
 */
public interface StateListener {

    void onConnected();

    void onExpired();

    void onDisconnected();
}
