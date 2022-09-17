package com.fentric.service.impl;

import com.fentric.pojo.IotRun;
import com.fentric.mapper.IotRunMapper;
import com.fentric.service.IotRunService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备运行状态表(需要定时器定时采集) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-05
 */
@Service
public class IotRunServiceImpl extends ServiceImpl<IotRunMapper, IotRun> implements IotRunService {

}
