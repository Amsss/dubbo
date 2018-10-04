package com.dubbo.rpc.invoker;

import com.caucho.hessian.client.HessianProxyFactory;
import com.dubbo.rpc.Invocation;
import com.dubbo.rpc.Result;
import com.dubbo.rpc.ServiceDispatcher;
import com.dubbo.rpc.exception.RpcException;
import java.net.MalformedURLException;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:28
 */
public class HessianInvoker extends AbstractInvoker {
    private String url;
    private HessianProxyFactory factory = new HessianProxyFactory();
    private ServiceDispatcher dispatcher;


    public HessianInvoker(String url) {
        if (url.startsWith("hessian")) {
            this.url = url.replaceAll("hessian", "http");
        } else {
            this.url = url;
        }
        try {

            factory.setConnectTimeout(ConnectTimeout);
            factory.setReadTimeout(ReadTimeout);
            dispatcher = (ServiceDispatcher) factory.create(ServiceDispatcher.class, this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Object call(Invocation invocation) throws Exception {

        if (dispatcher == null) {
            throw new RpcException(401, "DispatcherNotInited");
        }
        try {
            Result result = dispatcher.invoke(invocation);
            result.throwExpceptionIfHas();
            return result.getValue();
        } catch (Exception e) {
            Throwable t = e.getCause();
            if (t != null && t instanceof java.net.ConnectException) {
                throw new RpcException(RpcException.CONNECT_FALIED, t);
            } else {
                throw e;
            }
        } catch (Throwable e) {
            throw new RpcException(RpcException.UNKNOWN, e);
        }
    }

}
