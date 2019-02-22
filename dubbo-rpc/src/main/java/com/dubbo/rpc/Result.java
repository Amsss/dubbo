package com.dubbo.rpc;

import java.io.Serializable;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:32
 */
public class Result implements Serializable {
    private static final long serialVersionUID = -6229596616525453018L;
    private Object result;
    private Throwable exception;

    public Result(Object result) {
        this.result = result;
    }

    public Result() {

    }

    public Result(Throwable exception) {
        this.exception = exception;
    }

    public Object getValue() {
        return result;
    }

    public void setValue(Object value) {
        this.result = value;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }

    public void throwExpceptionIfHas() throws Throwable {
        if (exception != null) {
            throw exception;
        }
    }

}

