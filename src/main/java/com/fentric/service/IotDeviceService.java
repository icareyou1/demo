package com.fentric.service;

import com.fentric.domain.ResponseResult;
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
    //通过收到的网关id,查询设备分类
    int[] queryDeviceCategoryByReceiveGateWayId(Long receiveGatewayId);
    //返回给首页的设备状态
    ResponseResult getDeviceStatistic();
}
