package com.dubbo.rpc.config;

import com.dubbo.util.ReflectUtil;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/415:45
 */
public class ParameterConfig implements Config {

    private static final long serialVersionUID = 5686830350496073242L;
    private int index;
    private Class<?> type;
    private String typeName;

    public ParameterConfig(int index, Class<?> type) {
        this.type = type;
        this.index = index;
        typeName = ReflectUtil.getName(type);
    }

    public Class<?> getTypeClass() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getIndex() {
        return index;
    }
}
