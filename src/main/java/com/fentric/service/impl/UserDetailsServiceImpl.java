package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fentric.mapper.SysMenuMapper;
import com.fentric.mapper.SysUserMapper;
import com.fentric.pojo.LoginUser;
import com.fentric.pojo.SysMenu;
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
        sysUserWrapper.eq(SysUser::getDeleted,"0");
        sysUserWrapper.eq(SysUser::getStatus,"0");
        sysUserWrapper.eq(SysUser::getUserName,username);
        SysUser sysUser = sysUserMapper.selectOne(sysUserWrapper);
        //如果没有查询到用户就抛出异常
        if (Objects.isNull(sysUser)){
            //done 数据库用户名密码错误   (交给DaoAuthenticationProvider来捕获异常,最好为下面的异常)
            //done 最后在AbstractUserDetailsAuthenticationProvider 变成BadCredentialsException异常
            throw new UsernameNotFoundException("用户名不存在");
        }
        Set<String> permissions=new HashSet<>();
        //如果为超级管理员userId则查询所有权限
        if (sysUser.getUserId()==10010L){
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getDeleted,"0");
            queryWrapper.eq(SysMenu::getStatus,"0");
            queryWrapper.select(SysMenu::getPerms);
            List<SysMenu> sysMenus = sysMenuMapper.selectList(queryWrapper);
            //List<SysMenu> sysMenus = sysMenuMapper.selectMenuAll();
            for (SysMenu sysMenu : sysMenus) {
                if (sysMenu.getPerms()!=null&&!"".equals(sysMenu.getPerms())){
                    permissions.add(sysMenu.getPerms());
                }
            }
            //System.out.println(permissions);
        }else {
            //将查询到的数据进行封装
            QueryWrapper sysMenuWrapper = new QueryWrapper<>();
            sysMenuWrapper.eq("rm.deleted","0");
            sysMenuWrapper.eq("rm.status","0");
            sysMenuWrapper.eq("m.deleted","0");
            sysMenuWrapper.eq("m.status","0");
            sysMenuWrapper.eq("role_id",sysUser.getRoleId());
            sysMenuWrapper.ne("perms","");
            //todo 排除空权限   空字符串
            sysMenuWrapper.isNotNull("perms");
            permissions= sysMenuMapper.selectPermsByRoleId(sysMenuWrapper);
        }
        return new LoginUser(sysUser,permissions);
    }
}
