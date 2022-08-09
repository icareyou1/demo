package com.fentric.modbus;

import com.fentric.domain.UserCode;
import com.fentric.pojo.IotRun;
import com.fentric.service.IotRunService;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.SetObjComboFields;
import com.fentric.utils.SpringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import static com.fentric.modbus.DeviceDataPool.*;

//处理用户主动发送的指令
public class WatchingOperationMQ implements Runnable{

    @Override
    public void run() {
        System.out.println("用户指令监控模块启动...");
        while (true){
            //首先获取指令，看是否有线程匹配，没有就返回对应异常
            try {
                UserCode userCode = UserCodeMQ.take();
                long deviceId = userCode.getDeviceId();
                Socket socket = SocketMap.get(deviceId);
                //为空直接跳转下一次
                if (socket==null) {
                    userCode.setError("网关设备不在线");
                    DeviceDataPool.UserCodeDatamap.put(userCode.getUserId(),userCode);
                    continue;
                }
                //如果异常？
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                if (userCode.getCode()!=null){
                    byte[] codeByte = CodeUtils.hexStrToByteArr(userCode.getCode());
                    outputStream.write(codeByte);
                    byte[] bytes=new byte[1024];
                    int len=0;
                    //spd未上电会阻塞
                    socket.setSoTimeout(500);
                    //Thread.sleep(1000);
                    //没有获取到就会阻塞
                    while ((len=inputStream.read(bytes))!=-1){
                        String data = CodeUtils.byteArrToHexStr(bytes, len);
                        String temp = data.replace(" ", "");
                        StringBuilder replace = new StringBuilder(temp.substring(6,temp.length()-4));
                        Integer[] ints= new Integer[replace.length() / 4];
                        for (int i=0;i<replace.length()/4;i++){
                            //因为这里的数据都比较小，直接转换即可
                            ints[i]=(int)CodeUtils.hexStrToLong(replace.substring(i*4, i*4 + 4));
                        }
                        //todo 将数据放入，数据库处理队列（预先处理数据放入对应库中）
                        IotRunService iotRunService = SpringUtils.getBean("iotRunServiceImpl", IotRunService.class);
                        //todo 如何遍历封装？
                        IotRun iotRun = SetObjComboFields.setObj(IotRun.class, ints, "d1030", "d1079");
                        //设置设备id
                        iotRun.setDeviceId(userCode.getDeviceId());
                        //放入数据
                        userCode.setData(iotRun);
                        //放入缓存供controller快速获取
                        DeviceDataPool.UserCodeDatamap.put(userCode.getUserId(),userCode);
                        //保存进数据库
                        //iotRunService.save(iotRun);
                        //采集数据队列
                        DeviceDataMQ.offer(data);
                        //如果不是注册码，就直接break
                        break;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("读取超时...");
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


            /*
            //todo 从map中获取socket
            for (Long key : SocketMap.keySet()) {
                System.out.println("----");
                Socket socket = SocketMap.get(key);
                //jdk新写法，前提流实现autoclose接口
                try {
                    //如果异常？
                    InputStream input = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    //输出数据
                    while (true){
                        //接受十六进制字符串，没有则为null

                        System.out.println("userCode---"+userCode);
                        if (userCode.getCode()!=null){
                            byte[] codeByte = CodeUtils.hexStrToByteArr(userCode.getCode());
                            outputStream.write(codeByte);
                            byte[] bytes=new byte[1024];
                            int len=0;
                            //spd未上电会阻塞
                            socket.setSoTimeout(500);
                            //Thread.sleep(1000);
                            //没有获取到就会阻塞
                            while ((len=input.read(bytes))!=-1){
                                String data = CodeUtils.byteArrToHexStr(bytes, len);
                                String temp = data.replace(" ", "");
                                StringBuilder replace = new StringBuilder(temp.substring(6,temp.length()-4));
                                Integer[] ints= new Integer[replace.length() / 4];
                                for (int i=0;i<replace.length()/4;i++){
                                    //因为这里的数据都比较小，直接转换即可
                                    ints[i]=(int)CodeUtils.hexStrToLong(replace.substring(i*4, i*4 + 4));
                                }
                                //todo 将数据放入，数据库处理队列（预先处理数据放入对应库中）
                                IotRunService iotRunService = SpringUtils.getBean("iotRunServiceImpl", IotRunService.class);
                                //todo 如何遍历封装？
                                IotRun iotRun = SetObjComboFields.setObj(IotRun.class, ints, "d1030", "d1079");
                                //设置设备id
                                iotRun.setDeviceId(userCode.getDeviceId());
                                //放入数据
                                userCode.setData(iotRun);
                                //放入缓存供controller快速获取
                                DeviceDataPool.UserCodeDatamap.put(userCode.getUserId(),userCode);
                                //保存进数据库
                                //iotRunService.save(iotRun);
                                //采集数据队列
                                DeviceDataMQ.offer(data);
                                //如果不是注册码，就直接break
                                if (!("F4 70 0C 73 3D 1B ".equals(data))) break;
                            }
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }  catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            */
        }
    }
}
