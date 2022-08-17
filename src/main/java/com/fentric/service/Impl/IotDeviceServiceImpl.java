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
    public int[] queryDeviceCategoryByReceiveGateWayId(Long receiveGatewayId) {
        LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(IotDevice::getDeviceId);
        wrapper.select(IotDevice::getCategoryId);
        wrapper.ge(IotDevice::getDeviceId,receiveGatewayId);
        wrapper.le(IotDevice::getDeviceId,receiveGatewayId+32);
        List<IotDevice> iotDevices = iotDeviceMapper.selectList(wrapper);
        //如果查询出来的设备为null
        if (iotDevices==null){
            return null;
        }
        int[] temp=new int[iotDevices.size()];
        for (int i = 0; i < iotDevices.size(); i++) {
            IotDevice iotDevice = iotDevices.get(i);
            if (iotDevice.getCategoryId()!=null){
                temp[i]= Math.toIntExact(iotDevice.getCategoryId());
            }else {//如果设备类型为null则初始化为0
                temp[i]=0;
            }
        }

        return temp;
    }
}
