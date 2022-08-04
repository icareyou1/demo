package com.fentric.controller;

import com.fentric.domain.ResponseResult;
import com.fentric.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return deviceService.sendModbus();
    }
}
