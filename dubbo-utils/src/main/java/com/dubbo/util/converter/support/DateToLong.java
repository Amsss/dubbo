package com.dubbo.util.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @description:
 * @author: zhuzz
 * @date: 2019/1/8 16:12
 */
public class DateToLong implements Converter<Date, Long> {

    @Override
    public Long convert(Date source) {
        return source.getTime();
    }

}
