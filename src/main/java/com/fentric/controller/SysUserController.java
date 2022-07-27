package com.fentric.controller;

import com.fentric.annotation.FentricLogin;
import com.fentric.domain.ResponseResult;
import com.fentric.pojo.LoginUser;
import com.fentric.pojo.SysUser;
import com.fentric.service.SysUserService;
import com.fentric.utils.JwtUtils;
import com.fentric.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
public class SysUserController {
    @Autowired
    SysUserService sysUserService;

    //此处得使用参数解析器,不能使用多个@requestbody
    //因为请求中只会有一个requestbody,所以不能使用多个@requestbody
    //其他方案:将参数封装到同一个pojo中
    @PostMapping("/sysUser/login")
    public ResponseResult login(@FentricLogin("sysUser") SysUser sysUser,@FentricLogin("code") String code, @FentricLogin("uuid") String uuid){
        return sysUserService.login(sysUser,code,uuid);
    }
    @RequestMapping("/sysUser/logout")
    public ResponseResult logout(){
        return sysUserService.logout();
    }

    @RequestMapping("/sysUser/hello")
    @PreAuthorize("@fentric.hasAuthority('system:user:list')")
    //@CrossOrigin
    public String test(){
        String say = say();
        if (say==null){
            return "没有访问say()权限";
        }
        return say();
    }
    //基于方法权限控制
    public String say(){
        return "say...";
    }
}
