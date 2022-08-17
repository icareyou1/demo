package com.fentric.mapper;

import com.fentric.pojo.IotOnline;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用来记录设备的在线情况,启动前要进行数据库的写入,关闭后要写入数据库 Mapper 接口
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-15
 */
public interface IotOnlineMapper extends BaseMapper<IotOnline> {
    //根据网关设备id查询其下设备（包括网关）的运行状态---->返回    device_id,lost两个字段
    List<IotOnline> queryGatewayAndDevicesStatusByGatewayId(@Param("gatewayId") Long gatewayId);
}
