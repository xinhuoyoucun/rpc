package com.yuan.consumer.controller;


import com.yuan.commons.proxy.ProxyFactory;
import com.yuan.consumer.service.SayService;

import java.io.IOException;

/**
 * @author by yuanlai
 * @Date 2020/7/16 11:29 上午
 * @Description: TODO
 * @Version 1.0
 */
public class Consumer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 创建代理对象
        SayService serviceProxy = (SayService) new ProxyFactory(SayService.class).getProxyInstance();

        // 调用代理方法
        String result = serviceProxy.sayHello("yuan");

        System.out.println(result);
    }
}
