package com.dubbo.spring;

import com.dubbo.rpc.monitor.InvokeLogger;
import com.dubbo.rpc.registry.ServiceRegistry;
import com.dubbo.util.AppContextHolder;
import com.dubbo.util.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/9/2723:05
 */
public class AppDomainContext extends AppContextHolder implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {
    private static Pattern pattern = Pattern.compile("([a-zA-Z\\.0-9]+\\:[0-9]+)");
    private static CountDownLatch registerInitCountDownLatch = new CountDownLatch(1);
    private static String rpcServerWorkUrl;
    private static ServiceRegistry registry;
    private static InvokeLogger invokeLogger;
    private static boolean enableLogger;
    private static CountDownLatch contextInitLatch = new CountDownLatch(1);

    @Override
    public void setApplicationContext(ApplicationContext ctx){
        super.setApplicationContext(ctx);
        if(enableLogger){
            invokeLogger = new InvokeLogger();
            invokeLogger.startWork();
        }
    }

    public static void setRpcServerWorkUrl(String url){
        if(url.indexOf("localhost") > 0){
            url = url.replace("localhost", NetUtils.getLocalHost());
        }
        if(url.indexOf("127.0.0.1") > 0){
            url = url.replace("127.0.0.1", NetUtils.getLocalHost());
        }
        Matcher m = pattern.matcher(url);
        if(m.find()){
            host = m.group(1);
        }
        rpcServerWorkUrl = url;
    }

    public static String getRpcServerWorkUrl(){
        return rpcServerWorkUrl;
    }

    public static String getRpcServerHost(){
        return host;
    }

    public static void setRegistryAddress(String address){
        if(StringUtils.isEmpty(address)){
            ServiceRegistry.setDisable(true);
            setActiveStoreAddress(null);
        }
        else{
            if(address.indexOf("localhost") > 0){
                address = address.replace("localhost", NetUtils.getLocalHost());
            }
            if(address.indexOf("127.0.0.1") > 0){
                address = address.replace("127.0.0.1", NetUtils.getLocalHost());
            }
            setActiveStoreAddress(address);
            registry = new ServiceRegistry(); // ServiceRegistryFactory.getRegistry(address);
            registry.setStore(store);
            registry.start();
        }
        registerInitCountDownLatch.countDown();
    }

    public static ServiceRegistry getRegistry(){
        try {
            registerInitCountDownLatch.await();
        }
        catch(InterruptedException e) {

        }
        return registry;
    }

    public static boolean isRegistryActive(){
        return getRegistry() != null;
    }

    public static boolean isRegistryActiveAndConfigServer(){
        return isRegistryActive() && isConfigServer();
    }

    public static boolean isRegistryActiveAndConfigServer(String domainName){
        return isRegistryActive() && isConfigServer(domainName);
    }

    public static void setEnableLogger(boolean status) {
        enableLogger = status;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() == null){	// root context
            contextInitLatch.countDown();
        }
    }

    public static void contextInitAwait(){
        try {
            contextInitLatch.await();
        } catch (InterruptedException e) {}
    }

    @Override
    public void destroy() throws Exception {
        if(enableLogger){
            invokeLogger.shutdown();
        }
    }

}
