package com.yuan.commons.handler;

import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by yuanlai
 * @Date 2020/7/22 1:50 下午
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
public class MyInvocationHandler implements InvocationHandler {


    /**
     * 代理执行方法：利用代理类实例执行目标类的方法都会进入到invoke方法中执行
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> addressList = lookupProviders("say.hello");
        String address = chooseTarget(addressList);
        String ip = address.split(",")[0];
        int port = Integer.parseInt(address.split(",")[1]);
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            socket = new Socket(ip, port);

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // 将请求发给服务提供方
            objectOutputStream.writeUTF(proxy.getClass().getInterfaces()[0].getSimpleName());
            objectOutputStream.writeUTF(method.getName());
            objectOutputStream.writeObject(method.getParameterTypes());
            objectOutputStream.writeObject(args);

            // 将响应体反序列化
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object response = objectInputStream.readObject();


            if (response instanceof String) {
                return response;
            } else {
                throw new InternalError();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        return null;
    }

    /**
     * 选择服务地址
     *
     * @param providers
     * @return
     */
    private String chooseTarget(List<String> providers) {
        if (null == providers || providers.size() == 0) {
            throw new IllegalArgumentException();
        }
        return providers.get(0);
    }

    /**
     * 发现服务
     *
     * @param name
     * @return
     */
    private List<String> lookupProviders(String name) {
        List<String> strings = new ArrayList();
        strings.add("127.0.0.1,13000");
        return strings;
    }
}
