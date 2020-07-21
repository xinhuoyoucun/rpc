package com.yuan.commons.proxy;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by yuanlai
 * @Date 2020/7/21 3:54 下午
 * @Description: TODO
 * @Version 1.0
 */
public class ProxyFactory {
    //维护一个目标对象
    private Class target;

    public ProxyFactory(Class target) {
        this.target = target;
    }


    /**
     * 给目标对象生成代理对象
     * newProxyInstance(loader,interfaces,h)
     * loader:　　    一个ClassLoader对象，定义了由哪个ClassLoader对象来对生成的代理对象进行加载
     * interfaces:　　一个Interface对象的数组，表示的是我将要给我需要代理的对象提供一组什么接口，如果我提供了一组接口给它，那么这个代理对象就宣称实现了该接口(多态)，这样我就能调用这组接口中的方法了
     * h:　　         一个InvocationHandler对象，表示的是当我这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上
     */
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                target.getClassLoader(),
                new Class<?>[]{target},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        List<String> addressList = lookupProviders("say.hello");
                        String address = chooseTarget(addressList);
                        String ip = address.split(",")[0];
                        int port = Integer.parseInt(address.split(",")[1]);
                        Socket socket = null;
                        ObjectOutputStream objectOutputStream = null;
                        ObjectInputStream objectInputStream = null;
                        try{
                            socket = new Socket(ip, port);

                            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                            // 将请求发给服务提供方
                            objectOutputStream.writeUTF(target.getSimpleName());
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
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            if(objectOutputStream !=null){
                                objectOutputStream.close();
                            }
                            if(objectInputStream != null){
                                objectInputStream.close();
                            }
                            if(socket != null){
                                socket.close();
                            }
                        }
                        return null;
                    }
                }
        );
    }



    /**
     * 选择服务地址
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
     * @param name
     * @return
     */
    private static List<String> lookupProviders(String name) {
        List<String> strings = new ArrayList();
        strings.add("127.0.0.1,13000");
        return strings;
    }
}
