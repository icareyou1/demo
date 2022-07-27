package com.fentric.exception;

//token解析异常,原因:token被篡改...
public class TokenParseException extends TokenException{
    public TokenParseException(){
        super("token解析异常");
    }
}
