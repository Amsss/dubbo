package com.dubbo.rpc.invoker;

import com.dubbo.rpc.Invoker;
import com.dubbo.rpc.config.ProviderUrlConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:27
 */
public class InvokerFactory {

    private static ConcurrentHashMap<String, Invoker> store = new ConcurrentHashMap<String, Invoker>();
    protected static Lock lock = new ReentrantLock(false);

    public static void removeInvoker(ProviderUrlConfig pUrl) {
        store.remove(pUrl.getUrl());
    }

    public static Invoker getInvoker(ProviderUrlConfig pUrl) {
        if (pUrl != null) {
            String url = pUrl.getUrl();
            lock.lock();
            try {
                if (store.containsKey(url)) {
                    return store.get(url);
                }
                String protocol = pUrl.getProtocol();
                if (protocol.equals("hessian")) {
                    Invoker invoker = new HessianInvoker(url);
                    store.put(url, invoker);
                    return invoker;
                }
            } finally {
                lock.unlock();
            }
        }
        return null;
    }
}
