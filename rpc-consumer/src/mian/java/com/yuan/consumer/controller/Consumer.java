package com.yuan.consumer.controller;


import com.yuan.consumer.service.SayService;
import com.yuan.consumer.service.impl.SayServiceImpl;

import java.io.IOException;

/**
 * @author by yuanlai
 * @Date 2020/7/16 11:29 上午
 * @Description: TODO
 * @Version 1.0
 */
public class Consumer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SayService sayService=new SayServiceImpl();
        String result = sayService.sayHello("yuan");
        System.out.println(result);
    }
}
