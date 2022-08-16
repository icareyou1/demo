package com.fentric.service.impl;

import com.fentric.domain.Modbus;
import com.fentric.pojo.IotWarm;
import com.fentric.mapper.IotWarmMapper;
import com.fentric.service.IotWarmService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 告警表(借助缓存提高告警性能,如果和缓存中数据不同那就存入数据库,相同就不存入) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-09
 */
@Service
public class IotWarmServiceImpl extends ServiceImpl<IotWarmMapper, IotWarm> implements IotWarmService {
    @Autowired
    public IotWarmMapper iotWarmMapper;

    //将告警模块modbus结果存入表中
    @Override
    public void addWarmModbus(Modbus modbus) {
        IotWarm iotWarm = new IotWarm();
        iotWarm.setDeviceId(modbus.getDeviceId());
        iotWarm.setd1020to1021(modbus.getData());
        iotWarmMapper.insert(iotWarm);
    }
}
