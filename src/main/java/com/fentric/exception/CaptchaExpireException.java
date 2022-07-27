package com.fentric.exception;
//验证码过期类
public class CaptchaExpireException extends CaptchaException{
    public CaptchaExpireException(){
        super("验证码过期");
    }
}
