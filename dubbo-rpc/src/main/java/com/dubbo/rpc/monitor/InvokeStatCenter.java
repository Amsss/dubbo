package com.dubbo.rpc.monitor;

import com.dubbo.rpc.config.ProviderUrlConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:20
 */
public class InvokeStatCenter {

    private static final String SIGN = "#";
    protected static final LoadingCache<String, InvokeStat> statCenter = createCacheLoader();

    protected static LoadingCache<String, InvokeStat> createCacheLoader() {
        return CacheBuilder.newBuilder().build(new CacheLoader<String, InvokeStat>() {
            @Override
            public InvokeStat load(String token) throws Exception {
                String[] vs = token.split(SIGN);
                String protocol = null;
                String host = null;
                if(vs.length == 4){
                    protocol = vs[2];
                    host = vs[3];
                }
                InvokeStat stat = new InvokeStat(vs[0], vs[1], protocol, host);
                return stat;
            }
        });
    }

    public static InvokeStat getStat(String serviceName,String methodDesc,ProviderUrlConfig pUrl){
        StringBuffer sb = new StringBuffer();
        sb.append(serviceName).append(SIGN).append(methodDesc);
        if(pUrl != null){
            sb.append(SIGN).append(pUrl.getProtocol()).append(SIGN).append(pUrl.getHost());
        }
        try {
            return statCenter.get(sb.toString());
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static List<InvokeStat> getStatList(){
        List<InvokeStat> stats = new ArrayList<InvokeStat>();
        stats.addAll(statCenter.asMap().values());
        return stats;
    }
}
