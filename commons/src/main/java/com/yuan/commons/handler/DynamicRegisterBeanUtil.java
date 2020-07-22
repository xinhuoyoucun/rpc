package com.yuan.commons.handler;

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
            try {
                createProxyClass(clazz);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 创建代理类并将代理类注册到IOC容器中去
     * @param clazz 需要创建代理类的的类类型
     * @throws NoSuchMethodException
     */
    private void createProxyClass(Class clazz) throws NoSuchMethodException {
        // 一、创建代理类对象
        MyInvocationHandler handler = getMyInvocationHandler();

        // 二、获取代理类的Bean定义
        BeanDefinition proxyBeanDefinition = getProxyBeanDefinition(clazz, handler);

        // 三、将代理类的Bean信息注册到Spring容器中
        registerBeanDefinition(clazz, proxyBeanDefinition);
    }

    /**
     * 将代理类的Bean信息注册到Spring容器中：Bean名称为接口名
     * @param clazz
     * @param proxyBeanDefinition
     */
    private void registerBeanDefinition(Class clazz, BeanDefinition proxyBeanDefinition) {
        this.defaultListableBeanFactory.registerBeanDefinition(clazz.getSimpleName(), proxyBeanDefinition);
    }

    /**
     * 获取JDK代理类的Bean定义：根据目标接口的类型和处理器实例获取目标类接口的JDK代理类的Bean定义
     * @param clazz
     * @param handler
     * @return
     * @throws NoSuchMethodException
     */
    private BeanDefinition getProxyBeanDefinition(Class clazz, MyInvocationHandler handler) throws NoSuchMethodException {
        // 获取JDK代理类的类型
        Class<?> jdkDynamicProxyClass = getJDKDynamicProxyClass(clazz, handler);

        // 获取JDK代理类的Bean定义
        BeanDefinition jdkBeanDefinition = getJDKBeanDefinition(jdkDynamicProxyClass, handler);

        return jdkBeanDefinition;
    }

    /**
     * 获取JDK代理类的Bean定义：根据JDK代理类的类型和处理类实例创建JDK代理类的Bean定义
     * @param jdkDynamicProxyClass
     * @param handler
     */
    private BeanDefinition getJDKBeanDefinition(Class<?> jdkDynamicProxyClass, MyInvocationHandler handler) {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(jdkDynamicProxyClass)
                .addConstructorArgValue(handler)
                .getBeanDefinition();
        beanDefinition.setAutowireCandidate(true);
        return beanDefinition;
    }

    /**
     * 获取JDK代理类的类类型
     * @param clazz
     * @param handler
     */
    private Class<?> getJDKDynamicProxyClass(Class clazz, MyInvocationHandler handler) throws NoSuchMethodException {
        JdkProxyCreator jdkProxyCreator = new JdkProxyCreator(new Class[]{clazz}, handler);
        return jdkProxyCreator.getProxyClassInfo();
    }

    /**
     * 获取创建代理时所需的处理类
     * @return
     */
    private MyInvocationHandler getMyInvocationHandler() {
        return new MyInvocationHandler();
    }
}
