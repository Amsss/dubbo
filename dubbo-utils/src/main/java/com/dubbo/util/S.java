package com.dubbo.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/416:40
 */
public class S extends StringUtils {
    public static String obj2String(Object o){
        return o==null?null:String.valueOf(o);
    }
}
