package com.fentric.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.DeviceQueryParams;
import com.fentric.domain.vo.MonitorDeviceTable;
import com.fentric.pojo.IotDevice;
import com.fentric.service.IotDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/iotMonitor")
public class IotMonitorController {
    @Autowired
    IotDeviceService iotDeviceService;

    @GetMapping("/listMonitorDevice")
    @PreAuthorize("@fentric.hasAuthority('device:monitor:list')")
    public ResponseResult listMonitorDevice(DeviceQueryParams deviceQueryParams){
        Map<String, Object> data = (Map<String, Object>) iotDeviceService.listDevice(deviceQueryParams).getData();
        List<IotDevice> rows = (List<IotDevice>) data.get("rows");
        ArrayList<MonitorDeviceTable> list = new ArrayList<>();
        if (rows!=null){
            rows.forEach(item->{
                MonitorDeviceTable monitorDeviceTable = new MonitorDeviceTable();
                monitorDeviceTable.setDeviceId(item.getDeviceId());
                monitorDeviceTable.setDeviceName(item.getDeviceName());
                monitorDeviceTable.setTagIds(item.getTagIds());
                list.add(monitorDeviceTable);
            });
        }
        data.put("rows",list);
        return new ResponseResult(200,"设备监测列表查询成功",data);
    }
}
