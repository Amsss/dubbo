package com.dubbo.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/416:40
 */
public class S extends StringUtils {
    public static String obj2String(Object o){
        return o==null?null:String.valueOf(o);
    }
}
