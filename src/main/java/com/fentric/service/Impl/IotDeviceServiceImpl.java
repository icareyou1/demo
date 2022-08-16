package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.pojo.IotDevice;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.service.IotDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 设备信息表 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-08
 */
@Service
public class IotDeviceServiceImpl extends ServiceImpl<IotDeviceMapper, IotDevice> implements IotDeviceService {
    @Autowired
    IotDeviceMapper iotDeviceMapper;
    @Override
    public Long queryDeviceCountByReceiveGateWayId(Long receiveGatewayId) {
        LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(IotDevice::getDeviceId);
        wrapper.ge(IotDevice::getDeviceId,receiveGatewayId);
        wrapper.le(IotDevice::getDeviceId,receiveGatewayId+32);
        return iotDeviceMapper.selectCount(wrapper);
    }
}
