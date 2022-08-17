package com.fentric.service;

import com.fentric.pojo.IotDevice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 设备信息表 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-08
 */
public interface IotDeviceService extends IService<IotDevice> {
    int[] queryDeviceCategoryByReceiveGateWayId(Long receiveGatewayId);
}
