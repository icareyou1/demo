package com.fentric.exception;
//用户名或者密码不匹配异常
public class UsernamePasswordMissMatchException extends UsernamePasswordException {
    public UsernamePasswordMissMatchException(){
        super("用户名或密码错误");
    }
}
