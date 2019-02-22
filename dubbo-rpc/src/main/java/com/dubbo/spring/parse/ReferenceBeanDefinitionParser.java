package com.dubbo.spring.parse;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/9/2720:22
 */
public class ReferenceBeanDefinitionParser implements BeanDefinitionParser {
    private static final String REF = "ctd.net.rpc.beans.ReferenceBean";
    private static final String REF_DIRECT = "ctd.net.rpc.beans.DirectReferenceBean";

    @Override
    public BeanDefinition parse(Element el, ParserContext context) {
        RootBeanDefinition def = new RootBeanDefinition();
        MutablePropertyValues pv = def.getPropertyValues();
        NodeList remoteEls = el.getElementsByTagName("*");
        int n = remoteEls.getLength();
        if(n == 0){
            def.setBeanClassName(REF);
        }
        else{
            def.setInitMethodName("init");
            def.setBeanClassName(REF_DIRECT);
            if(n > 1){
                List<String> ls = new ArrayList<>();
                for(int i =0; i < n;i ++){
                    Element u = (Element) remoteEls.item(i);
                    String url = u.getAttribute("url");
                    ls.add(url);
                }
                pv.add("remoteUrls", ls);
            }
            else{
                Element u = (Element) remoteEls.item(0);
                if(u.hasAttribute("ref")){
                    RuntimeBeanReference reference = new RuntimeBeanReference(u.getAttribute("ref"));
                    pv.add("remoteUrls", reference);
                }
                else{
                    List<String> ls = new ArrayList<>();
                    String url = u.getAttribute("url");
                    ls.add(url);
                    pv.add("remoteUrls", ls);
                }
            }
        }
        String beanName = el.getAttribute("id");
        String interfaceClassName = el.getAttribute("interface");

        context.getRegistry().registerBeanDefinition(beanName, def);
        pv.add("id", beanName);
        pv.add("interface", interfaceClassName);

        return def;
    }
}
