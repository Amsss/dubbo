package com.dubbo.rpc.monitor;

import java.io.Serializable;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:17
 */
public interface InvokeLog extends Serializable {
    int getLogId();
    void setLogId(int logId);
}
