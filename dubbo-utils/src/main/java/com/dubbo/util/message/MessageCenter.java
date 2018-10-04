package com.dubbo.util.message;

import java.io.Serializable;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:01
 */
public class MessageCenter {

    private static MessageStore store;

    public static MessageStore getStore() {
        return store;
    }

    public void setStore(MessageStore store) {
        MessageCenter.store = store;
    }

    public static void pub(String topic, Serializable content){
        store.add(topic, content);
    }

    public static void subscribe(String topic, Object callback){
        store.subscribe(topic, callback);
    }

    public static void completeSubscriber(){
        if(store != null){
            store.completeSubscribe();
        }
    }
}
