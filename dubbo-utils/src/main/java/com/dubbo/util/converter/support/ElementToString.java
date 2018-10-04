package com.dubbo.util.converter.support;

import org.dom4j.Element;
import org.springframework.core.convert.converter.Converter;

public class ElementToString implements Converter<Element,String> {
	
	@Override
	public String convert(Element source) {
		return source.asXML();
	}

}
