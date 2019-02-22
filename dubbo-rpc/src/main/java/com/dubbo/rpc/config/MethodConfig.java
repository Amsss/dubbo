package com.dubbo.rpc.config;

import com.dubbo.util.ClassHelper;
import com.dubbo.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/415:45
 */
public class MethodConfig implements Config {
    private static final long serialVersionUID = 7032839928190825018L;
    private String methodName = "";
    private List<ParameterConfig> parameters = new ArrayList<ParameterConfig>();
    private ParameterConfig returnType;
    private String desc;

    public MethodConfig(String name) {
        methodName = name;
    }

    public MethodConfig(Method m) {
        methodName = m.getName();
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (Class<?> type : parameterTypes) {
            addParameter(type);
        }
        returnType = new ParameterConfig(0, m.getReturnType());
    }

    public String getName() {
        return methodName;
    }

    public void addParameter(Class<?> clz) {
        int i = parameters.size();
        ParameterConfig p = new ParameterConfig(i, clz);
        parameters.add(p);
    }

    public void addParameter(String className) throws ClassNotFoundException {
        addParameter(ClassHelper.forName(className));
    }

    public void setReturnType(Class<?> clz) {
        returnType = new ParameterConfig(0, clz);
    }

    public void setReturnType(String className) throws ClassNotFoundException {
        setReturnType(ClassHelper.forName(className));
    }

    public int getParameterCount() {
        return parameters.size();
    }

    public Class<?>[] getParameterTypes() {
        int n = parameters.size();

        Class<?>[] types = new Class<?>[n];

        int i = 0;
        for (ParameterConfig p : parameters) {
            types[i] = p.getTypeClass();
            i++;
        }
        return types;
    }

    public String[] getParamterTypeNames() {
        int n = parameters.size();
        if (n == 0) {
            return null;
        }
        String[] typeNames = new String[n];

        int i = 0;
        for (ParameterConfig p : parameters) {
            typeNames[i] = p.getTypeName();
            i++;
        }
        return typeNames;
    }

    public boolean isCompatible(Object[] args) {
        boolean result = true;
        if (args == null && parameters.size() == 0) {
            return result;
        }
        if (args != null && args.length == parameters.size()) {
            int i = 0;
            for (ParameterConfig p : parameters) {
                Object o = args[i];
                if (!ReflectUtil.isCompatible(p.getTypeClass(), o)) {
                    result = false;
                    break;
                }
                i++;
            }
        } else {
            result = false;
        }
        return result;
    }

    public String getDesc() {
        if (desc != null) {
            return desc;
        } else {
            StringBuffer sb = new StringBuffer(returnType.getTypeName());
            sb.append(" ").append(methodName).append("(");
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append(parameters.get(i).getTypeName());
            }
            desc = sb.append(")").toString();
        }
        return desc;
    }

}
