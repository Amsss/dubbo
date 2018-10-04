package com.dubbo.util.store;

import com.dubbo.util.store.support.ZooKeeperActiveStore;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/416:46
 */
public class StoreFactory {
    public static ActiveStore createStore(String address){
        return new ZooKeeperActiveStore(address);
    }
}
