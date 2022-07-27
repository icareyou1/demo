package com.fentric.handler;

import com.alibaba.fastjson2.JSON;
import com.fentric.domain.ResponseResult;
import com.fentric.utils.WebUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * 用户没有认证访问资源出现的异常
 * 认证处理器,将异常信息返回给前端,未认证401
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    //此处request为传入的请求,也是就是说可以获取前台页面输入的用户名密码验证码
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //未认证401
        ResponseResult responseResult = new ResponseResult(401,"用户未认证,访问资源为:"+request.getRequestURI());
        String json = JSON.toJSONString(responseResult);
        //放回字符串给前端
        WebUtils.renderString(response,json);
    }
}
