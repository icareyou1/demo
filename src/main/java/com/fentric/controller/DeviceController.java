package com.fentric.controller;

import com.fentric.domain.ResponseResult;
import com.fentric.domain.UserCode;
import com.fentric.pojo.LoginUser;
import com.fentric.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//注意这里
@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    DeviceService deviceService;
    @GetMapping("/sendModbus")
    public ResponseResult sendModbus(){
        //userId,deviceId,code
        //deviceId   1号
        //code   1,3,1030,50
        return deviceService.sendModbus();
    }
}
