package com.dubbo.util.converter.support;

import org.dom4j.Document;
import org.springframework.core.convert.converter.Converter;

/**
 * @description:
 * @author: zhuzz
 * @date: 2019/1/8 16:13
 */
public class DocumentToString implements Converter<Document, String> {

    @Override
    public String convert(Document source) {
        return source.asXML();
    }

}
