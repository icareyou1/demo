package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.TagQueryParams;
import com.fentric.domain.vo.PageUser;
import com.fentric.domain.vo.TagShow;
import com.fentric.pojo.IotTag;
import com.fentric.mapper.IotTagMapper;
import com.fentric.pojo.SysOrg;
import com.fentric.pojo.SysUser;
import com.fentric.service.IotTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fentric.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标签表(与设备表多对多) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-18
 */
@Service
public class IotTagServiceImpl extends ServiceImpl<IotTagMapper, IotTag> implements IotTagService {
    @Autowired
    IotTagMapper iotTagMapper;

    //查询标签列表
    @Override
    public ResponseResult listTag(TagQueryParams tagQueryParams) {
        //1.设置查询lambda
        LambdaQueryWrapper<IotTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotTag::getDeleted,"0");
        //不查询密码字段
        //2.封装查询IotTag的参数
        if (tagQueryParams.getTagName()!=null&&!"".equals(tagQueryParams.getTagName())){
            queryWrapper.like(IotTag::getTagName,tagQueryParams.getTagName());
        }
        //不用严格判定0或1,查询不到是用户自己问题
        if (tagQueryParams.getStatus()!=null&&!"".equals(tagQueryParams.getStatus())){
            queryWrapper.eq(IotTag::getStatus,tagQueryParams.getStatus());
        }
        try {
            if (tagQueryParams.getBeginTime()!=null&&!"".equals(tagQueryParams.getBeginTime())){
                queryWrapper.ge(IotTag::getCreateTime, LocalDateTime.of(LocalDate.parse(tagQueryParams.getBeginTime()), LocalTime.MIN));
            }
            if (tagQueryParams.getEndTime()!=null&&!"".equals(tagQueryParams.getEndTime())){
                queryWrapper.le(IotTag::getCreateTime,LocalDateTime.of(LocalDate.parse(tagQueryParams.getEndTime()),LocalTime.MAX));
            }
        } catch (Exception e) {
            return new ResponseResult(500,"日期格式出错");
        }
        //设置Page
        if (tagQueryParams.getPageNum()==null||tagQueryParams.getPageNum()<=0){
            tagQueryParams.setPageNum(1L);
        }
        if (tagQueryParams.getPageSize()==null||tagQueryParams.getPageSize()<=0){
            tagQueryParams.setPageSize(10L);
        }
        //3.利用this.page分页查询
        Page<IotTag> page = new Page<>();
        //设置起始页
        page.setCurrent(tagQueryParams.getPageNum());
        //设置每页数量
        page.setSize(tagQueryParams.getPageSize());
        page=this.page(page,queryWrapper);

        //获取列表数据
        List<IotTag> rows = page.getRecords();
        //获取总页数
        long total = page.getTotal();
        Map<String, Object> map = new HashMap<>();
        map.put("rows",rows);
        map.put("total",total);
        return new ResponseResult(200,"查询标签列表成功",map);
    }

    //校验添加标签参数
    @Override
    public boolean validateAddTag(IotTag iotTag) {
        //tagId为null,deleted为null
        if (iotTag.getTagId()!=null||
                iotTag.getDeleted()!=null||
                iotTag.getCreateTime()!=null||
                iotTag.getUpdateTime()!=null) return false;
        //标签名非空非空串;如果标签名有返回,则为false
        LambdaQueryWrapper<IotTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotTag::getTagName,iotTag.getTagName());
        queryWrapper.eq(IotTag::getDeleted,"0");
        if (CommonUtils.isNullOrEmptyStr(iotTag.getTagName())||iotTagMapper.selectOne(queryWrapper)!=null){
            return false;
        }
        if (!CommonUtils.isValidateStatus(iotTag.getStatus())) return false;
        //comment随意
        return true;
    }

    //修改标签参数是否合法
    @Override
    public boolean validateUpdateTag(IotTag iotTag) {
        //id不能为null,且大于0
        if (!CommonUtils.isValidateId(iotTag.getTagId())){
            return false;
        }
        //deleted为null
        if (iotTag.getDeleted()!=null||
                iotTag.getCreateTime()!=null||
                iotTag.getUpdateTime()!=null) return false;
        //标签名非空非空串;如果标签名有返回,则为false
        LambdaQueryWrapper<IotTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotTag::getTagName,iotTag.getTagName());
        queryWrapper.eq(IotTag::getDeleted,"0");
        if (CommonUtils.isNullOrEmptyStr(iotTag.getTagName())||iotTagMapper.selectOne(queryWrapper)!=null){
            return false;
        }
        if (!CommonUtils.isValidateStatus(iotTag.getStatus())) return false;
        //comment随意
        return true;
    }

    //获取设备管理页面左侧标签栏
    @Override
    public List<TagShow> getTagForTree() {
        //返回tagId和tagName的列表即可
        LambdaQueryWrapper<IotTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotTag::getDeleted,"0");
        queryWrapper.eq(IotTag::getStatus,"0");
        //查询非空的tagName
        queryWrapper.isNotNull(IotTag::getTagName);
        queryWrapper.select(IotTag::getTagId,IotTag::getTagName);
        List<IotTag> list = this.list(queryWrapper);
        List<TagShow> tagShows = new ArrayList<>();
        list.forEach(item->{
            TagShow tagShow = new TagShow(item.getTagId(), item.getTagName());
            tagShows.add(tagShow);
        });
        return tagShows;
    }

}
