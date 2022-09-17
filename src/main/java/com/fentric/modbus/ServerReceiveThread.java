package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.DeviceStatus;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.mapper.IotOnlineMapper;
import com.fentric.pojo.IotDevice;
import com.fentric.pojo.IotOnline;
import com.fentric.service.IotDeviceService;
import com.fentric.service.IotOnlineService;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.IOUtils;
import com.fentric.utils.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import static com.fentric.modbus.DeviceDataPool.DeviceStatusMap;
@Data
@AllArgsConstructor
@Slf4j
public class ServerReceiveThread implements Runnable{
    private int port;
    @Override
    public void run() {
        System.out.println("modbus服务器启动...");
        ServerSocket serverSocket= null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("modbus服务器启动报错");
            e.printStackTrace();
        }
        Socket accept=null;
        InputStream inputStream=null;

        while (true){
            try {
                accept = serverSocket.accept();
                //done 对传入的socket进行校验
                Long receiveGatewayId= Long.parseLong(IOUtils.receiveRegisterCode(accept).replace(" ",""),16)*1000;
                //查询数据库（不考虑放入缓存，新设备注册事件少）
                //确定是否注册，及注册了几个设备
                IotDeviceService iotDeviceServiceImpl = SpringUtils.getBean("iotDeviceServiceImpl", IotDeviceService.class);
                //查询网关id下,0-32下所有设备类型
                int[] category = iotDeviceServiceImpl.queryDeviceCategoryByReceiveGateWayId(receiveGatewayId);
                log.info("数据库中查询的设备类型为:{}",Arrays.toString(category));
                int iotDeviceCount=0;
                if (category!=null) iotDeviceCount=category.length;
                //能从数据库中查询到(至少一个说明有设备注入)
                if (iotDeviceCount>=1){
                    DeviceStatus deviceStatus = new DeviceStatus();
                    deviceStatus.setGateWayId(receiveGatewayId);
                    deviceStatus.setSocket(accept);
                    //done 查询状态表状态进行封装
                    IotOnlineService iotOnlineServiceImpl = SpringUtils.getBean("iotOnlineServiceImpl", IotOnlineService.class);
                    //将查询到设备id和在线情况封装进数组  可能会有0值存在,刚刚接入
                    int[] online = iotOnlineServiceImpl.putDevicesStatusIntoIntArray(receiveGatewayId);
                    log.info("从数据库中查询设备的状态为:{}", Arrays.toString(online));
                    //设置设备类型
                    deviceStatus.setCategory(category);
                    //设置状态
                    deviceStatus.setOnline(online);
                    deviceStatus.setShouldUpdate(false);
                    DeviceStatusMap.put(receiveGatewayId,deviceStatus);
                    log.info("有新设备"+accept.getInetAddress().getHostAddress()+":"+accept.getPort()+"接入，当前总设备数为："+DeviceStatusMap.size());
                }else {
                    //todo 防止ddos   如果有设备一直请求，那么就放入待销毁集合，到时间销毁
                    log.info("设备:"+accept.getInetAddress().getHostAddress()+":"+accept.getPort()+"企图接入,当前总设备数为："+DeviceStatusMap.size());
                    accept.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
