package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.DeviceStatus;
import com.fentric.domain.Modbus;
import com.fentric.mapper.IotWarmMapper;
import com.fentric.pojo.IotWarm;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.DBUtils;
import com.fentric.utils.IOUtils;
import com.fentric.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.fentric.modbus.DeviceDataPool.*;

/**
 * 时间由外面控制线程
 */
@Slf4j
public class ModuleWarmDetection implements Runnable{
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
        //Set<Long> gatewayIds = SocketMap.keySet();

        //如果某一设备发生超时，将影响下一个设备按序读取数据
        for (Long gatewayId : gatewayIds) {
            DeviceStatus deviceStatus = DeviceStatusMap.get(gatewayId);
            //如果取得null，说明有新的设备加入了
            if (deviceStatus.getOnline()==null){
                //1.查询设备表
                //2.查询状态表，没有状态的表默认为null
            }
            //获取socket，如果设备掉线则会被移除socket
            Socket socket = deviceStatus.getSocket();
            //如果socket为null,表示设备掉线了
            if (socket==null){
                //1.设置online[0]=2,表示不在线
                //2.其他设备置为3表示网关不在线
                //3.shouldUpdate标识记为true
                //4.跳过本次循环
            }
            /**
             * 同socket进行通信,
             * 1.如果通信失败,网关设置不在线,shouldUpdate置为true，跳过循环
             * 2.如果通信成功，且shouldupdate为true，就先写数据
             *     2.1如果online[0]=-2,则只将网关状态更新至表中其他不变化，并将shouldupdate置为false
             *     2.2如果online[0]=-1，将数组中小于0（小于说明该字段应该更新）的设备进行更新，更新后将值取绝对值，并将shouldUpdate置为false
             *
             *  3.和子设备进行通信，如果和上一次状态不同就进行记录（负数值），并将shouldUpdate置为true（设备状态有负数，如果相等就保持原样；不相等更新设备状态为负数）
             *  4.如果相同就，就下一轮
             *
             */
            //监测socket通信
            if (!IOUtils.queryGateWayStatus(socket)){
                //通信失败移除socket
                deviceStatus.setSocket(null);
                //设备状态
                int[] online = deviceStatus.getOnline();
                if (online[0]!=DEVICEOFFLINE) {
                    online[0]= -DEVICEOFFLINE;//注意符号（有负数表示未写入进数据库中）
                    deviceStatus.setOnline(online);
                    //下一次更新标志
                    deviceStatus.setShouldUpdate(true);
                }
                DeviceStatusMap.put(gatewayId,deviceStatus);
                continue;
            }else {//通信成功
                //检测到要写入设备状态缓存数据
                if (deviceStatus.isShouldUpdate()){
                    int[] online = deviceStatus.getOnline();
                    //网关设备不在线(只管一个就行)
                    if (online[0]==-DEVICEOFFLINE){
                        online[0]=DEVICEONLINE;
                        //todo 将网关状态写入数据库中

                        deviceStatus.setOnline(online);
                        deviceStatus.setShouldUpdate(false);
                    }
                    //网关设备在线(要管多个了，所以需要绝对值)   shouldUpdate为true，至少修改一个
                    if (Math.abs(online[0])==DEVICEONLINE){
                        //遍历设备状态找出没有写入数据库中的数据
                        for (int i = 0; i < online.length; i++) {
                            if (online[i]<0){
                                //标志复位
                                online[i]=Math.abs(online[i]);
                                //todo 写入数据库
                            }
                        }
                        deviceStatus.setOnline(online);
                        deviceStatus.setShouldUpdate(false);
                    }
                }else {//没有要写的数据
                    //确定online大小
                    int[] online = deviceStatus.getOnline();
                    int onlieLen = online.length;
                    //至少一个子设备才查询，否则不查询
                    if (onlieLen>1){
                        for (int salveindex = 1; salveindex < onlieLen; salveindex++) {
                            boolean slaveFlag=true;//和从设备通信结果标志，true表示成功，false表示本次失败
                            try {
                                //先查询告警量
                                Modbus modbus = new Modbus();
                                // todo  modbus指令设备编号，要从数据库中查询再缓存
                                modbus.setDeviceId(gatewayId*1000+salveindex);
                                modbus.setSocket(socket);
                                modbus.setSlaveId(salveindex);
                                modbus.setFunctionId(3);
                                modbus.setAddress(1020);
                                modbus.setQueryLen(2);
                                modbus = IOUtils.readHoldingRegisters(modbus);
                                //返回非空就代表查询失败
                                if (modbus.getError()!=null){
                                    //
                                    //todo  如何进行掉线检测
                                    continue;
                                }
                                //尝试获取缓存中的数据
                                String cacheData = WarmDataMap.get(modbus.getDeviceId());
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
                                    DBUtils.insertWarmModbus(modbus);
                                }
                                //缓存相同就不操作

                                //解析数据测试
                                String[] strs= new String[readHoldingRegistersResult.length() / 4];
                                for (int i=0;i<readHoldingRegistersResult.length()/4;i++){
                                    //因为这里的数据都比较小，直接转换即可
                                    strs[i] = CodeUtils.hexStrTobinStr(readHoldingRegistersResult.substring(i * 4, i * 4 + 4));
                                }
                                System.out.println("warm模块获取数据:");
                                for (String anStr : strs) {
                                    System.out.println(anStr);
                                }
                            } catch (IOException e) {
                                System.out.println("读取超时...");
                                slaveFlag=false;
                                e.printStackTrace();
                            }
                            //设备状态1
                            if (slaveFlag){
                                if (Math.abs(online[salveindex])!=DEVICEONLINE) online[salveindex]=-DEVICEONLINE;
                            }else {//设备状态2
                                if (Math.abs(online[salveindex])!=DEVICEOFFLINE) online[salveindex]=-DEVICEOFFLINE;
                            }
                            deviceStatus.setOnline(online);
                            deviceStatus.setShouldUpdate(true);
                        }
                    }
                }

            }


/*
            //如果socket突然掉线？ 服务器不知道
            //Socket socket = SocketMap.get(gatewayId);
            //进行下一轮
            if (socket==null) continue;
            try {
                //先查询告警量
                Modbus modbus = new Modbus();
                // todo  modbus指令设备编号，要从数据库中查询再缓存
                modbus.setDeviceId(gatewayId*1000+1);
                modbus.setSocket(socket);
                modbus.setSlaveId(1);
                modbus.setFunctionId(3);
                modbus.setAddress(1020);
                modbus.setQueryLen(2);
                modbus = IOUtils.readHoldingRegisters(modbus);
                //返回非空就代表查询失败
                if (modbus.getError()!=null){
                    //
                    //todo  如何进行掉线检测
                    continue;
                }
                //尝试获取缓存中的数据
                String cacheData = WarmDataMap.get(modbus.getDeviceId());
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
                    DBUtils.insertWarmModbus(modbus);

                    //IotWarm iotWarm = new IotWarm();
                    //iotWarm.setDeviceId(deviceId);
                    //iotWarm.setd1020to1021(readHoldingRegistersResult);
                    //iotWarmMapper = SpringUtils.getBean("iotWarmMapper", IotWarmMapper.class);
                    //iotWarmMapper.insert(iotWarm);

                }
                //缓存相同就不操作

                //解析数据测试
                String[] strs= new String[readHoldingRegistersResult.length() / 4];
                for (int i=0;i<readHoldingRegistersResult.length()/4;i++){
                    //因为这里的数据都比较小，直接转换即可
                    strs[i] = CodeUtils.hexStrTobinStr(readHoldingRegistersResult.substring(i * 4, i * 4 + 4));
                }
                System.out.println("warm模块获取数据:");
                for (String anStr : strs) {
                    System.out.println(anStr);
                }
            } catch (IOException e) {
                System.out.println("读取超时...");
                e.printStackTrace();
            }


*/
        }
    }
}
