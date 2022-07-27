package com.fentric.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//认证过程中出现的异常
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernamePasswordException extends RuntimeException{
    private String msg;
    private final String defaultMsg="认证过程异常";

    @Override
    public String getMessage() {
        if (msg==null||"".equals(msg)){
            return defaultMsg;
        }
        return msg;
    }
}
