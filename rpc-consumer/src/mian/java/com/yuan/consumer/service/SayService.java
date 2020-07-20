package com.yuan.consumer.service;

import java.io.IOException;

/**
 * @author by yuanlai
 * @Date 2020/7/16 3:53 下午
 * @Description: TODO
 * @Version 1.0
 */
public interface SayService {
    String sayHello(String name) throws IOException, ClassNotFoundException;
}
