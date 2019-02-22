package com.dubbo.util.exception;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/415:57
 */
public interface CodedBase {

    int getCode();

    void throwThis() throws Exception;

    String getMessage();

    Throwable getCause();

    StackTraceElement[] getStackTrace();
}
