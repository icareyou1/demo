package com.fentric.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fentric.annotation.FentricLogin;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.UserQueryParams;
import com.fentric.domain.vo.RouterMenu;
import com.fentric.domain.vo.SelectRole;
import com.fentric.pojo.SysUser;
import com.fentric.service.SysMenuService;
import com.fentric.service.SysRoleService;
import com.fentric.service.SysUserService;

import com.fentric.utils.CommonUtils;
import com.fentric.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-20
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysMenuService sysMenuService;
    @Autowired
    SysRoleService sysRoleService;

    //此处得使用参数解析器,不能使用多个@requestbody
    //因为请求中只会有一个requestbody,所以不能使用多个@requestbody
    //其他方案:将参数封装到同一个pojo中
    @PostMapping("/login")
    //@PreAuthorize("@fentric.hasAuthority('system/user/index')")
    public ResponseResult login(@FentricLogin("sysUser") SysUser sysUser,@FentricLogin("code") String code, @FentricLogin("uuid") String uuid){
        return sysUserService.login(sysUser,code,uuid);
    }
    @RequestMapping("/logout")
    public ResponseResult logout(){
        return sysUserService.logout();
    }

    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo(){
        return sysUserService.getUserInfo();
    }
    @GetMapping("/getRouters")
    public ResponseResult getRouters(){
        List<RouterMenu> routerMenus = sysMenuService.selectMenuByUserId(SpringUtils.getSysUser().getUserId());
        //不添加map,直接返回数组
        return new ResponseResult(200,"获取路由信息成功",routerMenus);
    }
    //展示用户列表
    @GetMapping("/listUser")
    @PreAuthorize("@fentric.hasAuthority('system:user:list')")
    public ResponseResult listUser(UserQueryParams userQueryParams){
        return sysUserService.listUser(userQueryParams);
    }
    //修改用户状态
    @PutMapping("/updateUserStatusByUserId")
    @PreAuthorize("@fentric.hasAuthority('system:user:update')")
    public ResponseResult updateUserStatusByUserId(@FentricLogin("userId")Integer userId,@FentricLogin("status")String status){
        if (10010==userId){
            return new ResponseResult(500,"超级管理员禁止修改状态");
        }
        if (userId<=0|| !CommonUtils.isValidateStatus(status)){
            return new ResponseResult(500,"参数不合法");
        }
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getUserId,userId)
                .set(SysUser::getStatus,status);
        if (sysUserService.update(updateWrapper)){
            return new ResponseResult(200,"用户状态修改成功");
        }else {
            return new ResponseResult(500,"用户状态修改失败");
        }
    }
    //新增用户获取角色信息
    @GetMapping("/getRolesForAddUser")
    @PreAuthorize("@fentric.hasAuthority('system:user:query')")
    public ResponseResult getRolesForAddUser(){
        List<SelectRole> list = sysRoleService.getRolesForAddUser();
        return new ResponseResult(200,"获取新增用户信息成功",list);
    }

    //修改用户获取弹出框的信息
    @GetMapping("/getUserByUserId")
    @PreAuthorize("@fentric.hasAuthority('system:user:query')")
    public ResponseResult getUserByUserId(@RequestParam("userId") Long userId){
        if (userId<=0){
            return new ResponseResult(500,"修改用户获取参数非法");
        }
        //获取角色列表
        List<SelectRole> roleList = sysRoleService.getRolesForAddUser();
        //获取用户信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeleted,"0");
        queryWrapper.eq(SysUser::getUserId,userId);
        SysUser user = sysUserService.getOne(queryWrapper);
        //抹去密码
        user.setPassword(null);
        Map<String, Object> map = new HashMap<>();
        map.put("roles",roleList);
        map.put("form",user);
        return new ResponseResult(200,"修改用户信息获取成功",map);
    }
    //新增用户
    @PostMapping("/addUser")
    @PreAuthorize("@fentric.hasAuthority('system:user:add')")
    public ResponseResult addUser(@RequestBody SysUser sysUser){
        //返回true则校验通过
        if (!sysUserService.validateAddUser(sysUser)){
            return new ResponseResult(500,"添加用户参数不合法");
        }
        //进行密码转换
        sysUser.setPassword(new BCryptPasswordEncoder().encode(sysUser.getPassword()));
        //添加用户
        sysUserService.save(sysUser);
        return new ResponseResult(200,"添加用户成功");
    }
    //修改用户
    @PutMapping("/updateUser")
    @PreAuthorize("@fentric.hasAuthority('system:user:update')")
    public ResponseResult updateUser(@RequestBody SysUser sysUser){
        //返回true则校验通过
        if (!sysUserService.validateUpdateUser(sysUser)){
            return new ResponseResult(500,"修改用户参数不合法");
        }
        //修改用户
        if (sysUserService.updateById(sysUser)){
            return new ResponseResult(200,"修改用户成功");
        }else {
            return new ResponseResult(500,"修改用户失败");
        }
    }
    //删除用户
    @DeleteMapping("/delUser")
    @PreAuthorize("@fentric.hasAuthority('system:user:delete')")
    public ResponseResult delUser(@RequestParam("userIds")String userIds){
        //接收到的userIds不会为null
        List<SysUser> list=new ArrayList<>();
        //为了保证线程安全
        AtomicBoolean isValid= new AtomicBoolean(true);
        //不能在中间return,还会继续执行的
        //外部内执行完毕,下面可能还没执行完
        Arrays.stream(userIds.split(",")).forEach((item->{
            long userId = Long.parseLong(item);
            if (userId<=0) isValid.set(false);
            SysUser sysUser = new SysUser();
            sysUser.setDeleted("1");
            sysUser.setUserId(userId);
            list.add(sysUser);
        }));
        if (!isValid.get()||list.size()<=0){
            return new ResponseResult(500,"删除用户,参数不合法");
        }
        //删除用户,将deleted置为1    只有在list没有数据时才会报错
        sysUserService.updateBatchById(list);
        return new ResponseResult(200,"删除用户成功");
    }
    //修改用户密码
    @PutMapping("/resetUserPassword")
    @PreAuthorize("@fentric.hasAuthority('system:user:update')")
    public ResponseResult resetUserPassword(@FentricLogin("userId")Integer userId,@FentricLogin("password")String password){
        if (10010==userId){
            return new ResponseResult(500,"该用户禁止重置密码");
        }
        if (userId<=0||password.length()<4){
            return new ResponseResult(500,"重置密码参数不合法");
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(Long.valueOf(userId));
        sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
        if (sysUserService.updateById(sysUser)) {
            return new ResponseResult(200,"重置密码成功");
        }else{
            return new ResponseResult(500,"重置密码失败");
        }
    }
}
