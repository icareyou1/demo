package com.fentric.service;

import com.fentric.mapper.IotRunMapper;
import com.fentric.pojo.IotRun;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 设备运行状态表(需要定时器定时采集) 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-05
 */
public interface IotRunService extends IService<IotRun> {
}
