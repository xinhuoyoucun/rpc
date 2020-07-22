package com.yuan.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author by yuanlai
 * @Date 2020/7/21 4:53 下午
 * @Description: TODO
 * @Version 1.0
 */
@SpringBootApplication(scanBasePackages = "com.yuan")
public class ConsumerApplication  {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class,args);
    }

}
