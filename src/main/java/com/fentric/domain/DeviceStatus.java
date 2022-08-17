package com.fentric.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Socket;

//必须保证设备的添加从1-32
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceStatus {
    private Long gateWayId;
    private Socket socket;
    //1在线，2不在线，3网关不在线  4服务器未启动    数组长度代表其下设备数
    int[] online;
    //表示每个从设备的设备类型
    int[] category;
    boolean shouldUpdate;
}
