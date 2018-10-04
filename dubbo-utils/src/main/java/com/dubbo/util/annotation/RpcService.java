package com.dubbo.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: pc注解类
 * @author: zhuzz
 * @date: 2018/9/2719:10r
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcService {
    String executor() default "default";
}
