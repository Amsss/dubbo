package com.dubbo.util.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @description:
 * @author: zhuzz
 * @date: 2019/1/8 16:13
 */
public class DateToNumber implements Converter<Date, Number> {

    @Override
    public Number convert(Date source) {
        return source.getTime();
    }

}
