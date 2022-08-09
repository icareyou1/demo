package com.fentric.service;

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

}
