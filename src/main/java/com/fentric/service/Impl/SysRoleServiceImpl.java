package com.fentric.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.RoleAddParams;
import com.fentric.domain.requestVO.RoleQueryParams;
import com.fentric.pojo.SysRole;
import com.fentric.mapper.SysRoleMapper;
import com.fentric.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * <p>
 * 角色表(与用户表1对1) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    SysRoleMapper sysRoleMapper;

    //查询角色表
    @Override
    public ResponseResult listRole(RoleQueryParams roleQueryParams) {
        //设置查询参数
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getDeleted,"0");
        //停不停用都要查询

        //1.对params进行非空判断     如果乱传json数据,会捕获500异常   "msg": "illegal input， offset 1, char 1"
        if (roleQueryParams.getParams()!=null&&!"".equals(roleQueryParams.getParams())){
            try {
                JSONObject jsonObject = JSON.parseObject(roleQueryParams.getParams());
                //如果存在key
                if (jsonObject.containsKey("beginTime")){
                    String beginTime = (String) jsonObject.get("beginTime");
                    LocalDate parse = LocalDate.parse(beginTime);
                    //可以不用使用因为默认从0时开始
                    queryWrapper.ge(SysRole::getCreateTime,parse);
                }
                //如果存在key
                if (jsonObject.containsKey("endTime")){
                    String endTime = (String) jsonObject.get("endTime");
                    LocalDate parse = LocalDate.parse(endTime);
                    //查询到当前最大值
                    queryWrapper.le(SysRole::getCreateTime, LocalDateTime.of(parse, LocalTime.MAX));
                }
            } catch (Exception e) {
                return new ResponseResult(500,"JSON参数非法");
            }
        }
        //2.roleName非空判断
        if (roleQueryParams.getRoleName()!=null&&!"".equals(roleQueryParams.getRoleName())){
            queryWrapper.like(SysRole::getRoleName,roleQueryParams.getRoleName());
        }
        //3.status非空判断
        if (roleQueryParams.getStatus()!=null&&!"".equals(roleQueryParams.getStatus())){
            queryWrapper.eq(SysRole::getStatus,roleQueryParams.getStatus());
        }
        //4.此处的roleQueryParams不会为空,但是里面属性可能为null
        if (roleQueryParams.getPageNum()==null||roleQueryParams.getPageNum()<=0){
            roleQueryParams.setPageNum(1L);
        }
        //5.设置页面大小
        if (roleQueryParams.getPageSize()==null||roleQueryParams.getPageSize()<=0){
            roleQueryParams.setPageSize(10L);
        }

        //total总数,current 当前页, size 每页显示的数  getPages()当前查询多少分页  getRecords()真正查询出来的数据返回list
        Page<SysRole> page = new Page<>();
        //设置起始页
        page.setCurrent(roleQueryParams.getPageNum());
        //设置每页数量
        page.setSize(roleQueryParams.getPageSize());
        page = this.page(page,queryWrapper);

        //获取查询的数据
        List<SysRole> rows = page.getRecords();
        //获取查询的总数
        long total = page.getTotal();
        //封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("rows",rows);
        map.put("total",total);
        return new ResponseResult(200,"查询角色成功",map);
    }

    //根据角色id查询角色信息
    @Override
    public ResponseResult getRoleByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getDeleted,"0");
        //停不停用同样要查询
        queryWrapper.eq(SysRole::getRoleId,roleId);
        SysRole sysRole = sysRoleMapper.selectOne(queryWrapper);
        return new ResponseResult(200,"根据角色id查询角色成功",sysRole);
    }
    //根据角色id删除角色
    @Override
    public ResponseResult delRole(String roleIds) {
        //先解析数组
        List<Long> list=new ArrayList<>();
        Arrays.stream(roleIds.split(",")).forEach((item->{
            list.add(Long.parseLong(item));
        }));

        for (Long roleId : list) {
            sysRoleMapper.deleteRoleByRoleId(roleId);
            //删除用户后,对应的权限就不处理了
        }

        return null;
    }

    //接收JSON字符串先进行处理
    @Override
    public Long addRole(RoleAddParams roleAddParams) {
        /**
         * 接收的参数为: roleName  status menuIds orgIds(不用处理,前端初始化出现)  menuCheckStrictly(可以不做处理)  comment
         */
        //存入role前先检查名字,不允许同名角色
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getDeleted,"0");
        queryWrapper.eq(SysRole::getRoleName,roleAddParams.getRoleName());
        //没有查询到返回null
        SysRole isExist = sysRoleMapper.selectOne(queryWrapper);
        if (isExist!=null){
            return -1L;
        }
        //新建一个role对象,封装数据(如何返回roleId)
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(roleAddParams.getRoleId());
        sysRole.setRoleName(roleAddParams.getRoleName());
        sysRole.setStatus(roleAddParams.getStatus());
        sysRole.setComment(roleAddParams.getComment());
        sysRoleMapper.insert(sysRole);
        //新增成功后,返回roleId
        return sysRole.getRoleId();
    }
}
