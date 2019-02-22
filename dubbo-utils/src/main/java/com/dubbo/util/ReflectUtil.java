package com.dubbo.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/416:24
 */
public class ReflectUtil {


    public static boolean isCompatible(Class<?> c, Object o) {
        boolean pt = c.isPrimitive();
        if (o == null) {
            return !pt;
        }

        if (pt) {
            if (c == int.class) {
                c = Integer.class;
            } else if (c == boolean.class) {
                c = Boolean.class;
            } else if (c == long.class) {
                c = Long.class;
            } else if (c == float.class) {
                c = Float.class;
            } else if (c == double.class) {
                c = Double.class;
            } else if (c == char.class) {
                c = Character.class;
            } else if (c == byte.class) {
                c = Byte.class;
            } else if (c == short.class) {
                c = Short.class;
            }
        }
        if (c == o.getClass()) {
            return true;
        }
        return c.isInstance(o);
    }

    public static String getCodeBase(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        ProtectionDomain domain = cls.getProtectionDomain();
        if (domain == null) {
            return null;
        }
        CodeSource source = domain.getCodeSource();
        if (source == null) {
            return null;
        }
        URL location = source.getLocation();
        if (location == null) {
            return null;
        }
        return location.getFile();
    }

    public static String getName(Class<?> c) {
        if (c.isArray()) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append("[]");
                c = c.getComponentType();
            }
            while (c.isArray());
            return c.getName() + sb.toString();
        }
        return c.getName();
    }

    public static String getName(final Method m) {
        StringBuilder ret = new StringBuilder();
        ret.append(getName(m.getReturnType())).append(' ');
        ret.append(m.getName()).append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                ret.append(',');
            }
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }

    public static String getName(final Constructor<?> c) {
        StringBuilder ret = new StringBuilder("(");
        Class<?>[] parameterTypes = c.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                ret.append(',');
            }
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }

    public static String getAllMethodNames(Class<?> c) {
        StringBuffer sb = new StringBuffer();
        Method[] methods = c.getDeclaredMethods();
        for (Method m : methods) {
            sb.append(getName(m)).append(";");
        }
        return sb.toString();
    }
}
