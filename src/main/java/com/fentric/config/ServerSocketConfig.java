package com.fentric.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.modbus.*;
import com.fentric.pojo.IotDevice;
import com.fentric.service.IotDeviceService;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.SpringUtils;
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
public class ServerSocketConfig {
    @Value("${socket.port}")
    private int port;

    @Bean
    //此处不用子线程将会导致阻塞,让tomcat启动不了
    public void createServerSocket() throws InterruptedException {
        //处理连接线程
        ThreadPool.execute(new ServerReceiveThread(port));
        //用户输入线程
        ThreadPool.execute(new WatchingOperationMQ());
        //掉线监测，由定时采集处完成
        //自动采集线程(分模块采集)
        Thread.sleep(3000);  //初始化连接
        ThreadPool.execute(new DetectDeviceOnline());
        new Thread(new Runnable() {
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                while (true){
                    if (System.currentTimeMillis()-l>30000){
                        l=System.currentTimeMillis();

                        ThreadPool.execute(new DetectDeviceOnline());
                        //System.out.println("异常事件检测模块启动....");
                        //ThreadPool.execute(new ModuleEventDetection());
                    }
                }
            }
        }).start();
    }
}
