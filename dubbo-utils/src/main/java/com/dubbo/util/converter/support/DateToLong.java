package com.dubbo.util.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class DateToLong implements Converter<Date,Long> {

	@Override
	public Long convert(Date source) {
		return source.getTime();
	}

}
