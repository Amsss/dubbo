package com.dubbo.spring.parse;

import com.dubbo.spring.AppDomainContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/9/2720:22
 */
public class ApplicationDomainBeanDefinitionParser implements BeanDefinitionParser {

    private static AtomicBoolean isInitApplicationDomainBean = new AtomicBoolean(false);

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (isInitApplicationDomainBean.get()) {
            throw new IllegalStateException("Duplicate definition domain name at <dubbo:application>, it allows appear once only.");
        }
        isInitApplicationDomainBean.set(true);
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(AppDomainContext.class);
        String name = element.getAttribute("name");
        AppDomainContext.setName(name);
        String id = element.getAttribute("id");
        if (StringUtils.isEmpty(id)) {
            id = name;
        }
        boolean enableLogger = Boolean.parseBoolean(element.getAttribute("enableLogger"));
        String registryAddress = element.getAttribute("registryAddress");
        String rpcServerWorkUrl = element.getAttribute("rpcServerWorkUrl");
        AppDomainContext.setRpcServerWorkUrl(rpcServerWorkUrl);
        AppDomainContext.setRegistryAddress(registryAddress);
        AppDomainContext.setEnableLogger(enableLogger);
        parserContext.getRegistry().registerBeanDefinition("$domain-" + "application", rootBeanDefinition);
        return rootBeanDefinition;
    }
}
