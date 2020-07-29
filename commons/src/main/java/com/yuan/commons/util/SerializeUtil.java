package com.yuan.commons.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * @author by yuanlai
 * @Date 2020/7/27 10:23 上午
 * @Description: 序列化／反序列化工具类
 * @Version 1.0
 */
public class SerializeUtil {

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (IOException e) {
            System.out.println("序列化对象出错！");
            e.printStackTrace();
            return null;
        }
    }

    public static Object unSerialize(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (IOException e) {
            System.out.println("反序列化出错！");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("反序列化出错！");
            e.printStackTrace();
            return null;
        }
    }
}
