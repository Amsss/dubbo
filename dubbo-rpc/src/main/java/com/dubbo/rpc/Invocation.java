package com.dubbo.rpc;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:24
 */
public class Invocation implements Serializable {
    private static final long serialVersionUID = -7883834289264437066L;
    private String beanName;
    private String methodDesc;
    private Object[] parameters;
    private HashMap<String, Object> headers;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String name) {
        beanName = name;
    }

    public void setMethodDesc(String signature) {
        methodDesc = signature;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public void setHeader(String name, Object v) {
        if (headers == null) {
            headers = new HashMap<String, Object>();
        }
        headers.put(name, v);
    }

    public Object getHeader(String name) {
        if (headers == null) {
            return null;
        }
        return headers.get(name);
    }

    public HashMap<String, Object> getAllHeaders() {
        return headers;
    }

    public void setAllHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
    }

}
