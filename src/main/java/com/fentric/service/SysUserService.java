package com.fentric.service;

import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.UserQueryParams;
import com.fentric.pojo.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-20
 */
public interface SysUserService extends IService<SysUser> {
    ResponseResult login(SysUser sysUser,String code,String uuid);
    ResponseResult logout();
    ResponseResult getUserInfo();
    //查询用户列表,
    ResponseResult listUser(UserQueryParams userQueryParams);
    //验证添加用户是否有效
    boolean validateAddUser(SysUser sysUser);
    //验证修改用户是否有效
    boolean validateUpdateUser(SysUser sysUser);
}
