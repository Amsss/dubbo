package com.dubbo.spring.parse;

import com.dubbo.rpc.beans.ServiceBean;
import com.dubbo.spring.AppDomainContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/9/2720:22
 */
public class ServiceBeanDefinitionParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition def = new RootBeanDefinition();
        def.setBeanClass(ServiceBean.class);
        def.setInitMethodName("deploy");

        String id = element.getAttribute("id");
        String ref = element.getAttribute("ref");
        String clz = element.getAttribute("class");
        String weights = element.getAttribute("weights");
        String subscribe = element.getAttribute("subscribe");
        String mockClass = element.getAttribute("mockClass");

        RuntimeBeanReference reference = null;
        MutablePropertyValues pv = def.getPropertyValues();

        if(StringUtils.isEmpty(id)){
            if(StringUtils.isEmpty(ref)){
                throw new IllegalStateException("service @id not defeined,@ref must not be null");
            }
            id = ref;
            reference = new RuntimeBeanReference(ref);
        }
        else{
            if(StringUtils.isEmpty(clz)){
                if(StringUtils.isEmpty(ref)){
                    throw new IllegalStateException("service[" + id + "] @ref not defeined,@class must not be null");
                }
                else{
                    reference = new RuntimeBeanReference(ref);
                }
            }
            else{
                reference = registryBean(parserContext,id,clz);
            }
        }

        String domain = AppDomainContext.getName();
        String beanName = domain + "." + id;
        parserContext.getRegistry().registerBeanDefinition(beanName, def);
        pv.add("id", beanName);
        pv.add("appDomain", domain);
        if(weights != null){
            pv.add("weights", weights);
        }
        if(subscribe != null){
            pv.add("subscribe", subscribe);
        }
        if (mockClass != null) {
            pv.add("mockClass", mockClass);
        }
        pv.add("ref", reference);
        return def;
    }

    private RuntimeBeanReference registryBean(ParserContext context,String id,String className){
        RootBeanDefinition def = new RootBeanDefinition();
        def.setBeanClassName(className);
        context.getRegistry().registerBeanDefinition(id, def);
        RuntimeBeanReference reference = new RuntimeBeanReference(id);
        return reference;
    }
}
