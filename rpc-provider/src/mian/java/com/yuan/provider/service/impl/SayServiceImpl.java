package com.yuan.provider.service.impl;


import com.yuan.provider.service.SayService;

/**
 * @author by yuanlai
 * @Date 2020/7/16 11:30 上午
 * @Description: TODO
 * @Version 1.0
 */
public class SayServiceImpl implements SayService {
    @Override
    public String sayHello(String text) {
        return "你好：".concat(text);
    }
}
