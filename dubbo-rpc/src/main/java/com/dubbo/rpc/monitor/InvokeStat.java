package com.dubbo.rpc.monitor;

import com.dubbo.spring.AppDomainContext;
import com.dubbo.util.NetUtils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:19
 */
public class InvokeStat {

    private String protocol;
    private String providerHost;
    private String serviceName;
    private String methodDesc;

    private Date statStart;

    private AtomicInteger successes = new AtomicInteger(0);;
    private AtomicInteger failures = new AtomicInteger(0);
    private long maxTimeCost;
    private long minTimeCost;
    private long avgTimeCost;
    private long lastTimeCost;

    public InvokeStat(String serviceName,String methodDesc,String protocol,String providerHost){
        this.serviceName = serviceName;
        this.methodDesc = methodDesc;
        this.protocol = protocol;
        this.providerHost = providerHost;
        reset();
    }

    public void reset(){
        statStart = new Date();
        successes.set(0);
        failures.set(0);
        maxTimeCost = 0l;
        minTimeCost = 0l;
        avgTimeCost = 0l;
        lastTimeCost = 0l;
    }

    public void increaseSuccesses(long timeCost){
        successes.incrementAndGet();
        caluTimeCost(timeCost);
    }

    public void increaseFailures(long timeCost){
        failures.incrementAndGet();
        caluTimeCost(timeCost);
    }

    private void caluTimeCost(long timeCost){
        maxTimeCost = Math.max(maxTimeCost, timeCost);
        minTimeCost = Math.min(minTimeCost, timeCost);
        int total = successes.getAndAdd(failures.get());
        if(total > 1){
            minTimeCost = Math.min(minTimeCost, timeCost);
        }
        else{
            minTimeCost = timeCost;
        }
        avgTimeCost = (total * avgTimeCost + timeCost)/(total + 1);
        lastTimeCost = timeCost;
    }

    public int getInvokeCount(){
        return successes.getAndAdd(failures.get());
    }

    public InvokeStatLog getLog(){
        InvokeStatLog log = new InvokeStatLog();
        log.setFromDomain(AppDomainContext.getName());
        log.setClientAddress(NetUtils.getLocalHost());
        log.setProviderHost(providerHost);
        log.setProtocol(protocol);
        log.setServiceBean(serviceName);
        log.setMethodDesc(methodDesc);
        log.setSuccesses(successes.get());
        log.setFailures(failures.get());
        log.setAvgTimeCost(avgTimeCost);
        log.setMaxTimeCost(maxTimeCost);
        log.setMinTimeCost(minTimeCost);
        log.setLastTimeCost(lastTimeCost);
        log.setStatStart(statStart);
        log.setStatEnd(new Date());
        reset();
        return log;
    }
}
