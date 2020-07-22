package com.yuan.consumer.controller;


import com.yuan.commons.proxy.ProxyFactory;
import com.yuan.consumer.service.SayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author by yuanlai
 * @Date 2020/7/16 11:29 上午
 * @Description: TODO
 * @Version 1.0
 */

@Slf4j
@RestController
public class Consumer {

    @Autowired
    private SayService sayService;

    @RequestMapping("/say")
    public String say() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        SayService serviceProxy = (SayService) new ProxyFactory(SayService.class).getProxyInstance();
        String result = serviceProxy.sayHello("yuan");
        return result;
    }

}
