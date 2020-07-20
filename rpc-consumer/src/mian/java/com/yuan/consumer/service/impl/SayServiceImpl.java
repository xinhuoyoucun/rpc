package com.yuan.consumer.service.impl;

import com.yuan.consumer.service.SayService;
import com.yuan.request.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by yuanlai
 * @Date 2020/7/16 2:21 下午
 * @Description: TODO
 * @Version 1.0
 */
public class SayServiceImpl implements SayService {
    @Override
    public String sayHello(String name) throws IOException, ClassNotFoundException {
        List<String> addressList = lookupProviders("say.hello");
        String address = chooseTarget(addressList);
        String ip=address.split(",")[0];
        int port = Integer.parseInt(address.split(",")[1]);
        Socket socket = new Socket(ip,port);

        // 将请求序列化
        RpcRequest rpcRequest =new RpcRequest();
        rpcRequest.setMethod("say");
        rpcRequest.setName(name);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        // 将请求发给服务提供方
        objectOutputStream.writeObject(rpcRequest);

        // 将响应体反序列化
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Object response = objectInputStream.readObject();


        if (response instanceof String) {
            return (String) response;
        } else {
            throw new InternalError();
        }

    }




    //选择服务地址
    private String chooseTarget(List<String> providers) {
        if (null == providers || providers.size() == 0) {
            throw new IllegalArgumentException();
        }
        return providers.get(0);
    }

    //发现服务
    private static List<String> lookupProviders(String name) {
        List<String> strings = new ArrayList();
        strings.add("127.0.0.1,13000");
        return strings;
    }
}
