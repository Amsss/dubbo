package com.dubbo.util.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @description:
 * @author: zhuzz
 * @date: 2019/1/8 16:13
 */
public class LongToDate implements Converter<Long, Date> {

    @Override
    public Date convert(Long source) {
        return new Date(source);
    }

}
