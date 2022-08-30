package com.fentric.handler;

import com.alibaba.fastjson2.JSON;
import com.fentric.domain.ResponseResult;
import com.fentric.pojo.LoginUser;
import com.fentric.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//注意:bug   未授权异常会先被全局异常给处理掉了
/**
 * 用户登录了,但是没有相关权限出现的异常
 * 授权处理器,403未授权
 */
@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseResult responseResult;
        if ("/sysUser/login".equals(request.getRequestURI())){
            responseResult = new ResponseResult(403, "已登录用户禁止访问:/sysUser/login");
        }else {
            //未授权403
            responseResult = new ResponseResult(403, "用户没有访问"+request.getRequestURI()+"权限");
        }
        String json = JSON.toJSONString(responseResult);
        WebUtils.renderString(response,json);
    }
}