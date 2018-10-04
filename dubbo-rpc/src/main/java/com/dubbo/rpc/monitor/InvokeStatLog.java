package com.dubbo.rpc.monitor;

import java.util.Date;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:19
 */
public class InvokeStatLog implements InvokeLog {
    private static final long serialVersionUID = 7782341067288064369L;

    private int logId;
    private String fromDomain;
    private String toDomain;
    private String clientAddress;

    private String protocol;
    private String providerHost;
    private String serviceBean;
    private String methodDesc;

    private Date statStart;
    private Date statEnd;

    private int successes;
    private int failures;
    private long maxTimeCost;
    private long minTimeCost;
    private long avgTimeCost;
    private long lastTimeCost;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getFromDomain() {
        return fromDomain;
    }

    public void setFromDomain(String fromDomain) {
        this.fromDomain = fromDomain;
    }

    public String getToDomain() {
        return toDomain;
    }

    public void setToDomain(String toDomain) {
        this.toDomain = toDomain;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProviderHost() {
        return providerHost;
    }

    public void setProviderHost(String providerHost) {
        this.providerHost = providerHost;
    }

    public String getServiceBean() {
        return serviceBean;
    }

    public void setServiceBean(String name) {
        this.serviceBean = name;
        int i = name.indexOf(".");
        toDomain = name.substring(0,i);
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public Date getStatStart() {
        return statStart;
    }

    public void setStatStart(Date statStart) {
        this.statStart = statStart;
    }

    public Date getStatEnd() {
        return statEnd;
    }

    public void setStatEnd(Date statEnd) {
        this.statEnd = statEnd;
    }

    public int getSuccesses() {
        return successes;
    }

    public void setSuccesses(int sucesses) {
        this.successes = sucesses;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public long getMaxTimeCost() {
        return maxTimeCost;
    }

    public void setMaxTimeCost(long maxTimeCost) {
        this.maxTimeCost = maxTimeCost;
    }

    public long getMinTimeCost() {
        return minTimeCost;
    }

    public void setMinTimeCost(long minTimeCost) {
        this.minTimeCost = minTimeCost;
    }

    public long getAvgTimeCost() {
        return avgTimeCost;
    }

    public void setAvgTimeCost(long avgTimeCost) {
        this.avgTimeCost = avgTimeCost;
    }

    public long getLastTimeCost() {
        return lastTimeCost;
    }

    public void setLastTimeCost(long lastTimeCost) {
        this.lastTimeCost = lastTimeCost;
    }



}

