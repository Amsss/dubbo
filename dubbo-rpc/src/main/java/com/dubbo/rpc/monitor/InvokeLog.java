package com.dubbo.rpc.monitor;

import java.io.Serializable;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:17
 */
public interface InvokeLog extends Serializable {
    int getLogId();
    void setLogId(int logId);
}
