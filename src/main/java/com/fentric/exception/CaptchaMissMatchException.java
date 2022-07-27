package com.fentric.exception;

//验证码不匹配异常
public class CaptchaMissMatchException extends CaptchaException{
    public CaptchaMissMatchException(){
        super("验证码错误");
    }
}
