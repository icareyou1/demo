package com.fentric.handler;

import com.fentric.domain.ResponseResult;
import com.fentric.exception.CaptchaException;
import com.fentric.exception.TokenException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

//全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {

    //可以处理captcha等异常,不能处理jwt等过滤器异常,因为先被ExceptionTranslationFilter处理了
    @ExceptionHandler(RuntimeException.class)
    public ResponseResult handleCaptchaException(RuntimeException runtimeException){

        String message = runtimeException.getMessage();
        return new ResponseResult(500,message);
    }

    //请求方式异常,post和get方面的
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseResult HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,HttpServletRequest request){
        String message="请求路径:"+request.getRequestURI()+"不支持请求"+request.getMethod()+"方式";
        return new ResponseResult(500,message);
    }
}
