package com.dubbo.util.context;

import com.dubbo.BeanUtils;
import com.dubbo.util.converter.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/417:25
 */
public class Context extends HashMap<String, Object> {
    private static final long serialVersionUID = 7821961495108859198L;
    public static final String APP_CONTEXT = "$applicationContext";
    public static final String WEB_SESSION = "$webSession";
    public static final String HTTP_REQUEST = "$httpRequest";
    public static final String HTTP_RESPONSE = "$httpResponse";
    public static final String DB_SESSION = "$dbSession";
    public static final String FROM_DOMAIN = "$fromDomain";
    public static final String CLIENT_IP_ADDRESS = "$ClientIpAddress";
    public static final String USER_ROLE_TOKEN = "$userRoleToken";
    public static final String REQUEST_APPNODE_DEEP = "$requestAppNodeDeep";
    public static final String REQUEST_UNIT_DEEP = "$requestUnitDeep";
    public static final String EXP_BEAN = "$exp";
    public static final String ENTITY_CONTEXT = "$r";
    public static final String USER = "user";

    private static String topCtxName;
    private static Context topCtx;

    public Context(String name, Context ctx) {
        topCtxName = name;
        topCtx = ctx;
        put(topCtxName, topCtx);
    }

    public Context() {
        if (topCtx != null) {
            put(topCtxName, topCtx);
        }
    }

    public Context(Map<String, Object> m) {
        super(m);
    }

    @Override
    public Object get(Object key) {
        if (containsKey(key)) {
            return super.get(key);
        } else {
            try {
                return BeanUtils.getProperty(this, (String) key);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public void set(String key, Object v) {
        try {
            BeanUtils.setProperty(this, key, v);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T get(Object key, Class<T> type) {
        Object result = get(key);
        return ConversionUtils.convert(result, type);
    }

    public void putCtx(String key, Object ctx) {
        put(key, ctx);
    }

    public String value(String name) {
        return get(name, String.class);
    }

    public boolean has(String key) {
        return containsKey(key);
    }

}