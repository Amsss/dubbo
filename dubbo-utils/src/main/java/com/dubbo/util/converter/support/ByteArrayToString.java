package com.dubbo.util.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.io.IOException;

public class ByteArrayToString implements Converter<byte[],String> {
	
	@Override
	public String convert(byte[] source) {
		try {
			return new String(source, "UTF-8");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
