package com.fentric.service;

import com.fentric.domain.Modbus;
import com.fentric.pojo.IotWarm;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 告警表(借助缓存提高告警性能,如果和缓存中数据不同那就存入数据库,相同就不存入) 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-09
 */
public interface IotWarmService extends IService<IotWarm> {
    //将告警模块modbus结果存入表中
    void addWarmModbus(Modbus modbus);
    //根据设备id查询最新的d1020to1021字段
    IotWarm queryLatestD1020to1021ByDeviceId(Long deviceId);
}
