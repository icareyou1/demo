package com.fentric.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.modbus.ModbusByTCP;
import com.fentric.modbus.ModuleWarmDetection;
import com.fentric.modbus.ServerReceiveThread;
import com.fentric.modbus.WatchingOperationMQ;
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
    public void createServerSocket(){
        //处理连接线程
        ThreadPool.execute(new ServerReceiveThread(port));
        //用户输入线程
        ThreadPool.execute(new WatchingOperationMQ());
        //掉线监测，由定时采集处完成
        //自动采集线程(分模块采集)
        new Thread(new Runnable() {
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                while (true){
                    if (System.currentTimeMillis()-l>5000){
                        l=System.currentTimeMillis();
                        System.out.println("warm模块启动检测...");
                        ThreadPool.execute(new ModuleWarmDetection());
                    }
                }
            }
        }).start();
    }
}
