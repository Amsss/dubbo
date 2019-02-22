package com.dubbo.util.acl;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018/10/416:19
 */
public class ACList<T> extends HashSet<T> {
    public static final int WHITE_LIST = 0;

    private static final long serialVersionUID = -8587154390178371024L;

    private final Lock lock = new ReentrantLock(false);
    private ACListType type;

    public ACList(ACListType type) {
        this.type = type;
    }

    public void setType(ACListType type) {
        this.type = type;
    }

    public ACListType getType() {
        return type;
    }

    @Override
    public boolean add(T o) {

        try {
            lock.lock();
            return super.add(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            lock.lock();
            return super.remove(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        try {
            lock.lock();
            return super.contains(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        try {
            lock.lock();
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            super.clear();
        } finally {
            lock.unlock();
        }
    }

    public boolean getAuthorizeValue(T o) {
        boolean exist = contains(o);
        if (type == ACListType.whiteList) {
            return exist;
        } else {
            return !exist;
        }
    }
}

