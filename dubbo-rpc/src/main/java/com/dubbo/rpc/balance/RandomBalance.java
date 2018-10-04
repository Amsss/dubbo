package com.dubbo.rpc.balance;

import com.dubbo.rpc.config.ProviderUrlConfig;

import java.util.List;
import java.util.Random;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:35
 */
public class RandomBalance implements Balance {
    private Random rd = new Random();

    @Override
    public ProviderUrlConfig select(List<ProviderUrlConfig> ls) {
        int max = ls.size();
        if (max == 0) {
            return null;
        }
        if (max == 1) {
            return ls.get(0);
        }
        int index = rd.nextInt(max);
        return ls.get(index);
    }

}
