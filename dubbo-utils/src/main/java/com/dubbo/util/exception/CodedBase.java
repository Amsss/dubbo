package com.dubbo.util.exception;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/415:57
 */
public interface CodedBase {

    int getCode();

    void throwThis() throws Exception;

    String getMessage();

    Throwable getCause();

    StackTraceElement[] getStackTrace();
}
