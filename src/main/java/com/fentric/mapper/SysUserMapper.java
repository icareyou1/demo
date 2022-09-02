package com.fentric.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.fentric.domain.vo.PageUser;
import com.fentric.pojo.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-20
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
    PageUser selectPageUserByUserId(@Param("userId")Long userId);

    //SysRole findSysRoleById(@Param(Constants.WRAPPER) Wrapper<SysRole> wrapper);
}
