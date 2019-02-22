package com.dubbo.rpc.monitor;

import java.util.Date;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:20
 */
public class InvokeAccessLog implements InvokeLog {
    private static final long serialVersionUID = 6634470541841011310L;
    private int logId;
    private int code;
    private String fromAppDomain;
    private String toAppDomain;
    private String clientAddress;

    private String protocol;
    private String providerHost;
    private String serviceName;
    private String methodDesc;

    private Date requestDt;
    private Date responseDt;
    private long timecost;


    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public void setResCode(int code) {
        this.code = code;
    }

    public int getResCode() {
        return code;
    }

    public void setFromDomain(String appDomain) {
        fromAppDomain = appDomain;
    }

    public String getFromDomain() {
        return fromAppDomain;
    }

    public String getToDomain() {
        return toAppDomain;
    }

    public void setToDomain(String toAppDomain) {
        this.toAppDomain = toAppDomain;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setProviderHost(String host) {
        providerHost = host;
    }

    public String getProviderHost() {
        return providerHost;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setServiceBean(String name) {
        serviceName = name;
        int i = name.indexOf(".");
        toAppDomain = name.substring(0, i);
    }

    public String getServiceBean() {
        return serviceName;
    }

    public void setMethodDesc(String desc) {
        methodDesc = desc;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setRequestDt(Date dt) {
        requestDt = dt;
    }

    public Date getRequestDt() {
        return requestDt;
    }

    public void setResponseDt(Date dt) {
        responseDt = dt;
    }

    public Date getResponseDt() {
        return responseDt;
    }

    public void setTimeCost(long timecost) {
        this.timecost = timecost;
    }

    public long getTimeCost() {
        return timecost;
    }

}
