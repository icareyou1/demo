package com.fentric.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Socket;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Modbus {
    //socket连接
    private Socket socket;
    //设备id共18位    [Long.parseLong(网关mac,16),高位补0]+0+[从站号，高位补0]
    private Long deviceId;
    //从站号
    private int slaveId;
    /**
     *  功能码
     *  3 查询
     *  6 写入单个
     *  16 批量写入
     */
    private int functionId;
    /**
     * 地址
     */
    private int address;
    /**
     * 查询长度
     */
    private int queryLen;
    /**
     * 返回处理后的报文信息(不带有帧头和校验码)
     */
    private String data;
    /**
     * 发送06功能写入的值
     */
    private int writeSingleValue;
    /**
     * 16功能号写多个寄存器
     */
    private int[] writeMultiValues;

    /**
     * 错误返回信息
     */
    private String error;
}
