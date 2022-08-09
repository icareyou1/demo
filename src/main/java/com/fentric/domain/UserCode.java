package com.fentric.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCode {
    private long userId;
    private long deviceId;
    private String code;
    private Object data;
    private String error;
}
