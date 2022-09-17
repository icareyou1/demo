package com.fentric.service.impl;

import com.fentric.pojo.IotEvent;
import com.fentric.mapper.IotEventMapper;
import com.fentric.service.IotEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备异常事件表(根据事件编号来确定) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-09
 */
@Service
public class IotEventServiceImpl extends ServiceImpl<IotEventMapper, IotEvent> implements IotEventService {

}
