package com.dubbo.util.message;

import java.io.Serializable;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:00
 */
public interface MessageStore {
    public static final String CALLBACK_METHOD = "onSubscribeMessage";

    void add(String topic, Serializable content);

    void subscribe(String topic, Object callback);

    void completeSubscribe();
}
