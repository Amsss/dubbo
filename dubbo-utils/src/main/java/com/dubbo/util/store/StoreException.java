package com.dubbo.util.store;

import com.dubbo.util.exception.CodedBaseException;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/416:11
 */
public class StoreException extends CodedBaseException {

    private static final long serialVersionUID = 7815426752583648734L;

    public static final int UNKNOWN = 500;

    public static final int CONNECT_FALIED = 501;

    public static final int TIMEOUT = 502;

    public static final int PATH_NOT_EXIST = 404;

    public static final int PATH_EXIST = 405;

    public static final int SESSION_TIMEOUT = 509;

    public StoreException(int code) {
        super(code);
    }

    public StoreException(int code, String msg) {
        super(code, msg);
    }

    public StoreException(Throwable e, int code, String msg) {
        super(e, code, msg);
    }

    public StoreException(Throwable e, int code) {
        super(e, code);
    }

    public StoreException(Throwable e, String msg) {
        super(e, msg);
    }

    public StoreException(Throwable e) {
        super(e);
    }

    public boolean isPathExist() {
        return code == PATH_EXIST;
    }

    public boolean isPathNotExist() {
        return code == PATH_NOT_EXIST;
    }

    public boolean isSessionTimeout() {
        return code == SESSION_TIMEOUT;
    }

    public boolean isConnectFailed() {
        return code == CONNECT_FALIED;
    }
}
