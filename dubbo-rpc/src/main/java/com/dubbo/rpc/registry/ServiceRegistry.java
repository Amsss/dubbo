package com.dubbo.rpc.registry;

import com.dubbo.rpc.auth.NodeAuthProvideHolder;
import com.dubbo.rpc.config.ProviderUrlConfig;
import com.dubbo.rpc.config.ServiceConfig;
import com.dubbo.rpc.exception.RpcException;
import com.dubbo.spring.AppDomainContext;
import com.dubbo.util.S;
import com.dubbo.util.acl.ACListType;
import com.dubbo.util.message.MessageCenter;
import com.dubbo.util.message.MessageStore;
import com.dubbo.util.store.ActiveStore;
import com.dubbo.util.store.StoreConstants;
import com.dubbo.util.store.StoreException;
import com.dubbo.util.store.listener.NodeListener;
import com.dubbo.util.store.listener.StateListener;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @description: 描述
 * @author: zhuzz
 * @date: 2018/10/4 15:46
 */
public class ServiceRegistry implements StateListener, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    public static final int PUBLISH_CHECK_DELAY = 5;
    private static final int RETRY_DELAY = 5;
    public static final String CHARSET = "UTF-8";

    protected final LoadingCache<String, ServiceConfig> serviceStore = createCacheLoader();
    private static ConcurrentLinkedQueue<ServiceConfig> uploadQueue = new ConcurrentLinkedQueue<ServiceConfig>();
    private static CopyOnWriteArraySet<ConnectFailedProviderUrlHolder> connectedFailedProviders = new CopyOnWriteArraySet<ConnectFailedProviderUrlHolder>();
    protected static CopyOnWriteArraySet<ServiceConfig> deployedSet = new CopyOnWriteArraySet<ServiceConfig>();
    private volatile boolean running = true;
    private Thread t;
    private ActiveStore store;
    private String domainServiceRoot;
    private String providerUrl;
    private static boolean disable;

    protected LoadingCache<String, ServiceConfig> createCacheLoader() {
        return CacheBuilder.newBuilder().build(new CacheLoader<String, ServiceConfig>() {
            @Override
            public ServiceConfig load(String beanName) throws Exception {
                return loadFromStore(beanName);
            }
        });
    }

    public ServiceConfig loadFromStore(String beanName) throws RpcException {
        String domain = StringUtils.substringBefore(beanName, ".");
        String servicePath = buildPathStr(StoreConstants.SERVICES_HOME, "/", domain, "/", beanName);
        try {
            byte[] data = store.getData(servicePath);
            ServiceConfig o = ServiceConfig.parse(new String(data, CHARSET));
            serviceStore.put(beanName, o);

            String providerUrlsRoot = buildPathStr(servicePath, "/", StoreConstants.SERVICE_PROVIDERS);
            startProviderUrlsWatch(beanName, providerUrlsRoot);
            startServiceRegistryWatch(beanName, servicePath);

            return o;
        } catch (StoreException e) {
            if (e.isPathNotExist()) {
                throw new RpcException(RpcException.SERVICE_NOT_REGISTED, "beanName[" + beanName + "] not found on server registry");
            }
            throw new RpcException(RpcException.UNKNOWN, "load service[" + beanName + "] from registry falied.");
        } catch (Exception e) {
            throw new RpcException(RpcException.UNKNOWN, "load service[" + beanName + "] falied.", e);
        }

    }

    public static void publish(ServiceConfig service) {
        if (!disable) {
            uploadQueue.add(service);
        }
    }

    public static void setDisable(boolean disable) {
        ServiceRegistry.disable = disable;
    }

    public static void checkConnectFailedProvider(ConnectFailedProviderUrlHolder holder) {
        connectedFailedProviders.add(holder);
    }

    public ServiceConfig find(String beanName) throws RpcException {
        store.connectingAwait();
        try {
            return serviceStore.get(beanName);
        } catch (ExecutionException e) {
            throw new RpcException(RpcException.SERVICE_NOT_REGISTED, "beanName[" + beanName + "] not found on server registry");
        }
    }

    private void startServiceRegistryWatch(final String beanName, final String path) {
        try {
            store.isPathExist(path, new NodeListener() {

                @Override
                public void onDeleted(String path) {
                    //startServiceRegistryWatch(beanName,path);
                    serviceStore.invalidate(beanName);
                    logger.info("service[" + beanName + "] unregistered.");
                }

                @Override
                public void onDataChanged(String path) {
                    startServiceRegistryWatch(beanName, path);
                }

            });
        } catch (StoreException e) {
            logger.error(e.getMessage(), e);
            try {
                TimeUnit.SECONDS.sleep(RETRY_DELAY);
            } catch (InterruptedException e1) {
            }
            startServiceRegistryWatch(beanName, path);
        }

    }

    private void startProviderUrlsWatch(final String beanName, final String path) {
        startProviderUrlsWatch(beanName, path, false);
    }

    private void startProviderUrlsWatch(final String beanName, final String path, boolean justWactchEvent) {
        try {
            List<String> ls = store.getChildren(path, new NodeListener() {
                @Override
                public void onChildrenChanged(String path) {
                    startProviderUrlsWatch(beanName, path);
                }

                @Override
                public void onDataChanged(String path) {
                    startProviderUrlsWatch(beanName, path, true);
                }
            });
            if (justWactchEvent) {
                return;
            }
            ServiceConfig service = null;
            try {
                service = serviceStore.get(beanName);
            } catch (ExecutionException e1) {

            }
            if (service != null && ls != null) {
                List<ProviderUrlConfig> urls = new ArrayList<ProviderUrlConfig>();
                for (String s : ls) {
                    String url;
                    try {
                        url = URLDecoder.decode(s, CHARSET);
                        urls.add(new ProviderUrlConfig(url));
                    } catch (UnsupportedEncodingException e) {
                    }
                }
                service.updateProviderUrls(urls);
            }
        } catch (StoreException e) {
            logger.error(e.getMessage(), e);
            try {
                TimeUnit.SECONDS.sleep(RETRY_DELAY);
            } catch (InterruptedException e1) {
            }
            startProviderUrlsWatch(beanName, path);
        }
    }

    public void undeployService(String beanName) throws StoreException {
        int i = beanName.indexOf(".");
        String domain = beanName.substring(0, i);
        String domainServiceRoot = buildPathStr(StoreConstants.SERVICES_HOME, "/", domain);
        String servicePath = buildPathStr(domainServiceRoot, "/", beanName);
        store.delete(servicePath);
    }

    public void deployService(String beanName, String serviceDesc, boolean overwrite) throws StoreException {
        int i = beanName.indexOf(".");
        String domain = beanName.substring(0, i);
        deployService(domain, beanName, serviceDesc, overwrite);
    }

    public void deployService(String domain, String beanName, String serviceDesc, boolean overwrite) throws StoreException {
        String domainServiceRoot = buildPathStr(StoreConstants.SERVICES_HOME, "/", domain);
        String servicePath = buildPathStr(domainServiceRoot, "/", beanName);

        if (!store.isPathExist(domainServiceRoot)) {
            store.createPath(domainServiceRoot, null);
        }

        try {
            byte[] serviceData = serviceDesc.getBytes(CHARSET);
            if (!store.isPathExist(servicePath)) {
                store.createPath(servicePath, serviceData);
                store.createPath(buildPathStr(servicePath, "/", StoreConstants.SERVICE_ACL), null);
                store.createPath(buildPathStr(servicePath, "/", StoreConstants.SERVICE_PROVIDERS), null);
                logger.info("service[" + beanName + "] path created.");
            } else {
                if (overwrite) {
                    store.setData(servicePath, serviceData);
                    logger.info("service[" + beanName + "] overwrited.");
                } else {
                    byte[] data = store.getData(servicePath);
                    String serverSideDesc = new String(data, CHARSET);

                    ServiceConfig L = ServiceConfig.parse(serviceDesc);
                    ServiceConfig S = ServiceConfig.parse(serverSideDesc);

                    if (!L.equals(S)) {
                        throw new IllegalStateException("service[" + beanName + "] is not compatible with the registry one,deploy failed.");
                    }

                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("service[" + beanName + "] parse error:" + e.getMessage());
        } catch (UnsupportedEncodingException e) {

        }
    }

    public void deployProviderUrl(ServiceConfig service) throws StoreException {
        String beanName = service.getId();
        String providerUrlsRoot = buildPathStr(domainServiceRoot, "/", beanName, "/", StoreConstants.SERVICE_PROVIDERS);

        if (!store.isPathExist(providerUrlsRoot)) {
            store.createPath(providerUrlsRoot, null);
        }
        String providerUrlPath = buildPathStr(providerUrlsRoot, "/", providerUrl);
        if (store.isPathExist(providerUrlPath)) {
            store.delete(providerUrlPath);
        }
        store.createTempPath(providerUrlPath, null);
        deployedSet.add(service);
        startWatchACL(service);

        logger.info("service[" + beanName + "] online.");
    }

    public void undeployProviderUrl(ServiceConfig service) throws StoreException {
        String beanName = service.getId();
        String providerUrlPath = buildPathStr(domainServiceRoot, "/", beanName, "/", StoreConstants.SERVICE_PROVIDERS, "/", providerUrl);
        store.delete(providerUrlPath);
        deployedSet.remove(service);

        logger.info("service[" + beanName + "] offline");
    }

    public void subscribe(String beanName, String topic, boolean host) throws StoreException {
        String subscribePath = buildPathStr(StoreConstants.TOPICS_HOME, "/", topic);
        if (store.isPathExist(subscribePath)) {
            String subscribeCallbackPath = null;
            if (host) {
                subscribeCallbackPath = buildPathStr(subscribePath, "/", beanName, "@", providerUrl);
            } else {
                subscribeCallbackPath = buildPathStr(subscribePath, "/", beanName);
            }
            if (store.isPathExist(subscribeCallbackPath)) {
                store.delete(subscribeCallbackPath);
            }
            store.createTempPath(subscribeCallbackPath, null);
            logger.info("service[" + beanName + "] subscribe success:[" + topic + "]");
        } else {
            logger.warn("service[" + beanName + "] subscribe falied:topic[" + topic + "] not exist.");
        }
    }

    public void addServiceACLItem(String beanName, String aclStr) throws StoreException {
        String domain = StringUtils.substringBefore(beanName, ".");
        String ACLPath = buildPathStr(StoreConstants.SERVICES_HOME, "/", domain, "/", beanName, "/", StoreConstants.SERVICE_ACL);
        if (!store.isPathExist(ACLPath)) {
            store.createPath(ACLPath, null);
        }
        String ACLItemPath = buildPathStr(ACLPath, "/", aclStr);
        if (!store.isPathExist(ACLItemPath)) {
            store.createPath(ACLItemPath, null);
        }
    }

    public void removeServiceACLItem(String beanName, String aclStr) throws StoreException {
        String domain = StringUtils.substringBefore(beanName, ".");
        String ACLPath = buildPathStr(StoreConstants.SERVICES_HOME, "/", domain, "/", beanName, "/", StoreConstants.SERVICE_ACL);
        if (!store.isPathExist(ACLPath)) {
            return;
        }
        String ACLItemPath = buildPathStr(ACLPath, "/", aclStr);
        if (store.isPathExist(ACLItemPath)) {
            store.delete(ACLItemPath);
        }
    }

    public List<String> getServiceACL(String beanName) throws StoreException {
        int i = beanName.indexOf(".");
        String domain = beanName.substring(0, i);
        String ACLPath = buildPathStr(StoreConstants.SERVICES_HOME, "/", domain, "/", beanName, "/", StoreConstants.SERVICE_ACL);
        if (!store.isPathExist(ACLPath)) {
            return null;
        }
        return store.getChildren(ACLPath);
    }

    private void startWatchACL(final ServiceConfig service) {
        String beanName = service.getId();

        if (!deployedSet.contains(service)) {
            logger.info("service[" + beanName + "] stopping watch ACL as be offlined.");
            return;
        }

        String path = buildPathStr(domainServiceRoot, "/", beanName, "/", StoreConstants.SERVICE_ACL);
        try {
            List<String> ls = store.getChildren(path, new NodeListener() {
                @Override
                public void onChildrenChanged(String path) {
                    startWatchACL(service);
                }
            });
            service.updateACL(ACListType.whiteList, ls);
        } catch (StoreException e) {
            logger.error(e.getMessage(), e);
            try {
                TimeUnit.SECONDS.sleep(RETRY_DELAY);
            } catch (InterruptedException e1) {
            }
            startWatchACL(service);
        }
    }

    public void deployDomainServerNode() throws StoreException {
        String domain = AppDomainContext.getName();
        String serverNodesHome = StoreConstants.SERVERNODES_HOME;

        if (!store.isPathExist(serverNodesHome)) {
            store.createPath(serverNodesHome, null);
        }

        String domainPath = buildPathStr(serverNodesHome, "/", domain);
        if (!store.isPathExist(domainPath)) {
            store.createPath(domainPath, null);
        }
        String serverNodePath = buildPathStr(domainPath, "/", AppDomainContext.getRpcServerHost(), "-");
        if (store.isPathExist(serverNodePath)) {
            store.delete(serverNodePath);
        }
        store.createSeqTempPath(serverNodePath, null);
        logger.info("serverNode[" + AppDomainContext.getRpcServerHost() + "] deployed for domain[" + domain + "]");
    }

    public void stop() {
        running = false;
        t = null;
    }

    public void start() {
        if (t == null || t.isAlive()) {
            running = true;
            t = new Thread(this, "ctd-serviceRegistry");
            t.setDaemon(true);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        }
    }

    public void setStore(ActiveStore store) {
        this.store = store;
        store.addStateListener(this);
    }

    public ActiveStore getStore() {
        return store;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onExpired() {
        try {
            deployDomainServerNode();
        } catch (StoreException e) {
            logger.info("redeploy domainServerNode falied:" + e.getMessage());
        }
        for (ServiceConfig service : deployedSet) {
            publish(service);
        }
        logger.info("store is rebuilded,redeploy local services.");
    }

    @Override
    public void run() {
        try {
            prepareStore();
            deployDomainServerNode();
            providerUrl = URLEncoder.encode(AppDomainContext.getRpcServerWorkUrl(), CHARSET);
            if (!NodeAuthProvideHolder.auth(AppDomainContext.getRpcServerHost())) {
                logger.warn("domain:{} host:{} is not allow to deploy rpc-service. Make sure it is authorised.", AppDomainContext.getName(), AppDomainContext.getRpcServerHost());
                return;
            }
            while (running) {

                try {
                    deployLocalServices();
                    checkConnectFailedProviders();

                    TimeUnit.SECONDS.sleep(PUBLISH_CHECK_DELAY);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Registry main thread has exception.", e);
        }
    }

    private void checkConnectFailedProviders() {

        for (ConnectFailedProviderUrlHolder holder : connectedFailedProviders) {
            try {
                ProviderUrlConfig pUrl = holder.getProviderUrl();
                String beanName = holder.getBeanName();

                if (!pUrl.isLastConnectFailed()) {
                    connectedFailedProviders.remove(holder);
                    continue;
                }

                String domain = beanName.substring(0, beanName.indexOf("."));
                String path = buildPathStr(StoreConstants.SERVICES_HOME, "/", domain, "/", beanName, "/", StoreConstants.SERVICE_PROVIDERS, "/", URLEncoder.encode(pUrl.getUrl(), CHARSET));

                boolean exist = store.isPathExist(path);
                if (exist) {
                    pUrl.setLastConnectFailed(false);
                } else {
                    ServiceConfig service = serviceStore.get(beanName);
                    if (service != null) {
                        service.removeProviderUrl(pUrl);
                    }
                }
                connectedFailedProviders.remove(holder);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void deployServiceSubscribe(ServiceConfig service, String topic) throws StoreException {
        AppDomainContext.contextInitAwait();
        MessageStore ms = MessageCenter.getStore();
        if (ms != null) {
            ms.subscribe(topic, service);
        }
    }

    private void deployLocalServices() throws StoreException {
        ServiceConfig service = null;
        String domain = AppDomainContext.getName();
        while ((service = uploadQueue.poll()) != null) {

            String beanName = service.getId();
            String serviceDesc = service.getDesc();

            deployService(domain, beanName, serviceDesc, true);
            deployProviderUrl(service);

            //for subscribe
            String topic = service.getParameter("subscribe");
            if (!S.isEmpty(topic)) {
                deployServiceSubscribe(service, topic);
            }
        }
        MessageCenter.completeSubscriber();
    }


    private void prepareStore() throws StoreException {
        if (store == null) {
            throw new IllegalStateException("ActiveStore is not inited.registry start failed.");
        }
        store.connect();

        if (!store.isPathExist(StoreConstants.ROOT_DIR)) {
            store.createPath(StoreConstants.ROOT_DIR, null);
        }
        if (!store.isPathExist(StoreConstants.SERVICES_HOME)) {
            store.createPath(StoreConstants.SERVICES_HOME, null);
        }
        String domain = AppDomainContext.getName();
        domainServiceRoot = buildPathStr(StoreConstants.SERVICES_HOME, "/", domain);

        if (!store.isPathExist(domainServiceRoot)) {
            store.createPath(domainServiceRoot, null);
        }

        logger.info("registry is running,connected to server[" + store.getServerAddress() + "]");
    }

    private String buildPathStr(String... strings) {
        StringBuffer sb = new StringBuffer();
        for (String s : strings) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onDisconnected() {

    }

    public void add(ServiceConfig desc) {
        serviceStore.put(desc.getId(), desc);
    }
}
