package com.dubbo.util.store;

import com.dubbo.util.store.listener.NodeListener;
import com.dubbo.util.store.listener.StateListener;

import java.util.List;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/416:30
 */
public interface ActiveStore {

    void connect();

    void connectingAwait();

    boolean isConnected();

    void close();

    String getServerAddress();

    void addStateListener(StateListener listener);

    void delete(String path) throws StoreException;

    boolean isPathExist(String path) throws StoreException;

    boolean isPathExist(String path, NodeListener listener) throws StoreException;

    List<String> getChildren(String path) throws StoreException;

    List<String> getChildren(String path, NodeListener listener) throws StoreException;

    void setData(String path, byte[] data) throws StoreException;

    byte[] getData(String path) throws StoreException;

    byte[] getData(String path, NodeListener listener) throws StoreException;

    void createTempPath(String path, byte[] data) throws StoreException;

    String createSeqTempPath(String path, byte[] data) throws StoreException;

    void createPath(String path, byte[] data) throws StoreException;

    String createSeqPath(String path, byte[] data) throws StoreException;
}
