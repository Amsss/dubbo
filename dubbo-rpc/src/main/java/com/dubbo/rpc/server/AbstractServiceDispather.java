package com.dubbo.rpc.server;


import com.dubbo.rpc.Invocation;
import com.dubbo.rpc.Result;
import com.dubbo.rpc.ServiceDispatcher;
import com.dubbo.rpc.config.MethodConfig;
import com.dubbo.rpc.config.ServiceConfig;
import com.dubbo.spring.AppDomainContext;
import com.dubbo.util.context.Context;
import com.dubbo.util.context.ContextUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbstractServiceDispather implements ServiceDispatcher {
	private static AbstractServiceDispather instance;

	protected AbstractServiceDispather() {

	}

	public static AbstractServiceDispather instance() {
		if (instance == null) {
			instance = new AbstractServiceDispather();
		}
		return instance;
	}

	@Override
	public Result invoke(Invocation invocation) {
		Result result = new Result();
		try {
			String beanName = invocation.getBeanName();
			String methodDesc = invocation.getMethodDesc();
			Object[] parameters = invocation.getParameters();

			Map<String, Object> headers = invocation.getAllHeaders();
			// init context
			Context ctx = new Context();
			Set<String> keys = headers.keySet();
			for (String k : keys) {
				ctx.put(k, headers.get(k));
			}
			ContextUtils.setContext(ctx);

			// start call
			Object v = call(beanName, methodDesc, parameters);
			result.setValue(v);
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause != null) {
				result.setException(cause);
			} else {
				result.setException(e);
			}
		} finally {
			ContextUtils.clear();
		}
		return result;
	}

	public Object call(String beanName, String methodDesc, Object[] parameters) throws Exception {

		ServiceConfig serviceConfig = (ServiceConfig) AppDomainContext.getBean("&" + beanName);
		MethodConfig methodConfig = serviceConfig.getMethodByDesc(methodDesc);
		if (methodConfig == null) {
			throw new IllegalStateException("service[" + beanName + "],method[" + methodDesc
					+ "] not defined");
		}
		return call(serviceConfig, methodConfig, parameters);
	}

	public Object call(ServiceConfig serviceConfig, MethodConfig methodConfig, Object[] parameters)
			throws Exception {
		String methodName = methodConfig.getName();
		Object bean = AppDomainContext.getBean(serviceConfig.getId());
		Class<?> clz = bean.getClass();
		Method m = clz.getMethod(methodName, methodConfig.getParameterTypes());
		Object v = m.invoke(bean, parameters);
		return v;
	}

}
