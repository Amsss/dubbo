package com.dubbo;

import org.dozer.DozerBeanMapper;
import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:36
 */
public class BeanUtils {

    private static DozerBeanMapper dozer = new DozerBeanMapper();

    public static <T> T map(Object source, Class<T> destinationClass) {
        return dozer.map(source, destinationClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T map(Object source, Object dest) {
        dozer.map(source, dest);
        return (T) dest;
    }

    public static void copy(Object source, Object dest) {
        dozer.map(source, dest);
    }

    public static Object getProperty(Object bean, String nm) throws Exception {
        Object val = null;
        val = MVEL.getProperty(nm, bean);
        return val;
    }

    public static void setProperty(Object bean, String nm, Object v) throws Exception {
        MVEL.setProperty(bean, nm, v);
    }

    public static void setPropertyInMap(Object bean, String nm, Object v) throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("key", nm);
        vars.put("value", v);
        MVEL.eval("setProperty(key,value)", bean, vars);
    }

}
