package com.dubbo.util.converter.support;

import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @description:
 * @author: zhuzz
 * @date: 2019/1/8 16:13
 */
public class DateToString implements Converter<Date, String> {

    private String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public String convert(Date source) {
        return DateTimeFormat.forPattern(DATETIME_FORMAT).print(source.getTime());
    }

}
