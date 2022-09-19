package com.fentric.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.annotation.FentricLogin;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.vo.TagShow;
import com.fentric.pojo.IotTag;
import com.fentric.service.IotDeviceService;
import com.fentric.service.IotTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
        //返回tagId和tagName的列表即可
        LambdaQueryWrapper<IotTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotTag::getDeleted,"0");
        List<IotTag> list = iotTagService.list(queryWrapper);
        List<TagShow> tagShows = new ArrayList<>();
        list.forEach(item->{
            TagShow tagShow = new TagShow(item.getTagId(), item.getTagName());
            tagShows.add(tagShow);
        });
        return new ResponseResult(200,"获取左侧标签成功",tagShows);
    }

    //展示设备列表
    @GetMapping("listDevice")
    @PreAuthorize("@fentric.hasAuthority('device:manage:list')")
    public ResponseResult listDevice(){
        return null;
    }
}
