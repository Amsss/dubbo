package com.dubbo.rpc.balance;

import com.dubbo.rpc.config.ServiceConfig;

import java.util.Map;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:34
 */
public class BalanceFactory {
    private static Balance balance;
    private static Map<String, Balance> specialBalances;

    public void setBalance(Balance balance){
        BalanceFactory.balance = balance;
    }

    public void setSpecialBalances(Map<String, Balance> specialBalances){
        BalanceFactory.specialBalances = specialBalances;
    }

    public static Balance getBalance(ServiceConfig sc){
        if(balance == null){
            balance = new RandomBalance();
        }
        String serviceId = sc.getId();
        if(specialBalances != null && specialBalances.containsKey(serviceId)){
            return specialBalances.get(serviceId);
        }
        return balance;
    }
}
