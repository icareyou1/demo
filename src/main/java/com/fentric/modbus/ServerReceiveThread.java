package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.pojo.IotDevice;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static com.fentric.modbus.DeviceDataPool.SocketMap;
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
                inputStream = accept.getInputStream();
                byte[] bytes=new byte[1024];
                accept.setSoTimeout(3000);
                Long deviceId=0L;
                int len=0;
                //todo 循环就可读取,其他设备会等待3秒失败
                while ((len=inputStream.read(bytes))!=-1){
                    String data = CodeUtils.byteArrToHexStr(bytes, len);
                    deviceId = CodeUtils.hexStrToLong(data);
                    break;
                }
                //查询数据库
                IotDeviceMapper iotDeviceMapper = SpringUtils.getBean("iotDeviceMapper", IotDeviceMapper.class);
                LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(IotDevice::getDeviceId);
                List<Object> objects = iotDeviceMapper.selectObjs(wrapper);
                boolean isRegister=false;
                for (Object item : objects) {
                    //如果注册了设备就连接
                    if (Long.parseLong(item.toString())==deviceId){
                        //如果有设备已经登录了替换
                        SocketMap.put(deviceId,accept);
                        isRegister=true;
                        System.out.println("有新设备"+accept.getInetAddress().getHostAddress()+":"+accept.getPort()+"接入，当前总设备数为："+SocketMap.size());
                        //成功就break;
                        break;
                    }
                }
                //设备没有注册，不允许连接
                if (!isRegister) {
                    System.out.println("设备:"+accept.getInetAddress().getHostAddress()+":"+accept.getPort()+"接入失败当前总设备数为："+SocketMap.size());
                    accept.close();
                }

                       /*
                        //启动消息队列线程（监测阻塞队列指令）
                        threadPool.execute(new WatchingOperationMQ(accept));
                        //设备接入
                        if ("192.168.12.7".equals(hostAddress)){
                            threadPool.execute(new ModbusByTCP(accept));
                        }else {
                            threadPool.execute(new ServerReceiveThread(accept));
                        }
                        */
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
