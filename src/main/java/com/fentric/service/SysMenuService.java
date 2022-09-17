package com.fentric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.vo.RouterMenu;
import com.fentric.pojo.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-22
 */
public interface SysMenuService extends IService<SysMenu> {
    List<RouterMenu> selectMenuByUserId(Long userId);
    //根据用户id生成菜单树
    ResponseResult treeSelectByUserId(Long userId);
    //根据角色id生成菜单树
    ResponseResult updateRoleShowTreeSelect(Long roleId);
}
