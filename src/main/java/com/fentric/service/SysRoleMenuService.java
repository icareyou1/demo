package com.fentric.service;

import com.fentric.pojo.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色菜单权限表(属于多多对多关系) 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-14
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {
    //修改角色菜单表sys_role_menu
    void updateRoleMenu(List<Long> menuIds,Long roleId);
}
