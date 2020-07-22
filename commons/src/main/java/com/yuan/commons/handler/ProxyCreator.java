package com.yuan.commons.handler;


import java.lang.reflect.InvocationTargetException;

public interface ProxyCreator {
    /**
     * 获取代理类实例
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    Object createProxyInstanceInfo() throws IllegalAccessException, InvocationTargetException, InstantiationException;

    /**
     * 获取代理类的类类型
     * @return
     */
    Class<?> getProxyClassInfo();
}