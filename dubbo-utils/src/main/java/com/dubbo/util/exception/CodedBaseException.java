package com.dubbo.util.exception;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/415:59
 */
public class CodedBaseException extends Exception implements CodedBase {

    protected static final long serialVersionUID = -8481811634176212223L;

    protected int code = 500;

    public CodedBaseException() {
        super();
    }

    public CodedBaseException(int code) {
        super();
        this.code = code;
    }

    public CodedBaseException(String msg) {
        super(msg);
    }

    public CodedBaseException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public CodedBaseException(Throwable e) {
        super(e);
    }

    public CodedBaseException(Throwable e, int code) {
        super(e);
        this.code = code;
    }

    public CodedBaseException(Throwable e, String msg) {
        super(msg, e);
    }

    public CodedBaseException(Throwable e, int code, String msg) {
        super(msg, e);
        this.code = code;
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public void throwThis() throws Exception {

    }
}
