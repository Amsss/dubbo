package com.dubbo.util;

import com.dubbo.util.store.ActiveStore;
import com.dubbo.util.store.StoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/416:36
 */
public class AppContextHolder implements ApplicationContextAware {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AppContextHolder.class);

    public static final String DEFAULT_SESSION_FACTORY = "mySessionFactory";
    public static String CONFIG_SERVER_DOMAIN = "platform";
    protected static String domain;
    protected static String host;
    protected static String zkConnect;
    protected static ApplicationContext appContext;
    protected static ActiveStore store;
    private static boolean devMode = true;
    private static String userTimezone = "GMT+8";

    private static CountDownLatch storeInitCountDownLatch = new CountDownLatch(1);

    public AppContextHolder() {
        setUserTimezone(userTimezone);
    }

    public AppContextHolder(boolean devMode){
        this();
        setDevMode(devMode);
    }

    public AppContextHolder(String userTimezone){
        setUserTimezone(userTimezone);
    }

    public AppContextHolder(boolean devMode, String userTimezone){
        setDevMode(devMode);
        setUserTimezone(userTimezone);
    }

    public static void setName(String name){
        domain = name;
    }

    public static String getName(){
        return domain;
    }

    public static String getHost(){
        return host;
    }

    public static String getZkConnect() {
        return zkConnect;
    }

    public static boolean isZkOpen() {
        return !S.isEmpty(zkConnect);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx)throws BeansException {
        appContext = ctx;
    }

    public static Object getBean(String beanName){
        return appContext.getBean(beanName);
    }

    public static void removeBean(String beanName){
        DefaultListableBeanFactory acf = (DefaultListableBeanFactory) appContext.getAutowireCapableBeanFactory();
        if(acf.containsBean(beanName)){
            acf.removeBeanDefinition(beanName);
        }
    }

    public static void addBean(String beanName,Class<?> clz,HashMap<String,Object> properties){
        DefaultListableBeanFactory acf = (DefaultListableBeanFactory) appContext.getAutowireCapableBeanFactory();
        if(acf.containsBean(beanName)){
            acf.removeBeanDefinition(beanName);
        }
        BeanDefinitionBuilder bd = BeanDefinitionBuilder.rootBeanDefinition(clz);
        Set<String> names = properties.keySet();
        for(String nm : names){
            bd.addPropertyValue(nm, properties.get(nm));
        }
        acf.registerBeanDefinition(beanName, bd.getBeanDefinition());
    }

    public static boolean containBean(String beanName){
        return appContext.containsBean(beanName);
    }

    public static <T> T getBean(String beanName,Class<T> type){
        return appContext.getBean(beanName,type);
    }

    public static void setActiveStoreAddress(String address){
        if(!StringUtils.isEmpty(address)){
            int i = address.lastIndexOf("/");
            String serverAddress = address.substring(i + 1);
            zkConnect = serverAddress;
            store = StoreFactory.createStore(serverAddress);
        }
        storeInitCountDownLatch.countDown();
    }

    public static ActiveStore getActiveStore(){
        try {
            storeInitCountDownLatch.await();
        }
        catch (InterruptedException e) {

        }
        return store;
    }

    public static ApplicationContext get(){
        return appContext;
    }

    public static boolean isReady(){
        return appContext != null;
    }

    public static boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        AppContextHolder.devMode = devMode;
        LOGGER.info("Debug[devMode] is -------------------{}-------------------", devMode?"ON":"OFF");
    }

    public void setConfigServerName(String configServerName){
        AppContextHolder.CONFIG_SERVER_DOMAIN = configServerName;
    }

    public static String getConfigServerName(){
        return CONFIG_SERVER_DOMAIN;
    }

    public static boolean isConfigServer(){
        return isConfigServer(domain);
    }

    public static boolean isConfigServer(String domainName){
        return CONFIG_SERVER_DOMAIN.equals(domainName);
    }

    public static String getConfigServiceId(String serviceId){
        return S.join(CONFIG_SERVER_DOMAIN, ".", serviceId);
    }

    public static String getUserTimezone() {
        return userTimezone;
    }

    public static void setUserTimezone(String userTimezone) {
        AppContextHolder.userTimezone = userTimezone;
        TimeZone.setDefault(TimeZone.getTimeZone(userTimezone));
        System.setProperty("user.timezone", userTimezone);
    }
}
