package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.DeviceStatus;
import com.fentric.domain.ResponseResult;
import com.fentric.modbus.DeviceDataPool;
import com.fentric.pojo.IotDevice;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.pojo.IotEvent;
import com.fentric.pojo.IotOper;
import com.fentric.pojo.IotWarm;
import com.fentric.service.IotDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fentric.service.IotEventService;
import com.fentric.service.IotOperService;
import com.fentric.service.IotWarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * <p>
 * 设备信息表 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-08
 */
@Service
public class IotDeviceServiceImpl extends ServiceImpl<IotDeviceMapper, IotDevice> implements IotDeviceService {
    @Autowired
    IotDeviceMapper iotDeviceMapper;
    @Autowired
    IotWarmService iotWarmService;
    @Autowired
    IotEventService iotEventService;
    @Autowired
    IotOperService iotOperService;

    @Override
    public int[] queryDeviceCategoryByReceiveGateWayId(Long receiveGatewayId) {
        LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(IotDevice::getDeviceId);
        wrapper.select(IotDevice::getCategoryId);
        wrapper.ge(IotDevice::getDeviceId,receiveGatewayId);
        wrapper.le(IotDevice::getDeviceId,receiveGatewayId+32);
        List<IotDevice> iotDevices = iotDeviceMapper.selectList(wrapper);
        //如果查询出来的设备为null
        if (iotDevices==null){
            return null;
        }
        int[] temp=new int[iotDevices.size()];
        for (int i = 0; i < iotDevices.size(); i++) {
            IotDevice iotDevice = iotDevices.get(i);
            if (iotDevice.getCategoryId()!=null){
                temp[i]= Math.toIntExact(iotDevice.getCategoryId());
            }else {//如果设备类型为null则初始化为0
                temp[i]=0;
            }
        }
        return temp;
    }

    /**
     *   返回给首页设备状态
     *       在线设备 离线设备
     *       今日告警 历史告警
     *       今日事件 历史事件
     *       今日操作 历史操作
     * @return
     */
    @Override
    public ResponseResult getDeviceStatistic() {
        //总设备
        Integer totalDeviceCount= Math.toIntExact(this.count());
        //在线设备
        Integer onlineDeviceCount=0;
        //离线设备
        Integer offlineDeviceCount=0;
        //今日告警
        Integer todayWarmCount=0;
        //历史告警
        Integer totalWarmCount=0;
        //今日事件
        Integer todayEventCount=0;
        //历史事件
        Integer totalEventCount=0;
        //今日操作
        Integer todayOperationCount=0;
        //历史操作
        Integer totalOperationCount=0;

        //1.查询在线情况
        Set<Long> gateWayIds = DeviceDataPool.DeviceStatusMap.keySet();
        if (gateWayIds!=null){
            for (Long gateWayId : gateWayIds) {
                DeviceStatus deviceStatus = DeviceDataPool.DeviceStatusMap.get(gateWayId);
                int[] online = deviceStatus.getOnline();
                //当网关不在线时,直接判定网关和子设备掉线
                if (online[0]!=1){
                    continue;
                }
                for (int i = 0; i < online.length; i++) {
                    //其他0或者2表示不在线(使用绝对值可以更加实时)
                    if (Math.abs(online[i])==1){
                        onlineDeviceCount++;
                    }
                }
            }
        }
        //2.离线设备
        offlineDeviceCount=totalDeviceCount-onlineDeviceCount;
        //3.今日告警数量
        LocalDateTime today_start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime today_end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        /*DateTimeFormatter time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String todayStart=time.format(today_start);
        String todayEnd = time.format(today_end);*/
        LambdaQueryWrapper<IotWarm> iotWarmQueryWrapper = new LambdaQueryWrapper<>();
        iotWarmQueryWrapper.ge(IotWarm::getCreateTime,today_start);
        iotWarmQueryWrapper.le(IotWarm::getCreateTime,today_end);
        todayWarmCount= Math.toIntExact(iotWarmService.count(iotWarmQueryWrapper));
        //4.历史告警
        totalWarmCount = Math.toIntExact(iotWarmService.count());
        //5.今日事件
        LambdaQueryWrapper<IotEvent> iotEventLambdaQueryWrapper = new LambdaQueryWrapper<>();
        iotEventLambdaQueryWrapper.ge(IotEvent::getCreateTime,today_start);
        iotEventLambdaQueryWrapper.le(IotEvent::getCreateTime,today_end);
        todayEventCount= Math.toIntExact(iotEventService.count(iotEventLambdaQueryWrapper));
        //6.历史事件
        totalEventCount= Math.toIntExact(iotEventService.count());
        //7.今日操作
        LambdaQueryWrapper<IotOper> iotOperLambdaQueryWrapper = new LambdaQueryWrapper<>();
        iotOperLambdaQueryWrapper.ge(IotOper::getCreateTime,today_start);
        iotOperLambdaQueryWrapper.le(IotOper::getCreateTime,today_end);
        todayOperationCount= Math.toIntExact(iotOperService.count(iotOperLambdaQueryWrapper));
        //8.历史操作
        totalOperationCount= Math.toIntExact(iotOperService.count());
        //9.封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("totalDeviceCount",totalDeviceCount);
        map.put("onlineDeviceCount",onlineDeviceCount);
        map.put("offlineDeviceCount",offlineDeviceCount);
        map.put("todayWarmCount",todayWarmCount);
        map.put("totalWarmCount",totalWarmCount);
        map.put("todayEventCount",todayEventCount);
        map.put("totalEventCount",totalEventCount);
        map.put("todayOperationCount",todayOperationCount);
        map.put("totalOperationCount",totalOperationCount);
        return new ResponseResult(200,"查询设备统计成功",map);
    }
}
