package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.DeviceStatus;
import com.fentric.domain.Modbus;
import com.fentric.mapper.IotOnlineMapper;
import com.fentric.mapper.IotWarmMapper;
import com.fentric.pojo.IotOnline;
import com.fentric.pojo.IotWarm;
import com.fentric.service.IotOnlineService;
import com.fentric.service.IotWarmService;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.IOUtils;
import com.fentric.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.fentric.modbus.DeviceDataPool.*;

/**
 * 时间由外面控制线程
 */
@Slf4j
public class DetectDeviceOnline implements Runnable{
    //告警量缓存,用来对比
    public static Map<Long,String> WarmDataMap=new HashMap<>();
    /**
     *   思路： 根据socket
     */
    @Override
    public void run() {
        System.out.println("warm模块启动检测...");
        //todo  DeviceSocketMap，先进行获取online的获取，如果为空则从数据库中重新获取  如果socket获取失败则remove
        //gatewayIds为网关设备id，所有在线设备
        Set<Long> gatewayIds = DeviceStatusMap.keySet();
        //如果某一设备发生超时，将影响下一个设备按序读取数据的时间
        for (Long gatewayId : gatewayIds) {
            DeviceStatus deviceStatus = DeviceStatusMap.get(gatewayId);
            //如果取得null，说明有新的设备加入了
            if (deviceStatus.getOnline()==null){
                //1.查询设备表
                //2.查询状态表，没有状态的表默认为null
                IotOnlineService iotOnlineServiceImpl = SpringUtils.getBean("iotOnlineServiceImpl", IotOnlineService.class);
                int[] ints = iotOnlineServiceImpl.putDevicesStatusIntoIntArray(gatewayId);
                deviceStatus.setOnline(ints);
            }
            //获取socket，如果设备掉线则会被移除socket
            Socket socket = deviceStatus.getSocket();
            //如果socket为null,表示设备掉线了
            if (socket==null){
                //更新网关状态写入数据库
                IotOnline iotOnline = new IotOnline();
                iotOnline.setDeviceId(gatewayId);
                iotOnline.setLost("2");
                IotOnlineMapper iotOnlineMapper = SpringUtils.getBean("iotOnlineMapper", IotOnlineMapper.class);
                iotOnlineMapper.insert(iotOnline);
                log.info("socket为null，网关设备{}掉线",gatewayId);
                continue;
            }
            /**
             * 同socket进行通信,
             * 1.如果通信失败,网关设置不在线,跳过循环
             * 2.如果通信成功
             *     是否有子设备状态变化，有的话就直接先处理子设备
             *    处理网关状态
             *  3.和子设备进行通信，如果和上一次状态不同就进行记录（负数值），并将shouldUpdate置为true（设备状态有负数，如果相等就保持原样；不相等更新设备状态为负数）
             *  4.如果相同就，就下一轮
             *
             */
            //socket通信失败
            if (!IOUtils.queryGateWayStatus(socket)){
                log.info("网关通信失败,子设备缓存写入状态：{}",deviceStatus.isShouldUpdate());
                //通信失败移除socket
                try {
                    deviceStatus.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //只有null的话会导致一直获取null（因为注册处认为设备还在）
                deviceStatus.setSocket(null);
                //1.获取设备在线状态
                int[] online = deviceStatus.getOnline();
                //子设备应该更新缓存(需要先断设备电源，在断网关连接)
                if (deviceStatus.isShouldUpdate()){
                    //掉线标志
                    int index=0;
                    //如果上一次网关在线，从后往前扫描缓存队列
                    for (int i=online.length-1;i>0;i--){
                        //网关掉线，后面的都会通信失败
                        if (Math.abs(online[i])==1){
                            index=i;
                            break;
                        }
                    }
                    //重新扫描缓存队列，在掉线标志前的就修正写入数据库，在掉线标志后的设备状态置为3
                    for (int i=1;i<index+1;i++){
                        if (online[i]<0){
                            //标志复位
                            online[i]=Math.abs(online[i]);
                            //todo 写入数据库
                            IotOnline iotOnline = new IotOnline();
                            iotOnline.setDeviceId(gatewayId+i);
                            iotOnline.setLost(String.valueOf(online[i]));
                            IotOnlineMapper iotOnlineMapper = SpringUtils.getBean("iotOnlineMapper", IotOnlineMapper.class);
                            iotOnlineMapper.insert(iotOnline);
                        }
                    }
                    for (int i = index+1; i < online.length; i++) {
                        online[i]=3;
                    }
                    log.info("子设备掉线索引为{}",index);
                }
                //写入网关状态
                IotOnline iotOnline = new IotOnline();
                iotOnline.setDeviceId(gatewayId);
                iotOnline.setLost(String.valueOf(online[0]));
                IotOnlineMapper iotOnlineMapper = SpringUtils.getBean("iotOnlineMapper", IotOnlineMapper.class);
                iotOnlineMapper.insert(iotOnline);

                online[0]= DEVICEOFFLINE;//注意符号（有负数表示未写入进数据库中）
                deviceStatus.setOnline(online);
                DeviceStatusMap.put(gatewayId,deviceStatus);
                log.info("和网关通信失败....");
                continue;
            }else {//通信成功
                log.info("网关通信成功,子设备缓存写入状态：{}",deviceStatus.isShouldUpdate());
                int[] online = deviceStatus.getOnline();
                //子设备是否有状态变化
                if (deviceStatus.isShouldUpdate()){
                    //遍历设备状态找出没有写入数据库中的数据
                    for (int i = 1; i < online.length; i++) {
                        if (online[i]<0){
                            //标志复位
                            online[i]=Math.abs(online[i]);
                            //todo 写入数据库
                            IotOnline iotOnline = new IotOnline();
                            iotOnline.setDeviceId(gatewayId+i);
                            iotOnline.setLost(String.valueOf(online[i]));
                            IotOnlineMapper iotOnlineMapper = SpringUtils.getBean("iotOnlineMapper", IotOnlineMapper.class);
                            iotOnlineMapper.insert(iotOnline);
                        }
                    }
                    deviceStatus.setOnline(online);
                    deviceStatus.setShouldUpdate(false);
                }
                //确定online大小
                IotOnline iotOnline = new IotOnline();
                iotOnline.setDeviceId(gatewayId);
                iotOnline.setLost(String.valueOf(online[0]));
                IotOnlineMapper iotOnlineMapper = SpringUtils.getBean("iotOnlineMapper", IotOnlineMapper.class);
                iotOnlineMapper.insert(iotOnline);
                online[0]= DEVICEONLINE;
                int onlieLen = online.length;
                //至少一个子设备才查询，否则不查询
                if (onlieLen>1){
                    for (int salveindex = 1; salveindex < onlieLen; salveindex++) {
                        boolean slaveFlag=true;//和从设备通信结果标志，true表示成功，false表示本次失败
                        try {
                            //先查询告警量
                            Modbus modbus = new Modbus();
                            // todo  modbus指令设备编号，要从数据库中查询再缓存
                            modbus.setDeviceId(gatewayId+salveindex);
                            modbus.setSocket(socket);
                            modbus.setSlaveId(salveindex);
                            modbus.setFunctionId(3);
                            modbus.setAddress(1020);
                            modbus.setQueryLen(2);
                            modbus = IOUtils.readHoldingRegisters(modbus);
                            //返回非空就代表查询失败
                            if (modbus.getError()!=null){
                                //todo  如何进行掉线检测
                                slaveFlag=false;
                                //设备状态1
                                if (slaveFlag){
                                    if (Math.abs(online[salveindex])!=DEVICEONLINE) {
                                        online[salveindex]=-DEVICEONLINE;
                                        deviceStatus.setShouldUpdate(true);
                                    }
                                }else {//设备状态2
                                    if (Math.abs(online[salveindex])!=DEVICEOFFLINE) {
                                        online[salveindex]=-DEVICEOFFLINE;
                                        deviceStatus.setShouldUpdate(true);
                                    }
                                }
                                deviceStatus.setOnline(online);
                                DeviceStatusMap.put(gatewayId,deviceStatus);
                                log.info("从设备{}查询失败，查询下一个从设备",modbus.getDeviceId());
                                continue;
                            }
                            //业务模块，从上面获取modbus

                            //尝试获取缓存中的数据
                            /*String cacheData = WarmDataMap.get(modbus.getDeviceId());
                            log.info("缓存cacheData数据{}",cacheData);
                            if (cacheData==null||"".equals(cacheData)){
                                IotWarmMapper iotWarmMapper = SpringUtils.getBean("iotWarmMapper", IotWarmMapper.class);
                                //查询数据库(查询该设备最新),将数据库数据放入缓存
                                LambdaQueryWrapper<IotWarm> wrapper = new LambdaQueryWrapper<>();
                                wrapper.select(IotWarm::getd1020to1021);
                                wrapper.eq(IotWarm::getDeviceId,modbus.getDeviceId());
                                //根据创建时间降序
                                wrapper.orderByDesc(IotWarm::getCreateTime).last("limit 1");
                                IotWarm iotWarm = iotWarmMapper.selectOne(wrapper);
                                //System.out.println("查询到的数据为："+iotWarm);
                                if (iotWarm!=null){
                                    String queryData = iotWarm.getd1020to1021();
                                    if (queryData!=null){
                                        WarmDataMap.put(modbus.getDeviceId(),queryData);
                                        //再次读取缓存
                                        cacheData=queryData;
                                        log.info("再次取得缓存cacheData后数据{}",cacheData);
                                    }
                                }
                            }
                            String readHoldingRegistersResult = modbus.getData();
                            //和缓存数据不同，放入数据库中,和缓存
                            log.info("readHoldingRegistersResult:{}",readHoldingRegistersResult);

                            if (!readHoldingRegistersResult.equals(cacheData)){
                                WarmDataMap.put(modbus.getDeviceId(),readHoldingRegistersResult);
                                //放入数据库
                                IotWarmService iotWarmServiceImpl = SpringUtils.getBean("iotWarmServiceImpl", IotWarmService.class);
                                iotWarmServiceImpl.addWarmModbus(modbus);
                            }*/
                            //缓存相同就不操作


                        } catch (IOException e) {
                            System.out.println("读取超时...");
                            slaveFlag=false;
                            e.printStackTrace();
                        }
                        //设备状态1
                        if (slaveFlag){
                            if (Math.abs(online[salveindex])!=DEVICEONLINE) {
                                online[salveindex]=-DEVICEONLINE;
                                deviceStatus.setShouldUpdate(true);
                            }
                        }else {//设备状态2
                            if (Math.abs(online[salveindex])!=DEVICEOFFLINE) {
                                online[salveindex]=-DEVICEOFFLINE;
                                deviceStatus.setShouldUpdate(true);
                            }
                        }
                        deviceStatus.setOnline(online);
                        DeviceStatusMap.put(gatewayId,deviceStatus);
                    }
                }
            }
        }
    }
}
