package com.fentric.service.Impl;

import com.fentric.config.ServerSocketConfig;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.UserCode;
import com.fentric.pojo.LoginUser;
import com.fentric.service.DeviceService;
import com.fentric.utils.CodeUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.fentric.modbus.DeviceDataPool.UserCodeDatamap;
import static com.fentric.modbus.DeviceDataPool.UserCodeMQ;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Override
    public ResponseResult sendModbus() {
        UserCode userCode = new UserCode();
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getSysUser().getUserId();
        userCode.setUserId(userId);
        userCode.setDeviceId(268762082393371L);
        userCode.setCode(CodeUtils.generateModbus(1,3,1030,50));
        //发送指令,查询指令
        try {
            //查询模块五
            UserCodeMQ.put(userCode);
            //为空原因被取走了
            System.out.println("用户指令"+UserCodeMQ);
            Thread.sleep(500);
            //获得返回结果
            UserCode userCodeData = UserCodeDatamap.get(userId);
            UserCodeDatamap.remove(userId);
            System.out.println("用户查询到的数据："+userCodeData);
            //记录日志
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        return new ResponseResult(200,"操作modbus成功",map);
    }
}