package com.fentric.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fentric.exception.UsernamePasswordMissMatchException;
import com.fentric.mapper.SysMenuMapper;
import com.fentric.mapper.SysUserMapper;
import com.fentric.pojo.LoginUser;
import com.fentric.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

//使用的是默认配置,spring security会在容器中找UserDetailsService
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Override
    public UserDetails loadUserByUsername(String username){
        /**
         * 查询用户信息
         */
        LambdaQueryWrapper<SysUser> sysUserWrapper = new LambdaQueryWrapper<>();
        sysUserWrapper.eq(SysUser::getUserName,username);
        SysUser sysUser = sysUserMapper.selectOne(sysUserWrapper);
        //如果没有查询到用户就抛出异常
        if (Objects.isNull(sysUser)){
            //done 数据库用户名密码错误   (交给DaoAuthenticationProvider来捕获异常,最好为下面的异常)
            //done 最后在AbstractUserDetailsAuthenticationProvider 变成BadCredentialsException异常
            throw new UsernameNotFoundException("用户名不存在");
        }
        //查询对应权限信息
        //将查询到的数据进行封装
        QueryWrapper sysMenuWrapper = new QueryWrapper<>();
        sysMenuWrapper.eq("role_id",sysUser.getRoleId());
        //排除空权限
        sysMenuWrapper.isNotNull("perms");
        Set<String> permissions= sysMenuMapper.selectPermsByRoleId(sysMenuWrapper);
        return new LoginUser(sysUser,permissions);
    }
}
