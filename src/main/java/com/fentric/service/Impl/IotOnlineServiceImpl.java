package com.fentric.service.impl;

import com.fentric.pojo.IotOnline;
import com.fentric.mapper.IotOnlineMapper;
import com.fentric.service.IotOnlineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用来记录设备的在线情况,启动前要进行数据库的写入,关闭后要写入数据库 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-15
 */
@Service
public class IotOnlineServiceImpl extends ServiceImpl<IotOnlineMapper, IotOnline> implements IotOnlineService {
    @Autowired
    public IotOnlineMapper iotOnlineMapper;
    @Override
    public int[] putDevicesStatusIntoIntArray(Long gatewayId) {
        List<IotOnline> iotOnlines = iotOnlineMapper.queryGatewayAndDevicesStatusByGatewayId(gatewayId);
        //list不会为null，至少为1才会进入此
        int[] temp=new int[iotOnlines.size()];
        for (int i = 0; i < iotOnlines.size(); i++) {
            IotOnline iotOnline = iotOnlines.get(i);
            if (iotOnline.getLost()!=null){
                temp[i]= Integer.parseInt(iotOnline.getLost());
            }else {//如果为空就初始化为0
                temp[i]=0;
            }
        }
        return temp;
    }
}
