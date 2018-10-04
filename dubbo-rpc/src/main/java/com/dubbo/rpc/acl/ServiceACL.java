package com.dubbo.rpc.acl;

import com.dubbo.util.acl.ACList;
import com.dubbo.util.acl.ACListType;

import java.util.List;

/**
 * @author: zhuzz
 * @description:
 * @date: 2018/10/416:15
 */
public class ServiceACL {

    private ACList<ServiceACLItem> ACLs;

    public ServiceACL(){
        ACLs = new ACList<ServiceACLItem>(ACListType.whiteList);
    }

    public ServiceACL(ACListType type){
        ACLs = new ACList<ServiceACLItem>(type);
    }

    public void setType(ACListType type){
        ACLs.setType(type);
    }

    public boolean add(String method,String domain,String ipAddress){
        ServiceACLItem item = new ServiceACLItem(method,domain,ipAddress);
        return ACLs.add(item);
    }

    public boolean remove(String method,String domain,String ipAddress){
        ServiceACLItem item = new ServiceACLItem(method,domain,ipAddress);
        return ACLs.remove(item);
    }

    public void updateAll(List<ServiceACLItem> ls){
        if(ls.size() == 0){
            ACLs.clear();
        }
        for(ServiceACLItem i : ls){
            ACLs.add(i);
        }
        for(ServiceACLItem i : ACLs){
            if(!ls.contains(i)){
                ACLs.remove(i);
            }
        }
    }


    public boolean getAuthorizeValue(String method,String domain,String ipAddress){
        if(ACLs.size() == 0){
            return true;
        }
        ServiceACLItem item = new ServiceACLItem(method,domain,ipAddress);

        boolean result = false;
        result = ACLs.getAuthorizeValue(item);
        if(!result){
            item = new ServiceACLItem(method,domain,null);
            result = ACLs.getAuthorizeValue(item);
            if(!result){
                item = new ServiceACLItem(null,domain,null);
                result = ACLs.getAuthorizeValue(item);
            }
        }
        return result;
    }
}
