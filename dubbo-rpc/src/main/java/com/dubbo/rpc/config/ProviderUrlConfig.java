package com.dubbo.rpc.config;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/415:44
 */
public class ProviderUrlConfig implements Config {

    private static final long serialVersionUID = -4795468642059609441L;
    private static Pattern pattern = Pattern.compile("([a-zA-Z\\.0-9]+\\:[0-9]+)");

    private String url;
    private String host;
    private String protocol;
    private HashMap<String, String> parameters = new HashMap<String, String>();
    private int weights;
    private AtomicInteger successes = new AtomicInteger(0);
    private AtomicInteger failures = new AtomicInteger(0);
    private AtomicBoolean lastConnectFailed = new AtomicBoolean(false);

    public ProviderUrlConfig(String url) {
        this.url = url;

        int i = url.indexOf(":");
        if (i > 0) {
            protocol = url.substring(0, i);
        }
        i = url.indexOf("?");
        if (i > 0) {
            String s = url.substring(i + 1);
            String[] args = s.split("&");
            for (String t : args) {
                String[] pm = t.split("=");
                if (pm.length == 2) {
                    parameters.put(pm[0], pm[1]);
                }
            }
        }
        host = parseHostFromUrl(url);
    }

    public static String parseHostFromUrl(String url) {
        Matcher m = pattern.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getUrl() {
        return url;
    }

    public int getWeights() {
        return weights;
    }

    public void increaseInvokeCount(boolean success) {
        if (success) {
            successes.incrementAndGet();
        } else {
            failures.incrementAndGet();
        }
    }

    public int getSuccesseCount() {
        return successes.get();
    }

    public int getFalureCount() {
        return failures.get();
    }

    public int getInvokeCount() {
        return successes.get() + failures.get();
    }

    public boolean equals(Object o) {
        if (o == null || !this.getClass().isInstance(o)) {
            return false;
        }
        return url.equals(((ProviderUrlConfig) o).getUrl());
    }

    public void setLastConnectFailed(boolean status) {
        lastConnectFailed.set(status);
    }

    public boolean isLastConnectFailed() {
        return lastConnectFailed.get();
    }
}
