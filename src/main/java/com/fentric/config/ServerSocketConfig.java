package com.fentric.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.modbus.*;
import com.fentric.pojo.IotDevice;
import com.fentric.service.IotDeviceService;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static com.fentric.modbus.DeviceDataPool.SocketMap;
import static com.fentric.modbus.DeviceDataPool.ThreadPool;


@Component
@Slf4j
public class ServerSocketConfig {
    @Value("${socket.port}")
    private int port;

    @Bean
    //此处不用子线程将会导致阻塞,让tomcat启动不了
    public void createServerSocket() throws InterruptedException {
        //处理连接线程
        ThreadPool.execute(new ServerReceiveThread(port));
        //todo 用户输入线程（暂时放一边）
        ThreadPool.execute(new WatchingOperationMQ());

        //数据采集
        /*ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                //自动采集线程(分模块采集)
                try {
                    Thread.sleep(3000);  //初始化连接
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int count=1;
                while (true){
                    ThreadPool.execute(new DetectDeviceOnline());
                    log.info("当前活动线程:{}",ThreadPool.getActiveCount());
                    //让线程活动起来
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //这里执行完一个再执行
                    while (ThreadPool.getActiveCount()>=4){
                    }
                    log.info("==============第{}个DetectDeviceOnline执行完毕===================================",count);
                    count++;
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
/*
        //自动采集线程(分模块采集)
        Thread.sleep(3000);  //初始化连接
        int count=1;
        while (true){
            ThreadPool.execute(new DetectDeviceOnline());
            log.info("当前活动线程:{}",ThreadPool.getActiveCount());
            //让线程活动起来
            Thread.sleep(100);
            //这里执行完一个再执行
            while (ThreadPool.getActiveCount()>=3){
            }
            log.info("==============第{}个DetectDeviceOnline执行完毕===================================",count);
            count++;
            Thread.sleep(30000);
        }
*/

    }
}
