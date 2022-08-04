package com.fentric.config;

import com.fentric.modbus.ModbusByTCP;
import com.fentric.modbus.ServerReceiveThread;
import com.fentric.pojo.LoginUser;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ServerSocketConfig {
    //消息队列
    public static LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    public static ServerSocket serverSocket=null;
    //阻塞队列保存,没有线程执行的任务
    public static final ThreadPoolExecutor threadPool=
            new ThreadPoolExecutor(15,20,100, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());

    public void createServerSocket(){
        try {
            serverSocket=new ServerSocket(9999);
            while (true){
                System.out.println("accept前...");
                Socket accept = serverSocket.accept();
                System.out.println("accept后...");
                String hostAddress = accept.getInetAddress().getHostAddress();
                int port = accept.getPort();
                System.out.println("获取客户端:"+hostAddress+":"+port);
                //设备接入
                if ("192.168.12.7".equals(hostAddress)){
                    threadPool.execute(new ModbusByTCP(accept));
                }else {
                    threadPool.execute(new ServerReceiveThread(accept));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
