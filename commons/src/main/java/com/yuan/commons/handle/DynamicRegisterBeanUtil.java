package com.yuan.commons.handle;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author by yuanlai
 * @Date 2020/7/22 3:31 下午
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
@Component
public class DynamicRegisterBeanUtil implements BeanFactoryPostProcessor {

    /**
     * 动态注册Bean所需对象
     * 默认实现了ListableBeanFactory和BeanDefinitionRegistry接口，基于BeanDefinition对象，是一个成熟的Bean Factory。
     */
    private DefaultListableBeanFactory defaultListableBeanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

        this.defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;

//        01 获取指定路径下的所有类类型
        Reflections reflections = new Reflections("com.yuan.consumer.service");

//        02 获取标注了@Rest注解的类类型
        Set<Class<?>> typesAnnotatedWithRestApi = reflections.getTypesAnnotatedWith(RestApi.class);

//        03 为标注了@RestApi 注解的所有类类型创建代理类并将该代理类注册到IOC容器中去
        for (Class clazz : typesAnnotatedWithRestApi) {
            log.debug(clazz.getSimpleName());
            // 创建代理类的BeanDefinition并将其注册到IOC容器中去
            createProxyClass(clazz);

        }

    }

    /**
     * 创建代理类并将代理类注册到IOC容器中去
     * @param clazz 需要创建代理类的的类类型
     */
    private void createProxyClass(Class clazz)  {
        // 一、获取代理类对象的处理类
        MyInvocationHandler handler = getMyInvocationHandler(clazz);

        // 二、获取代理类的BeanDefinition（重点）
        BeanDefinition proxyBeanDefinition = getProxyBeanDefinition(clazz, handler);

        // 三、将代理类的Bean信息注册到Spring容器中
        registerBeanDefinition(clazz, proxyBeanDefinition);
    }


    private MyInvocationHandler getMyInvocationHandler(Class clazz) {
        return new MyInvocationHandler(clazz);
    }

    /**
     * 创建代理时所需的处理类
     * @return
     */
    private class MyInvocationHandler implements InvocationHandler {

        private Class target;

        public MyInvocationHandler(Class target) {
            this.target = target;
        }

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


    /**
     * 获取JDK代理类的BeanDefinition：根据目标接口的类型和处理器实例获取目标类接口的JDK代理类的Bean定义
     * @param clazz
     * @param handler
     * @return
     */
    private BeanDefinition getProxyBeanDefinition(Class clazz, MyInvocationHandler handler) {
        // 获取JDK代理类的类型
        Class<?> jdkDynamicProxyClass = getJDKDynamicProxyClass(clazz);

        // 获取JDK代理类的Bean定义
        BeanDefinition jdkBeanDefinition = getJDKBeanDefinition(jdkDynamicProxyClass, handler);

        return jdkBeanDefinition;
    }

    /**
     * 获取JDK代理类的BeanDefinition：根据JDK代理类的类型和处理类实例创建JDK代理类的Bean定义
     * @param jdkDynamicProxyClass
     * @param handler
     */
    private BeanDefinition getJDKBeanDefinition(Class<?> jdkDynamicProxyClass, MyInvocationHandler handler) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(jdkDynamicProxyClass);

        AbstractBeanDefinition beanDefinition = builder
                .addConstructorArgValue(handler)
                .getBeanDefinition();

        beanDefinition.setAutowireCandidate(true);
        return beanDefinition;
    }

    /**
     * 获取JDK代理类的类类型
     * @param clazz
     */
    private Class<?> getJDKDynamicProxyClass(Class clazz) {
        Class<?> jdkProxyClass =  Proxy.getProxyClass(clazz.getClassLoader(), clazz);
        return jdkProxyClass;
    }


    /**
     * 将代理类的Bean信息注册到Spring容器中：Bean名称为接口名
     * @param clazz
     * @param proxyBeanDefinition
     */
    private void registerBeanDefinition(Class clazz, BeanDefinition proxyBeanDefinition) {
        this.defaultListableBeanFactory.registerBeanDefinition(clazz.getSimpleName(), proxyBeanDefinition);
    }
}
