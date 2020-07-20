package com.yuan.provider.controller;

import com.yuan.provider.service.SayService;
import com.yuan.provider.service.impl.SayServiceImpl;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * @author by yuanlai
 * @Date 2020/7/16 11:29 上午
 * @Description: TODO
 * @Version 1.0
 */
public class Provider {
    private static final HashMap<String, Class> serviceRegistry = new HashMap<String, Class>();

    public static void main(String[] args) throws Exception {
        Provider provider = new Provider();
        provider.serviceRegistry();
        provider.serviceProvider();
    }


    private void serviceRegistry() {
        serviceRegistry.put(SayService.class.getSimpleName(), SayServiceImpl.class);
    }

    private void serviceProvider() throws Exception {
        ServerSocket serverSocket = new ServerSocket(13000);
        ObjectInputStream objectInputStream = null;
        Socket socket = null;
        try {
            while (true) {
                socket = serverSocket.accept();
                // 将请求反序列化
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String className = objectInputStream.readUTF();
                String methodName = objectInputStream.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) objectInputStream.readObject();
                Object[] arguments = (Object[]) objectInputStream.readObject();

                // 调用服务
                Class serviceClass = serviceRegistry.get(className);
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                Object result = method.invoke(serviceClass.newInstance(), arguments);

                System.out.println("结果："+result);
                // 返回结果
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(result);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(objectInputStream != null){
                objectInputStream.close();
            }
            if(socket != null){
                socket.close();
            }
            if(serverSocket != null){
                serverSocket.close();
            }
        }
    }

}
