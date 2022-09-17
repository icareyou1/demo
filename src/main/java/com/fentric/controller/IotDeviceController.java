package com.fentric.controller;

import com.fentric.domain.ResponseResult;
import com.fentric.service.IotDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 设备信息表 前端控制器
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-08
 */
@RestController
@RequestMapping("/iotDevice")
public class IotDeviceController {
    @Autowired
    IotDeviceService iotDeviceService;

    /**
     * 在线设备 离线设备
     * 今日告警 历史告警
     * 今日事件 历史事件
     * 今日操作 历史操作
     * @return
     */
    @GetMapping("/getDeviceStatistic")
    public ResponseResult getDeviceStatistic(){
        return iotDeviceService.getDeviceStatistic();
    }
}
