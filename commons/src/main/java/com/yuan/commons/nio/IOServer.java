package com.yuan.commons.nio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author by yuanlai
 * @Date 2020/7/27 4:11 下午
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
public class IOServer {
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8000);

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("demo-pool-%d").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        singleThreadPool.execute(()->{
            for (;;){
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        singleThreadPool.execute(()->{
            for (;;){
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        singleThreadPool.execute(()->{
            for (;;){
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });



//        singleThreadPool.execute(() -> {
//            while (true) {
//
//                try {
//                    // (1) 阻塞方法获取新的连接
//                    Socket socket = serverSocket.accept();
//
//                    // (2) 每一个新的连接都创建一个线程，负责读取数据
//                    new Thread(() -> {
//                        try {
//                            int len;
//                            byte[] data = new byte[1024];
//                            InputStream inputStream = socket.getInputStream();
//                            // (3) 按字节流方式读取数据
//                            while ((len = inputStream.read(data)) != -1) {
//                                System.out.println(new String(data, 0, len));
//                            }
//                        } catch (IOException e) {
//                        }
//                    }).start();
//
//                } catch (IOException e) {
//                }
//
//            }
//        });

        singleThreadPool.shutdown();

    }
}
