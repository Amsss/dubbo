package com.dubbo.util.converter.support;

import com.dubbo.util.NetUtils;
import org.springframework.core.convert.converter.Converter;

import java.net.InetSocketAddress;

public class StringToInetSocketAddress implements Converter<String,InetSocketAddress> {
	
	@Override
	public InetSocketAddress convert(String source) {
		return NetUtils.toAddress(source);
	}

}
