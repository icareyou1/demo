package com.fentric.service;

import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.DeviceQueryParams;
import com.fentric.pojo.IotDevice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    //获取搜索栏设备IP下拉菜单
    Set<String> getDeviceIpForSearch();
    //返回设备列表
    ResponseResult listDevice(DeviceQueryParams deviceQueryParams);

    //添加设备的时候参数是否合法
    boolean validateAddDevice(IotDevice iotDevice);
    //修改设备的时候参数是否合法
    boolean validateUpdateDevice(IotDevice iotDevice);

    //更新设备,要有事务
    void updateDevice(IotDevice iotDevice);
    //新增设备,要有事务
    void addDevice(IotDevice iotDevice);

    //根据设备id删除设备,同时要删除其标签
    void delDeviceByDeviceIds(List<Long> list);
}
