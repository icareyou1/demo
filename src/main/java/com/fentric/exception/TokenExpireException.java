package com.fentric.exception;

//token过期异常
public class TokenExpireException extends TokenException{
    public TokenExpireException(){
        super("token过期");
    }
}
