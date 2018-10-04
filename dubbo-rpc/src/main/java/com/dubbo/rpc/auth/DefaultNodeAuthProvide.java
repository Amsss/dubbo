package com.dubbo.rpc.auth;

import com.dubbo.util.AppContextHolder;
import com.dubbo.util.JSONUtils;
import com.dubbo.util.store.ActiveStore;
import com.dubbo.util.store.StoreConstants;

import java.util.HashMap;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:03
 */
public class DefaultNodeAuthProvide implements NodeAuthProvide {

    private ActiveStore store;


    public DefaultNodeAuthProvide() {
        store = AppContextHolder.getActiveStore();
    }

    @Override
    public boolean auth(String host) {
        try {
            byte[] data = store.getData(StoreConstants.SERVERNODES_HOME);
            if(data == null){
                return true;
            }
            HashMap<String, Integer> d = JSONUtils.parse(data, HashMap.class);
            if(d.containsKey(host)){
                int v = d.get(host);
                if(0 == v){
                    return false;
                }else{
                    return true;
                }
            }else{
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

