package com.yuan.consumer.service;

import com.yuan.commons.handle.RestApi;

import java.io.IOException;

/**
 * @author by yuanlai
 * @Date 2020/7/16 3:53 下午
 * @Description: TODO
 * @Version 1.0
 */
@RestApi
public interface SayService {
    String sayHello(String name) throws IOException, ClassNotFoundException;
}
