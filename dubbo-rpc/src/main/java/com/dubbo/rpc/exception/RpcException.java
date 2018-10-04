package com.dubbo.rpc.exception;

import com.dubbo.util.exception.CodedBaseException;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/415:47
 */
public class RpcException extends CodedBaseException {

    private static final long serialVersionUID = 7815426752583648734L;

    public static final int UNKNOWN = 500;

    public static final int CONNECT_FALIED = 501;

    public static final int TIMEOUT = 502;

    public static final int SERVICE_OFFLINE = 400;

    public static final int SERVICE_NOT_REGISTED = 404;

    public static final int METHOD_NOT_FOUND = 405;

    public static final int ILLEGAL_ARGUMENTS = 406;

    public static final int REGISTRY_NOT_READY = 407;

    public RpcException() {
        super(UNKNOWN);
    }

    public RpcException(String message, Throwable cause) {
        super(cause, message);
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(int code) {
        super(code);
    }

    public RpcException(int code, String message, Throwable cause) {
        super(cause, code, message);
    }

    public RpcException(int code, String message) {
        super(code, message);
    }

    public RpcException(int code, Throwable cause) {
        super(cause, code);
    }

    public boolean isServiceOffline() {
        return code == SERVICE_OFFLINE;
    }

    public boolean isServiceNotRegisted() {
        return code == SERVICE_NOT_REGISTED;
    }

    public boolean isTimeout() {
        return code == TIMEOUT;
    }

    public boolean isConnectFailed() {
        return code == CONNECT_FALIED;
    }
}
