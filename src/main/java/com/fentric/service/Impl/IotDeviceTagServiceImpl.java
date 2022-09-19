package com.fentric.service.impl;

import com.fentric.pojo.IotDeviceTag;
import com.fentric.mapper.IotDeviceTagMapper;
import com.fentric.service.IotDeviceTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备标签表(属于多对多关系) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-18
 */
@Service
public class IotDeviceTagServiceImpl extends ServiceImpl<IotDeviceTagMapper, IotDeviceTag> implements IotDeviceTagService {

}
