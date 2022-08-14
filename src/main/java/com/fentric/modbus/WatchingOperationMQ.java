package com.fentric.modbus;

import com.fentric.domain.Modbus;
import com.fentric.domain.UserCode;
import com.fentric.pojo.IotRun;
import com.fentric.service.IotRunService;
import com.fentric.utils.*;

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
                //没有获取将会一直阻塞
                UserCode userCode = UserCodeMQ.take();
                //网关id
                long gatewayId = userCode.getGatewayId();
                Socket socket = SocketMap.get(gatewayId);
                //为空直接跳转下一次，为空情况下是被踢出了
                if (socket==null) {
                    userCode.setError("网关设备不在线");
                    DeviceDataPool.UserCodeDatamap.put(userCode.getUserId(),userCode);
                    continue;
                }
                Modbus modbusUserSending = userCode.getModbusUserSending();
                modbusUserSending.setSocket(socket);
                //判断用户操作类型,3，6，16
                int functionId = modbusUserSending.getFunctionId();
                if (functionId==3){
                    modbusUserSending=IOUtils.readHoldingRegisters(modbusUserSending);
                }else if (functionId==6){
                    modbusUserSending=IOUtils.writeSingleRegister(modbusUserSending);
                }else if (functionId==16){
                    modbusUserSending=IOUtils.writeMultiRegister(modbusUserSending);
                }else {
                    //在前面做好判断，不要来到这一步
                }
                //返回非空就代表查询失败
                if (modbusUserSending.getError()!=null){
                    //
                    //todo  1.超时查询就多获取几次，稍后放入数据库晚点展示            2.告诉用户查询失败，
                    continue;
                }
                //放入缓存供controller快速获取
                userCode.setModbusUserSending(modbusUserSending);
                DeviceDataPool.UserCodeDatamap.put(userCode.getUserId(),userCode);
                //将操作查询运行状态表结果放入数据库中(反射创建对象异常)
                DBUtils.insertRunModbus(modbusUserSending);
                //todo 采集数据队列,如果使用就将上面的消息放入队列
                //DeviceDataMQ.offer(modbusUserSending);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
