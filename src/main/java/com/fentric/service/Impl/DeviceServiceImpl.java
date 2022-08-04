package com.fentric.service.Impl;

import com.fentric.config.CaptchaConfig;
import com.fentric.config.ServerSocketConfig;
import com.fentric.domain.ResponseResult;
import com.fentric.modbus.ClientSendThread;
import com.fentric.pojo.LoginUser;
import com.fentric.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Override
    public ResponseResult sendModbus() {
        //发送指令,查询指令
        new Thread(new ClientSendThread("192.168.12.201",9999)).start();
        Map<String, Object> map = new HashMap<>();
        return new ResponseResult(200,"操作modbus成功",map);
    }
}
