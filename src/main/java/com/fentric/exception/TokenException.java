package com.fentric.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenException extends RuntimeException{
    //错误消息
    private String msg;
    //默认消息提示
    private final String defaultMsg="Token异常";

    @Override
    public String getMessage() {
        if (msg==null||"".equals(msg)){
            return defaultMsg;
        }
        return msg;
    }
}
