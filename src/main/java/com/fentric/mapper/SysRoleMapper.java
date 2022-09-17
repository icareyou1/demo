package com.fentric.mapper;

import com.fentric.pojo.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 角色表(与用户表1对1) Mapper 接口
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {
    //设置标志位为1
    void deleteRoleByRoleId(@Param("roleId") Long roleId);
}
