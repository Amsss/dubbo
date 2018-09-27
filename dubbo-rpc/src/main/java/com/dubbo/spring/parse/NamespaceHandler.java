package com.dubbo.spring.parse;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/9/2719:44
 */
public class NamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("applicationDomain", new ApplicationDomainBeanDefinitionParser());
        registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser());
        registerBeanDefinitionParser("reference", new ReferenceBeanDefinitionParser());
    }
}
