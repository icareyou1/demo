package com.fentric.service.impl;

import com.fentric.pojo.IotWarm;
import com.fentric.mapper.IotWarmMapper;
import com.fentric.service.IotWarmService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
