package com.fentric.service;

import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.RoleAddOrUpdateParams;
import com.fentric.domain.requestVO.RoleQueryParams;
import com.fentric.domain.vo.SelectRole;
import com.fentric.pojo.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色表(与用户表1对1) 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
public interface SysRoleService extends IService<SysRole> {

    //传递的参数有pageNum,pageSize,roleName,status,params
    ResponseResult listRole(RoleQueryParams roleQueryParams);
    //根据角色id获取角色信息
    ResponseResult getRoleByRoleId(Long roleId);
    //根据传递的id删除角色 (axios不方便传递数组,改为字符串传递)
    ResponseResult delRole(String roleIds);
    //添加角色
    Long addRole(RoleAddOrUpdateParams roleAddOrUpdateParams);

    //角色信息,选择项
    List<SelectRole> getRolesForAddUser();
}
