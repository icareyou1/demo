package com.fentric.service.impl;

import com.fentric.pojo.IotOper;
import com.fentric.mapper.IotOperMapper;
import com.fentric.service.IotOperService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备操作表(记录用户对哪个设备进行了什么操作,) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-09
 */
@Service
public class IotOperServiceImpl extends ServiceImpl<IotOperMapper, IotOper> implements IotOperService {

}
