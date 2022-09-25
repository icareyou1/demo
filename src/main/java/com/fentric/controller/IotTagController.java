package com.fentric.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fentric.annotation.FentricLogin;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.TagQueryParams;
import com.fentric.pojo.IotTag;
import com.fentric.service.IotTagService;
import com.fentric.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * 标签表(与设备表多对多) 前端控制器
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-18
 */
@RestController
@RequestMapping("/iotTag")
public class IotTagController {
    @Autowired
    IotTagService iotTagService;

    //展示标签列表
    @GetMapping("/listTag")
    @PreAuthorize("@fentric.hasAuthority('device:tag:list')")
    public ResponseResult listTag(TagQueryParams tagQueryParams){
        //判断传入字段
        return iotTagService.listTag(tagQueryParams);
    }

    //通过tagId修改tag状态
    @PutMapping("/updateTagStatusByTagId")
    @PreAuthorize("@fentric.hasAuthority('device:tag:update')")
    public ResponseResult updateTagStatusByTagId(@FentricLogin("tagId")Integer tagId,@FentricLogin("status")String status){
        if (tagId<=0||!CommonUtils.isValidateStatus(status)){
            return new ResponseResult(500,"修改标签状态,参数不合法");
        }
        LambdaUpdateWrapper<IotTag> updateWrapper = new LambdaUpdateWrapper<>();
        //设置id
        updateWrapper.eq(IotTag::getTagId,tagId);
        //设置状态
        updateWrapper.set(IotTag::getStatus,status);
        if (iotTagService.update(updateWrapper)){
            return new ResponseResult(200,"修改标签状态成功");
        }else {
            return new ResponseResult(500,"修改标签状态失败");
        }
    }

    //新增标签
    @PostMapping("/addTag")
    @PreAuthorize("@fentric.hasAuthority('device:tag:add')")
    public ResponseResult addTag(@RequestBody IotTag iotTag){
        //校验是否合法
        if (!iotTagService.validateAddTag(iotTag)) {
            return new ResponseResult(500,"新增标签,参数非法");
        }
        //添加标签
        if (iotTagService.save(iotTag)) {
            return new ResponseResult(200,"新增标签成功");
        }else return new ResponseResult(500,"新增标签失败");
    }

    //通过标签id获取标签
    @GetMapping("/getTagByTagId")
    @PreAuthorize("@fentric.hasAuthority('device:tag:query')")
    public ResponseResult getTagByTagId(@RequestParam("tagId")Long tagId){
        if (tagId<=0){
            return new ResponseResult(500,"获取标签信息,参数非法");
        }
        LambdaQueryWrapper<IotTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotTag::getDeleted,"0");
        queryWrapper.eq(IotTag::getTagId,tagId);
        IotTag iotTag = iotTagService.getOne(queryWrapper);
        if (iotTag==null){
            return new ResponseResult(500,"获取用户信息失败");
        }
        return new ResponseResult(200,"用户信息获取成功",iotTag);
    }

    //修改标签
    @PutMapping("/updateTag")
    @PreAuthorize("@fentric.hasAuthority('device:tag:update')")
    public ResponseResult updateTag(@RequestBody IotTag iotTag){
        iotTag.setDeleted(null);
        iotTag.setCreateTime(null);
        iotTag.setUpdateTime(null);
        //修改标签参数是否合法
        if ((!iotTagService.validateUpdateTag(iotTag))){
            return new ResponseResult(500,"修改标签,参数非法");
        }
        //修改标签
        if (iotTagService.updateById(iotTag)){
            return new ResponseResult(200,"修改标签成功");
        }else return new ResponseResult(500,"修改标签失败");
    }

    //删除标签
    @DeleteMapping("/delTag")
    @PreAuthorize("@fentric.hasAuthority('device:tag:delete')")
    public ResponseResult delTag(@RequestParam("tagIds")String tagIds){
        ArrayList<IotTag> list = new ArrayList<>();
        AtomicBoolean isValid= new AtomicBoolean(true);
        Arrays.stream(tagIds.split(",")).forEach(item->{
            long tagId = Long.parseLong(item);
            if (tagId<=0) isValid.set(false);
            IotTag iotTag = new IotTag();
            iotTag.setTagId(tagId);
            iotTag.setDeleted("1");
            list.add(iotTag);
        });
        if (!isValid.get()) return new ResponseResult(500,"删除标签,参数不合法");
        //删除用户,
        if (iotTagService.updateBatchById(list)){
            return new ResponseResult(200,"删除标签成功");
        }else return new ResponseResult(500,"删除标签失败");
    }
}
