package com.fentric.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fentric.pojo.SysMenu;
import com.fentric.pojo.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-22
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    Set<String> selectPermsByRoleId(@Param(Constants.WRAPPER)Wrapper<String> wrapper);
    List<SysMenu> selectMenuAll();
    List<SysMenu> selectMenuByRoleId(@Param("roleId")Long roleId);
}
