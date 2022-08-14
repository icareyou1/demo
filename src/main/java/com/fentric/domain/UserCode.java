package com.fentric.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCode {
    //用户id
    private long userId;
    //网关设备id
    private long gatewayId;
    //用户发送的modbus
    private Modbus modbusUserSending;
    //网关错误信息
    private String error;

}
