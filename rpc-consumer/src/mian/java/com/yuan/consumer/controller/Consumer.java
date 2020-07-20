package com.yuan.consumer.controller;


import com.yuan.commons.proxy.MyInvocationHandler;
import com.yuan.consumer.service.SayService;

import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * @author by yuanlai
 * @Date 2020/7/16 11:29 上午
 * @Description: TODO
 * @Version 1.0
 */
public class Consumer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MyInvocationHandler invocationHandler = new MyInvocationHandler(SayService.class);
        // 构造代码实例

        /**
         * newProxyInstance(loader,interfaces,h)
         * loader:　　    一个ClassLoader对象，定义了由哪个ClassLoader对象来对生成的代理对象进行加载
         * interfaces:　　一个Interface对象的数组，表示的是我将要给我需要代理的对象提供一组什么接口，如果我提供了一组接口给它，那么这个代理对象就宣称实现了该接口(多态)，这样我就能调用这组接口中的方法了
         * h:　　         一个InvocationHandler对象，表示的是当我这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上
         */
        SayService serviceProxy = (SayService) Proxy.newProxyInstance(
                SayService.class.getClassLoader(),
                new Class<?>[]{SayService.class},
                invocationHandler
        );
        // 调用代理方法
        String result = serviceProxy.sayHello("yuan");

        System.out.println(result);
    }
}
