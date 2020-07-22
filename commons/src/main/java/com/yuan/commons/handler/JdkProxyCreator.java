package com.yuan.commons.handler;

import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * @author by yuanlai
 * @Date 2020/7/22 1:54 下午
 * @Description: JDK代理工具类
 * @Version 1.0
 */
@Data
public class JdkProxyCreator implements ProxyCreator {

    /**
     * 需要产生代理类的接口数组
     */
    private Class<?>[] interfaces;

    /**
     * 根据interfaces创建的动态代理类的类类型
     */
    private Class<?> proxyClass;

    /**
     * 根据interfaces创建的动态代理类的构造器
     */
    private Constructor<?> proxyConstructor;

    /**
     * 根据interfaces创建的动态代理类所需的处理器
     */
    private InvocationHandler invocationHandler;

    /**
     * 自定义构造器：
     * 根据获取到处理器实例创建JDK代理类的类类型
     * 并通过JDK代理类的类类型获取代理类的构造器
     *
     * @param interfaces        接口数组
     * @param invocationHandler 代理类的处理器
     * @throws NoSuchMethodException
     */
    public JdkProxyCreator(
            Class<?>[] interfaces,
            InvocationHandler invocationHandler
    ) throws NoSuchMethodException {
        this.interfaces = interfaces;
        this.invocationHandler = invocationHandler;

        // 创建代理类的类类型
        this.proxyClass = Proxy.getProxyClass(
                this.getClass().getClassLoader(),
                this.interfaces
        );

        // 根据代理类的类类型获取代理类的构造器
        this.proxyConstructor = this.proxyClass
                .getConstructor(InvocationHandler.class);

    }

    @Override
    public Object createProxyInstanceInfo() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return this.proxyConstructor.newInstance(this.invocationHandler);
    }

    @Override
    public Class<?> getProxyClassInfo() {
        return this.proxyClass;
    }
}
