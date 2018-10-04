package com.dubbo.rpc.monitor;

import com.dubbo.rpc.config.ProviderUrlConfig;
import com.dubbo.spring.AppDomainContext;
import com.dubbo.util.NetUtils;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:16
 */
public class InvokeLogger {
    private static AtomicBoolean isServerOnline = new AtomicBoolean(true);
    private static int serverOfflineMaxQueueCapti = 5000;
    protected static ConcurrentLinkedQueue<InvokeLog> sendQueue = new ConcurrentLinkedQueue<InvokeLog>();

    protected ExecutorService exec;
    protected int workThreads = 2;

    public static int LOG_LEVEL = 300;
    public static int LOG_TIMECOST_MIN = 5000;


    public static void log(String beanName, String methodDesc, ProviderUrlConfig pUrl, Date requestDt, Date responseDt, int code) {
        if (beanName.equals(InvokeLogSender.serverLoggerBeanName)) {
            return;
        }

        long timeCost = responseDt.getTime() - requestDt.getTime();

        //invoke stat
        InvokeStat stat = InvokeStatCenter.getStat(beanName, methodDesc, pUrl);
        if (code > 300) {
            stat.increaseFailures(timeCost);
        } else {
            stat.increaseSuccesses(timeCost);
        }

        //for access log
        if (code > LOG_LEVEL || timeCost > LOG_TIMECOST_MIN) {
            if (!isServerOnline.get() && sendQueue.size() > serverOfflineMaxQueueCapti) {
                sendQueue.clear();
            }
            InvokeAccessLog log = new InvokeAccessLog();
            log.setServiceBean(beanName);
            log.setMethodDesc(methodDesc);
            log.setProviderHost(pUrl != null ? pUrl.getHost() : null);
            log.setRequestDt(requestDt);
            log.setResponseDt(responseDt);
            log.setTimeCost(timeCost);
            log.setResCode(code);
            log.setFromDomain(AppDomainContext.getName());
            log.setClientAddress(NetUtils.getLocalHost());
            log.setProtocol(pUrl != null ? pUrl.getProtocol() : null);
            sendQueue.add(log);
        }

    }

    public static void markServerOnline(boolean on) {
        isServerOnline.set(on);
    }

    public static boolean getServerOnlineMark() {
        return isServerOnline.get();
    }

    public static void putToSendQueue(InvokeLog log) {
        sendQueue.add(log);
    }

    public void setWorkThreads(int nums) {
        workThreads = nums;
    }

    public void startWork() {
        exec = Executors.newFixedThreadPool(workThreads + 1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
        for (int i = 0; i < workThreads; i++) {
            InvokeLogSender sender = new InvokeLogSender();
            sender.setSendQueue(sendQueue);
            exec.submit(sender);
        }
        InvokeStatLogCheck statLogCheck = new InvokeStatLogCheck();
        statLogCheck.setSendQueue(sendQueue);
        exec.submit(statLogCheck);

    }

    public void shutdown() {
        if (exec == null || exec.isShutdown()) {
            return;
        }
        exec.shutdown();
    }


}