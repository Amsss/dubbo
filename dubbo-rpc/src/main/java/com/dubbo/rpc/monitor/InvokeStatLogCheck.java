package com.dubbo.rpc.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:18
 */
public class InvokeStatLogCheck implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeStatLogCheck.class);
    private ConcurrentLinkedQueue<InvokeLog> sendQueue;
    private static int DELAY = 30;

    public void setSendQueue(ConcurrentLinkedQueue<InvokeLog> queue) {
        sendQueue = queue;
    }

    @Override
    public void run() {
        while (true) {
            List<InvokeStat> stats = InvokeStatCenter.getStatList();
            for (InvokeStat stat : stats) {
                if (stat.getInvokeCount() == 0) {
                    continue;
                }
                sendQueue.add(stat.getLog());
            }
            try {
                TimeUnit.SECONDS.sleep(DELAY);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }

}

