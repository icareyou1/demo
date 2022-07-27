package com.fentric.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fentric.annotation.FentricLogin;
import com.fentric.pojo.SysUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;

public class LoginParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    //如果注解是@FentricLogin就支持
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(FentricLogin.class);
    }

    //通过request获取body参数,request中的流数据只能获取一次
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //每个标有@FentricLogin的注解都会进来一次
        String annotationValue = parameter.getParameterAnnotation(FentricLogin.class).value();
        //获取request
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        //如果attribute为null,说明第一次进入,则要从body中获取数据
        Object attribute = request.getAttribute(annotationValue);
        if (attribute==null){
            request= wrapRequest(request);
        }
        //当注解值为sysUser时,封装userName和password
        if ("sysUser".equals(annotationValue)){
            SysUser sysUser = new SysUser();
            sysUser.setUserName((String) request.getAttribute("userName"));
            sysUser.setPassword((String) request.getAttribute("password"));
            return sysUser;
        }
        return attribute;
    }

    //包装request,将body中数据转为object存入attribute中
    private HttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        //拼接字符
        StringBuilder stringBuilder = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        while ((len=reader.read(buf))!=-1){
            stringBuilder.append(buf);
        }
        JSONObject jsonObject = JSON.parseObject(stringBuilder.toString());
        Set<String> keys = jsonObject.keySet();
        for (String key : keys) {
            request.setAttribute(key,jsonObject.get(key));
        }
        return request;
    }
}
