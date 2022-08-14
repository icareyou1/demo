package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.DeviceStatus;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.pojo.IotDevice;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.IOUtils;
import com.fentric.utils.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static com.fentric.modbus.DeviceDataPool.DeviceStatusMap;
@Data
@AllArgsConstructor
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
                IotDeviceMapper iotDeviceMapper = SpringUtils.getBean("iotDeviceMapper", IotDeviceMapper.class);
                LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
                wrapper.ge(IotDevice::getDeviceId,receiveGatewayId);
                wrapper.le(IotDevice::getDeviceId,receiveGatewayId+32);
                List<IotDevice> iotDevices = iotDeviceMapper.selectList(wrapper);
                //能从数据库中查询到
                if (iotDevices.size()>=1){
                    DeviceStatus deviceStatus = new DeviceStatus();
                    deviceStatus.setGateWayId(receiveGatewayId);
                    deviceStatus.setSocket(accept);
                    //todo 查询状态表状态进行封装
                    //int[] ints=new int[iotDevices.size()];
                    //设置状态
                    deviceStatus.setShouldUpdate(false);
                    DeviceStatusMap.put(receiveGatewayId,deviceStatus);

                    //SocketMap.put(receiveGatewayId,accept);
                    System.out.println("有新设备"+accept.getInetAddress().getHostAddress()+":"+accept.getPort()+"接入，当前总设备数为："+DeviceStatusMap.size());
                }else {
                    //todo 防止ddos   如果有设备一直请求，那么就放入待销毁集合，到时间销毁
                    System.out.println("设备:"+accept.getInetAddress().getHostAddress()+":"+accept.getPort()+"企图接入,当前总设备数为："+DeviceStatusMap.size());
                    accept.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
