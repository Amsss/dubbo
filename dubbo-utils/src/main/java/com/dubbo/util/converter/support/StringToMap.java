package com.dubbo.util.converter.support;

import com.dubbo.util.JSONUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class StringToMap implements Converter<String,Map> {
	
	@Override
	public Map convert(String source) {
		return JSONUtils.parse(source, Map.class);
	}

}
