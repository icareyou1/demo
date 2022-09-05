package com.fentric.controller;

import com.fentric.annotation.FentricLogin;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.vo.RouterMenu;
import com.fentric.pojo.LoginUser;
import com.fentric.pojo.SysUser;
import com.fentric.service.SysMenuService;
import com.fentric.service.SysUserService;
import com.fentric.utils.JwtUtils;
import com.fentric.utils.RedisCache;
import com.fentric.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @RequestMapping("/hello")
    @PreAuthorize("@fentric.hasAuthority('system/user/index2')")
    /**
     * 自定义失败失效,返回 500不允许访问
     */
    //@CrossOrigin
    public String test(){
        return "hello";
    }


    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo(){
        return sysUserService.getUserInfo();
    }
    @GetMapping("/getRouters")
    public ResponseResult getRouters(){
        List<RouterMenu> routerMenus = sysMenuService.selectMenuByUserId(SpringUtils.getSysUser().getUserId());
        System.out.println(routerMenus);
        //不添加map,直接返回数组
        return new ResponseResult(200,"获取路由信息成功",routerMenus);
    }
}
