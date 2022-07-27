package com.fentric.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.fentric.pojo.SysRole;
import org.apache.ibatis.annotations.Param;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    String test(@Param("id") Integer id);
    SysRole findSysRoleById(@Param(Constants.WRAPPER) Wrapper<SysRole> wrapper);
}
