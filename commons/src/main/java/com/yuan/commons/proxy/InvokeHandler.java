package com.yuan.commons.proxy;


import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author by yuanlai
 * @Date 2020/7/20 12:53 下午
 * @Description: TODO
 * @Version 1.0
 */
public class InvokeHandler implements InvocationHandler, Serializable {

    private static final long serialVersionUID = 7162003009983904103L;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
