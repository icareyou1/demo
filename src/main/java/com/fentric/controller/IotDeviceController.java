package com.fentric.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fentric.annotation.FentricLogin;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.DeviceQueryParams;
import com.fentric.domain.vo.CategorySelectShow;
import com.fentric.domain.vo.TagShow;
import com.fentric.pojo.IotCategory;
import com.fentric.pojo.IotDevice;
import com.fentric.pojo.IotTag;
import com.fentric.service.IotCategoryService;
import com.fentric.service.IotDeviceService;
import com.fentric.service.IotTagService;
import com.fentric.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    @Autowired
    IotTagService iotTagService;
    @Autowired
    IotCategoryService iotCategoryService;

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

    //获取tag列表数据在设备处进行展示
    @GetMapping("/tagShowByTree")
    public ResponseResult tagShowByTree(){
        return new ResponseResult(200,"获取左侧标签成功",iotTagService.getTagForTree());
    }

    //获取搜索区域的设备类型
    @GetMapping("/deviceCategoryForSearch")
    public ResponseResult deviceCategoryForSearch(){
        return new ResponseResult(200,"获取设备类型下拉菜单成功",iotCategoryService.getDeviceCategoryForSearch());
    }

    //获取搜索区域的设备ip
    @GetMapping("/deviceIpForSearch")
    public ResponseResult deviceIpForSearch(){
        return new ResponseResult(200,"获取设备IP地址成功",iotDeviceService.getDeviceIpForSearch());
    }

    //展示设备列表
    @GetMapping("/listDevice")
    @PreAuthorize("@fentric.hasAuthority('device:manage:list')")
    public ResponseResult listDevice(DeviceQueryParams deviceQueryParams){
        return iotDeviceService.listDevice(deviceQueryParams);
    }

    //修改设备状态
    @PutMapping("/updateDeviceStatusByDeviceId")
    @PreAuthorize("@fentric.hasAuthority('device:manage:update')")
    public ResponseResult updateDeviceStatusByDeviceId(@FentricLogin("deviceId")Long deviceId,@FentricLogin("status")String status){
        if (deviceId<=0||!CommonUtils.isValidateStatus(status)){
            return new ResponseResult(500,"参数不合法");
        }
        LambdaUpdateWrapper<IotDevice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(IotDevice::getDeviceId,deviceId)
                .set(IotDevice::getStatus,status);
        if (iotDeviceService.update(updateWrapper)){
            return new ResponseResult(200,"设备状态修改成功");
        }else {
            return new ResponseResult(500,"设备状态修改失败");
        }
    }

    //新增设备
    @PostMapping("/addDevice")
    @PreAuthorize("@fentric.hasAuthority('device:manage:add')")
    public ResponseResult addDevice(@RequestBody IotDevice iotDevice){
        if (!iotDeviceService.validateAddDevice(iotDevice)) {
            return new ResponseResult(500,"添加设备参数不合法");
        }
        //添加用户(如果id重复,会出现问题,前端做了限制,机器端会有问题)
        try {
            iotDeviceService.addDevice(iotDevice);
            return new ResponseResult(200,"添加设备成功");
        } catch (Exception e) {
            return new ResponseResult(500,"添加设备失败");
        }
    }

    //获取修改设备时的回显数据
    @GetMapping("/getDeviceByDeviceId")
    @PreAuthorize("@fentric.hasAuthority('device:manage:query')")
    public ResponseResult getDeviceByDeviceId(@RequestParam("deviceId")Long deviceId){
        if (deviceId<=0){
            return new ResponseResult(500,"修改设备获取回显失败");
        }
        IotDevice device = iotDeviceService.getById(deviceId);
        return new ResponseResult(200,"获取回显成功",device);
    }

    //修改设备
    @PutMapping("/updateDevice")
    @PreAuthorize("@fentric.hasAuthority('device:manage:add')")
    public ResponseResult updateDevice(@RequestBody IotDevice iotDevice){
        if (!iotDeviceService.validateUpdateDevice(iotDevice)){
            return new ResponseResult(500,"修改设备参数不合法");
        }
        try {
            iotDeviceService.updateDevice(iotDevice);
            return new ResponseResult(200,"修改设备成功");
        }catch (Exception e){
            return new ResponseResult(500,"修改设备失败");
        }
    }

    //删除设备
    @DeleteMapping("/delDevice")
    @PreAuthorize("@fentric.hasAuthority(('device:manage:delete'))")
    public ResponseResult delDevice(@RequestParam("deviceIds")String deviceIds){
        //存储接受到的deviceIds
        List<Long> list = new ArrayList<>();
        AtomicBoolean isValid=new AtomicBoolean(true);
        Arrays.stream(deviceIds.split(",")).forEach(item->{
            //规定deviceId为18
            if (item.length()!=18) isValid.set(false);
            //解析报错抛出的异常会被捕获
            long deviceId = Long.parseLong(item);
            if (deviceId<=0) isValid.set(false);
            list.add(deviceId);
        });
        if (!isValid.get()||list.size()<=0){
            return new ResponseResult(500,"删除设备参数不合法");
        }
        //删除设备,真实删除
        iotDeviceService.delDeviceByDeviceIds(list);
        return new ResponseResult(200,"删除设备成功");
}
}
