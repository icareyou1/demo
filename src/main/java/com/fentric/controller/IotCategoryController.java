package com.fentric.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fentric.domain.ResponseResult;
import com.fentric.pojo.IotCategory;
import com.fentric.service.IotCategoryService;
import com.fentric.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备类型表(参数部分为了拓展性,尽量走协议格式) 前端控制器
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-20
 */
@RestController
@RequestMapping("/iotCategory")
public class IotCategoryController {
    @Autowired
    IotCategoryService iotCategoryService;

    //查询类别表
    @GetMapping("/listCategory")
    @PreAuthorize("@fentric.hasAuthority('device:category:list')")
    public ResponseResult listCategory(String categoryName,String status,Long pageNum,Long pageSize){
        LambdaQueryWrapper<IotCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotCategory::getDeleted,"0");
        //验证参数有效性
        if (!CommonUtils.isNullOrEmptyStr(categoryName)){
            queryWrapper.eq(IotCategory::getCategoryName,categoryName);
        }
        if (CommonUtils.isValidateStatus(status)){
            queryWrapper.eq(IotCategory::getStatus,status);
        }
        if (pageNum==null||pageNum<=0){
            pageNum=1L;
        }
        if (pageSize==null||pageSize<=0){
            pageSize=10L;
        }
        Page<IotCategory> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page=iotCategoryService.page(page,queryWrapper);
        List<IotCategory> rows = page.getRecords();
        long total = page.getTotal();
        Map<String, Object> map = new HashMap<>();
        map.put("rows",rows);
        map.put("total",total);
        return new ResponseResult(200,"分类查询成功",map);
    }

}
