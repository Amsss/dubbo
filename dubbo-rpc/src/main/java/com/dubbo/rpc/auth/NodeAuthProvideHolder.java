package com.dubbo.rpc.auth;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/417:02
 */
public class NodeAuthProvideHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeAuthProvideHolder.class);
    private static NodeAuthProvide provide;
    public static void setNodeAuthProvide(String nodeAuthProvide) {
        if(StringUtils.isEmpty(nodeAuthProvide)){
            return;
        }
        try {
            provide = (NodeAuthProvide) Class.forName(nodeAuthProvide).newInstance();
            LOGGER.info("The NodeAuthProvide is [{}]", nodeAuthProvide);
        } catch (Exception e) {
            LOGGER.error("NodeAuthProvide [{}] load failed.",nodeAuthProvide);
        }
    }
    public static boolean auth(String host){
        if(provide == null){
            provide = new DefaultNodeAuthProvide();
        }
        return provide.auth(host);
    }
}
