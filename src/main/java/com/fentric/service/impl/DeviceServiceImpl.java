package com.fentric.service.impl;

import com.fentric.domain.Modbus;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.UserCode;
import com.fentric.pojo.LoginUser;
import com.fentric.service.DeviceService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.fentric.modbus.DeviceDataPool.UserCodeDatamap;
import static com.fentric.modbus.DeviceDataPool.UserCodeMQ;

@Service
public class DeviceServiceImpl implements DeviceService {
    //测试代码,用户主动发送消息
    @Override
    public ResponseResult sendModbus() {
        UserCode userCode = new UserCode();
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getSysUser().getUserId();
        userCode.setUserId(userId);
        //从页面获取到终端设备的id号
        Long deviceId=268762082393371001L;
        //数据应从页面获取
        userCode.setGatewayId(deviceId/1000);
        Modbus modbus = new Modbus();
        //socket在处理队列中，根据设备id获取
        modbus.setDeviceId(deviceId);
        modbus.setSlaveId(1);
        modbus.setFunctionId(3);
        modbus.setAddress(1022);
        modbus.setQueryLen(58);
        userCode.setModbusUserSending(modbus);

        //userCode.setCode(CodeUtils.generateModbus(1,3,1030,50));

        //发送指令,查询指令
        try {
            //查询模块五
            UserCodeMQ.put(userCode);
            //为空原因被取走了
            System.out.println("用户指令"+UserCodeMQ);
            long startTime=System.currentTimeMillis();
            //3秒获取不到就算失败
            while (System.currentTimeMillis()-startTime<3000){
                //获得返回结果
                UserCode userCodeData = UserCodeDatamap.get(userId);
                UserCodeDatamap.remove(userId);
                System.out.println("用户查询到的数据："+userCodeData);
            }
            //记录日志
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        return new ResponseResult(200,"操作modbus成功",map);
    }
}
