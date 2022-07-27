package com.fentric.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//验证码异常类,由子类来继承
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaException extends RuntimeException{
    //错误消息
    private String msg;
    //默认消息提示
    private final String defaultMsg="验证码异常";

    //在全局异常处理处,调用获取异常信息
    @Override
    public String getMessage() {
        if (msg==null||"".equals(msg)){
            return defaultMsg;
        }
        return msg;
    }
}
