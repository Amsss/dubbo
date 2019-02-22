package com.dubbo.rpc.monitor;

import com.dubbo.rpc.Client;
import com.dubbo.rpc.exception.RpcException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:17
 */
public class InvokeLogSender implements Runnable {
    private static final Log logger = LogFactory.getLog(InvokeLogSender.class);
    public static String serverLoggerBeanName = "esb.rpcServerLogger";
    public static String serverLoggerMethodName = "log";
    private static int TIME_DELAY = 15;

    private ConcurrentLinkedQueue<InvokeLog> sendQueue;


    public void setSendQueue(ConcurrentLinkedQueue<InvokeLog> queue){
        sendQueue = queue;
    }

    @Override
    public void run() {
        while(true){
            List<InvokeLog> ls = new ArrayList<InvokeLog>();
            InvokeLog log;
            int retryCount = 0;
            while((log = sendQueue.poll()) != null || ls.size() > 0){
                if(log != null){
                    ls.add(log);
                }
                if(ls.size() >= 200 || sendQueue.isEmpty()){
                    try{
                        Client.rpcInvoke(serverLoggerBeanName, serverLoggerMethodName, new Object[]{ls});
                        InvokeLogger.markServerOnline(true);
                        retryCount = 0;
                    }
                    catch(RpcException e){
                        logger.error(e);
                        if(e.isServiceOffline() || e.isConnectFailed()){
                            retryCount ++;
                            InvokeLogger.markServerOnline(false);
                            try {
                                TimeUnit.SECONDS.sleep(TIME_DELAY * retryCount);
                            }
                            catch (InterruptedException e1) {

                            }
                            continue;
                        }
                        logger.error(e.getMessage());
                    }
                    catch(Exception e){
                        try {
                            TimeUnit.SECONDS.sleep(TIME_DELAY * retryCount);
                        }
                        catch (InterruptedException e1) {

                        }
                        continue;
                    }
                    ls.clear();

                }
            }

            try {
                TimeUnit.SECONDS.sleep(TIME_DELAY);
            }
            catch (InterruptedException e) {

            }
        }

    }

}

