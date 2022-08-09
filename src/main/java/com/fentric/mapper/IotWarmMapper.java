package com.fentric.mapper;

import com.fentric.pojo.IotWarm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 告警表(借助缓存提高告警性能,如果和缓存中数据不同那就存入数据库,相同就不存入) Mapper 接口
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-09
 */
public interface IotWarmMapper extends BaseMapper<IotWarm> {

}
