package com.fentric.service;

import com.fentric.pojo.IotOnline;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用来记录设备的在线情况,启动前要进行数据库的写入,关闭后要写入数据库 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-15
 */
public interface IotOnlineService extends IService<IotOnline> {
    //将查询得到的device_id,lost 封装进数组
    int[] putDevicesStatusIntoIntArray(Long gatewayId);
}
