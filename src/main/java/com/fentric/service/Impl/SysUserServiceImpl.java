package com.fentric.service.Impl;

import com.fentric.domain.ResponseResult;
import com.fentric.exception.UsernamePasswordException;
import com.fentric.exception.UsernamePasswordMissMatchException;
import com.fentric.exception.CaptchaExpireException;
import com.fentric.exception.CaptchaMissMatchException;
import com.fentric.pojo.LoginUser;
import com.fentric.pojo.SysUser;
import com.fentric.mapper.SysUserMapper;
import com.fentric.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fentric.utils.JwtUtils;
import com.fentric.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-20
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    //springsecurity认证器
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Value("${redis.expireTime}")
    private int expireTime;

    @Override
    public ResponseResult login(SysUser sysUser,String code,String uuid) {
        //验证码处理部分,会自动捕获异常返回json,有效性只有一次
        validateCaptcha(code,uuid);
        //利用authenticationManager的authenticate进行认证
        Authentication authentication= null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(sysUser.getUserName(),sysUser.getPassword()));
        } catch (AuthenticationException e) {
            //认证没有通过,会有异常拦截器进行处理
            if (e instanceof BadCredentialsException){
                //return new ResponseResult(500,"用户名或密码错误");
                //done 用户名或密码不正确
                throw new UsernamePasswordMissMatchException();
            }else{
                //done 认证处理中的其他异常
                throw new UsernamePasswordException("用户名和密码匹配中的其他异常");
            }
        }
        //认证通过,使用userid生成token返回给前端
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //刷新redis中的用户,并且将LoginUser的userId封装进jwt中
        return refreshAndTokenLoginUser(loginUser);
    }
    /**
     *    刷新redis中的用户,并将其useId封装进jwt中
     * @param loginUser
     * @return
     */
    private ResponseResult refreshAndTokenLoginUser(LoginUser loginUser){
        String userId = loginUser.getSysUser().getUserId().toString();
        //认证后对象存入redis中
        String redisKey="login:"+userId;
        //todo 目前redis一个月
        if (expireTime>0){
            redisCache.setCacheObject(redisKey,loginUser,expireTime, TimeUnit.MINUTES);
        }else{
            //否则永久存储
            redisCache.setCacheObject(redisKey,loginUser);
        }
        //userid作为前后端发送的key
        String token = JwtUtils.createToken(userId);
        //jwt封装进responseResult中
        Map<String, String> map = new HashMap<>();
        map.put("token",token);
        return new ResponseResult(200,"登录成功",map);
    }

    /**
     * 从securityContextHolder获取id,删除redis中的缓存
     * @return
     */
    @Override
    public ResponseResult logout() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getSysUser().getUserId().toString();
        //从redis中删除
        String redisKey="login:"+userId;
        redisCache.deleteObject(redisKey);
        return new ResponseResult(200,"注销成功");
    }

    /**
     * 验证校验码
     * @param code
     * @param uuid
     */
    private void validateCaptcha(String code,String uuid){
        //验证码校验,捕获验证码异常后会自动返回前端
        //验证码只能使用一次
        String redisKeyCode="code:"+uuid;
        String captcha=redisCache.getCacheObject(redisKeyCode);
        redisCache.deleteObject(redisKeyCode);
        /**
         * 验证码抛出异常,能给前端返回json的原因是
         * 定义了全局的异常捕获器,能够将捕获到的异常进行封装,然后返回给前端
         */
        //抛出验证码过期异常
        if (captcha==null){
            throw new CaptchaExpireException();
        }
        //抛出验证码错误异常
        if(!code.equalsIgnoreCase(captcha)){
            throw new CaptchaMissMatchException();
        }
    }
}
