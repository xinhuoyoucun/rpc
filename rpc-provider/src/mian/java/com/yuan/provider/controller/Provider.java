package com.yuan.provider.controller;

import com.yuan.request.RpcRequest;
import com.yuan.provider.service.SayService;
import com.yuan.provider.service.impl.SayServiceImpl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author by yuanlai
 * @Date 2020/7/16 11:29 上午
 * @Description: TODO
 * @Version 1.0
 */
public class Provider {
    public static void main(String[] args) throws IOException {

        SayService sayService=new SayServiceImpl();
        ServerSocket serverSocket=new ServerSocket(13000);

        try {
            while (true) {

                Socket socket = serverSocket.accept();
                try {
                    // 将请求反序列化
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    Object object = objectInputStream.readObject();

                    // 调用服务
                    String result = null;
                    if (object instanceof RpcRequest) {
                        RpcRequest rpcRequest = (RpcRequest) object;
                        if ("say".equals(rpcRequest.getMethod())) {
                            result = sayService.sayHello(rpcRequest.getName());
                        } else {
                            throw new UnsupportedOperationException();
                        }
                    }

                    // 返回结果
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    socket.close();
                }
            }
        } finally {
            serverSocket.close();

        }




    }
}
