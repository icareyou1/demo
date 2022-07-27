package com.fentric.filter;

import com.fentric.exception.TokenExpireException;
import com.fentric.exception.TokenParseException;
import com.fentric.pojo.LoginUser;
import com.fentric.utils.JwtUtils;
import com.fentric.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *    此处异常不能被全局异常捕获的原因:
 *    请求-->filter--->interceptor-->ControllerAdvice--->aspect-->Controller
 *                                                                          \
 *                                                                           \
 *                                                                           \/
 *                filter<---interceptor<---ControllerAdvice<---aspect<---响应
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;
    //redis中token失效时间,单位:分钟
    @Value("${redis.expireTime}")
    private int expireTime;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        //没有token放行
        if (token==null||"".equals(token)){
            filterChain.doFilter(request,response);
            //当过滤器返回的时候,阻止进一步往下执行
            return;
        }
        //解析token
        String loginUser_key;
        try {
            Claims claims = JwtUtils.parseToken(token);
            //获取userid
            loginUser_key = (String) claims.get("loginUser_key");
            //刷新redis中用户,并将权限放入securityContextHolder.getContext().setAuthentication(authenticationToken中)
            refreshAndSetLoginUser(loginUser_key);
        } catch (Exception e) {
            //done token解析异常
            //此处不应该抛出异常终止继续下去的行为
            //当token过期或者错误,应该继续向下验证用户名密码等
        }

        //放行
        filterChain.doFilter(request,response);
    }

    /**
     *  刷新拥有token的用户,并将权限进行封装
     * @param loginUser_key
     */
    private void refreshAndSetLoginUser(String loginUser_key){
        //根据用户id从redis中获取信息
        String redisKey = "login:" + loginUser_key;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        //todo  目前redis一个月
        if (loginUser!=null){
            if (expireTime>0){
                //刷新权限信息
                redisCache.setCacheObject(redisKey,loginUser,expireTime, TimeUnit.MINUTES);
            }else {
                //否则永久存储
                redisCache.setCacheObject(redisKey,loginUser);
            }
            //封装权限信息
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            //done 存入securityContextHolder中,  使用:   PermissionComponent类中会取出权限    /sysUser/logout
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        //done token过期就不用管,如果去登录页面就会进行用户名密码验证
        //如果去资源,则最终401

    }
}
