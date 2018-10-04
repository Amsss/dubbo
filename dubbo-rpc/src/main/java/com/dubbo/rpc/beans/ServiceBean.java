package com.dubbo.rpc.beans;

import com.dubbo.rpc.config.MethodConfig;
import com.dubbo.rpc.config.ServiceConfig;
import com.dubbo.rpc.registry.ServiceRegistry;
import com.dubbo.util.annotation.RpcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:50
 */
public class ServiceBean<T> extends ServiceConfig implements FactoryBean<T> {
    private static final long serialVersionUID = -496458391892972962L;
    protected T ref;

    @Override
    public T getObject() throws Exception {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    private void getMethodFromClassToMap(Class<?> c, Map<String,MethodConfig> methods){
        Method[] ms = c.getMethods();
        for(Method m : ms){
            if (m.isAnnotationPresent(RpcService.class) || m.isAnnotationPresent(RpcService.class)) {
                MethodConfig mc = new MethodConfig(m);
                String k = (mc.getDesc());
                if(!methods.containsKey(k)){
                    methods.put(k, mc);
                }
            }
        }
    }

    private void processRpcMethod(Class<?> c, Map<String,MethodConfig> methods){
        //process interface
        Class<?>[] interfaces = c.getInterfaces();
        for(Class<?> cls:interfaces){
            getMethodFromClassToMap(cls, methods);
        }
        //process superclass
        Class<?> cls = c.getSuperclass();
        if(cls != null){
            getMethodFromClassToMap(cls, methods);
            processRpcMethod(cls, methods);
        }
        //process self
        getMethodFromClassToMap(c, methods);
    }

    public void deploy() {
        if (ref == null) {
            throw new IllegalStateException("service ref object not setup.");
        }
        Class<?> c = ref.getClass();

        processRpcMethod(c, methods);

        if (methods.size() > 0) {
            ServiceRegistry.publish(this);
            String subscribe = this.getParameter("subscribe");
            if (!StringUtils.isEmpty(subscribe)) {
                if (!methods.containsKey("void onSubscribeMessage(java.lang.Object)") && !methods.containsKey("void onSubscribeMessage(java.lang.String)")) {
                    throw new IllegalStateException(
                            "service["
                                    + id
                                    + "] has @subscribe,but [void onSubscribeMessage(java.lang.Object)] method not defined.");
                }
            }
        } else {
            throw new IllegalStateException(
                    "service["
                            + id
                            + "] has no method with annotation\'@RpcService\'.its meanless to defined as service");
        }
    }

    public void setWeights(String weights) {
        setParameter("weights", weights);
    }

    public void setSubscribe(String subscribe) {
        setParameter("subscribe", subscribe);
    }

    public void setSubscribeWay(String way) {
        setParameter("subscribeWay", way);
    }

    public void setMockClass(String mockClass) {
        setParameter("mockClass", mockClass);
    }

    @Override
    public Class<?> getObjectType() {
        String mockClass = getParameter("mockClass");
        if (mockClass != null) {
            try {
                return Class.forName(mockClass);
            } catch (ClassNotFoundException e) {
                throw new NoClassDefFoundError(mockClass);
            }
        }
        if (ref != null) {
            return ref.getClass();
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}

