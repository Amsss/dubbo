package com.dubbo.rpc;

import com.dubbo.rpc.balance.Balance;
import com.dubbo.rpc.balance.BalanceFactory;
import com.dubbo.rpc.config.MethodConfig;
import com.dubbo.rpc.config.ProviderUrlConfig;
import com.dubbo.rpc.config.ServiceConfig;
import com.dubbo.rpc.exception.RpcException;
import com.dubbo.rpc.invoker.InvokerFactory;
import com.dubbo.rpc.monitor.InvokeLogger;
import com.dubbo.rpc.registry.ConnectFailedProviderUrlHolder;
import com.dubbo.rpc.registry.ServiceRegistry;
import com.dubbo.spring.AppDomainContext;
import com.dubbo.util.NetUtils;
import com.dubbo.util.context.Context;

import java.util.Date;
import java.util.HashMap;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:21
 */
public class Client {

    private static String clientAddress = NetUtils.getLocalHost();

    public static Object rpcInvoke(String beanName, String methodName, Object[] parameters, HashMap<String,Object> headers, Balance balance) throws Exception{

        Object v = null;
        int resCode = 200;
        Date requestDt = new Date();
        ProviderUrlConfig pUrl = null;
        String methodDesc = null;
        try{
            ServiceRegistry registry = AppDomainContext.getRegistry();
            if(registry == null){
                throw new RpcException(RpcException.REGISTRY_NOT_READY,"registry not ready or disable.");
            }
            ServiceConfig sc = registry.find(beanName);
            MethodConfig mc = sc.getCompatibleMethod(methodName, parameters);
            if(mc == null){
                throw new RpcException(RpcException.METHOD_NOT_FOUND,"service[" + beanName + "] method[" + methodName + "] paramters is not compatiabled");
            }
            methodDesc = mc.getDesc();

            Invocation invocation = new Invocation();
            invocation.setBeanName(beanName);
            invocation.setMethodDesc(methodDesc);
            invocation.setParameters(parameters);

            invocation.setAllHeaders(headers);
            invocation.setHeader(Context.CLIENT_IP_ADDRESS,clientAddress);
            invocation.setHeader(Context.FROM_DOMAIN, AppDomainContext.getName());


            if(balance == null){
                balance = BalanceFactory.getBalance(sc);
            }
            int retryCount = 0;
            int maxRetrys = sc.getProvidersCount();
            while(true){
                pUrl = balance.select(sc.getProviderUrls());

                Invoker invoke = InvokerFactory.getInvoker(pUrl);
                if(invoke == null){
                    throw new RpcException(RpcException.SERVICE_OFFLINE,"service[" + beanName + "] all provider server offline.retry again later.");
                }
                try{
                    v = invoke.call(invocation);
                    pUrl.setLastConnectFailed(false);
                }
                catch(RpcException e){
                    if(!e.isConnectFailed()){
                        throw e;
                    }
                    pUrl.setLastConnectFailed(true);
                    ServiceRegistry.checkConnectFailedProvider(new ConnectFailedProviderUrlHolder(beanName,pUrl));
                    retryCount ++;
                    if(retryCount <= maxRetrys){
                        continue;
                    }
                    else{
                        throw e;
                    }
                }
                return v;
            }
        }
        catch(RpcException e){
            resCode = e.getCode();
            throw e;
        }
        finally{
            if(pUrl != null){
                pUrl.increaseInvokeCount(resCode < 300);
                Date responseDt = new Date();
                InvokeLogger.log(beanName, methodDesc, pUrl, requestDt, responseDt, resCode);
            }
        }

    }

    public static Object rpcInvoke(String beanName,String methodName,Object[] parameters,HashMap<String,Object> headers) throws Exception{
        return rpcInvoke(beanName,methodName,parameters,headers,null);
    }

    public static Object rpcInvoke(String beanName,String methodName,Object ...parameters) throws Exception{
        return rpcInvoke(beanName,methodName,parameters,null);
    }

    public static Object rpcInvoke(String beanName,String methodName,Balance balance,Object ...parameters) throws Exception{
        return rpcInvoke(beanName,methodName,parameters,null,balance);
    }

    public static Object rpcInvoke(String beanName,String methodName) throws Exception{
        return rpcInvoke(beanName,methodName,null,null,null);
    }
}

