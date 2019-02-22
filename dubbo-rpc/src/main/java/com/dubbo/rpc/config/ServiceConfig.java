package com.dubbo.rpc.config;

import com.dubbo.rpc.acl.ServiceACL;
import com.dubbo.rpc.acl.ServiceACLItem;
import com.dubbo.util.acl.ACListType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/415:43
 */
public class ServiceConfig implements Config {

    private static final long serialVersionUID = -1079333626088246955L;
    private static final Log logger = LogFactory.getLog(ServiceConfig.class);
    private static Pattern pattern;

    protected LinkedHashMap<String, MethodConfig> methods = new LinkedHashMap<String, MethodConfig>();
    protected CopyOnWriteArraySet<ProviderUrlConfig> providerUrls = new CopyOnWriteArraySet<ProviderUrlConfig>();
    protected HashMap<String, String> parameters = new HashMap<String, String>();
    protected ServiceACL ACL = new ServiceACL();

    protected String domain;
    protected String id;
    protected String desc;

    static {
        String re = "([\\.a-zA-Z0-9]+) ([a-zA-Z0-9\\[\\]]+)\\(([\\.a-zA-Z0-9\\[\\],]*)\\);";
        pattern = Pattern.compile(re);
    }

    public void setId(String v) {
        id = v;
    }

    public String getId() {
        return id;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public void setParameter(String name, String v) {
        if (StringUtils.isEmpty(v)) {
            return;
        }
        parameters.put(name, v);
    }

    public void setAppDomain(String domain) {
        this.domain = domain;
    }

    public String getAppDomain() {
        return domain;
    }

    public void addMethod(MethodConfig mc) {
        methods.put(mc.getDesc(), mc);
    }

    public List<MethodConfig> getMethods() {
        List<MethodConfig> ls = new ArrayList<MethodConfig>();
        Collection<MethodConfig> c = methods.values();
        for (MethodConfig m : c) {
            ls.add(m);
        }
        return ls;
    }

    public MethodConfig getMethodByDesc(String desc) {
        return methods.get(desc);
    }

    public List<MethodConfig> getMethodByName(String name) {
        List<MethodConfig> methodConfigs = null;
        Collection<MethodConfig> c = methods.values();
        for (MethodConfig m : c) {
            if (m.getName().equals(name)) {
                if (methodConfigs == null) {
                    methodConfigs = new ArrayList<MethodConfig>();
                }
                methodConfigs.add(m);
            }
        }
        return methodConfigs;
    }

    public String getDesc() {
        if (desc != null) {
            return desc;
        }
        StringBuffer sb = new StringBuffer("[").append(id).append("]\n");
        Collection<MethodConfig> c = methods.values();
        for (MethodConfig m : c) {
            sb.append(m.getDesc()).append(";\n");
        }
        int n = parameters.size();
        if (parameters.size() > 0) {
            sb.append("?");
            Set<String> names = parameters.keySet();
            int i = 0;
            for (String nm : names) {
                sb.append(nm).append("=").append(parameters.get(nm));
                if (i < n - 1) {
                    sb.append("&");
                }
                i++;
            }
        }
        desc = sb.toString();
        return desc;
    }

    public MethodConfig getCompatibleMethod(String methodName, Object[] args) {

        Collection<MethodConfig> c = methods.values();
        for (MethodConfig m : c) {
            if (m.getName().equals(methodName) && m.isCompatible(args)) {
                return m;
            }
        }
        return null;
    }

    public void addProviderUrl(String url) {
        providerUrls.add(new ProviderUrlConfig(url));
    }

    public void updateACL(ACListType type, List<String> ls) {
        ACL.setType(type);
        List<ServiceACLItem> items = new ArrayList<ServiceACLItem>();
        if (ls != null) {
            for (String s : ls) {
                items.add(new ServiceACLItem(s));
            }
        }
        ACL.updateAll(items);
    }

    public ServiceACL getACL() {
        return ACL;
    }

    public void updateProviderUrls(List<ProviderUrlConfig> urls) {

        if (urls.size() == 0) {
            providerUrls.clear();
            logger.warn("service[" + id + "] all providers offline.");
        }

        for (ProviderUrlConfig pUrl : urls) {
            if (providerUrls.add(pUrl)) {
                logger.info("service[" + id + "@" + pUrl.getHost() + "] online.");
            }
        }

        for (ProviderUrlConfig pUrl : providerUrls) {
            if (!urls.contains(pUrl)) {
                providerUrls.remove(pUrl);
                logger.info("service[" + id + "@" + pUrl.getHost() + "] offline");
            }
        }
    }

    public void removeProviderUrl(String url) {
        providerUrls.remove(new ProviderUrlConfig(url));
    }

    public void removeProviderUrl(ProviderUrlConfig pUrl) {
        providerUrls.remove(pUrl);
    }

    public List<ProviderUrlConfig> getProviderUrls() {
        List<ProviderUrlConfig> ls = new ArrayList<ProviderUrlConfig>();
        for (ProviderUrlConfig urlc : providerUrls) {
            if (!urlc.isLastConnectFailed()) {
                ls.add(urlc);
            }
        }
        return ls;
    }

    public int getProvidersCount() {
        return providerUrls.size();
    }

    @Override
    public boolean equals(Object v) {
        if (v == null || !ServiceConfig.class.isInstance(v)) {
            return false;
        }
        ServiceConfig o = (ServiceConfig) v;

        if (!o.getId().equals(id)) {
            return false;
        }
        List<MethodConfig> ls = o.getMethods();
        if (ls.size() != methods.size()) {
            return false;
        }
        for (MethodConfig m : ls) {
            if (!methods.containsKey(m.getDesc())) {
                return false;
            }
        }
        return true;
    }

    public static ServiceConfig parse(String s) throws ClassNotFoundException {

        int p1 = s.indexOf(".");
        int p2 = s.indexOf("]");

        String domain = s.substring(1, p1);
        String beanName = s.substring(1, p2);

        ServiceConfig service = new ServiceConfig();
        service.setAppDomain(domain);
        service.setId(beanName);

        Matcher m = pattern.matcher(s);
        while (m.find()) {
            int count = m.groupCount();
            if (count == 3) {
                String returnType = m.group(1);
                String methodName = m.group(2);
                String[] parameters = m.group(3).split(",");

                MethodConfig method = new MethodConfig(methodName);
                for (String clz : parameters) {
                    if (StringUtils.isEmpty(clz)) {
                        continue;
                    }
                    method.addParameter(clz);
                }
                method.setReturnType(returnType);
                service.addMethod(method);
            }
        }
        //for service args
        int p = s.indexOf("?");
        if (p > 0) {
            String q = s.substring(p + 1);
            String[] args = q.split("\\&");
            for (String t : args) {
                String[] pm = t.split("=");
                if (pm.length == 2) {
                    service.setParameter(pm[0], pm[1]);
                }
            }
        }

        return service;
    }
}
