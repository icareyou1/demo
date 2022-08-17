package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.Modbus;
import com.fentric.mapper.IotEventMapper;
import com.fentric.mapper.IotRunMapper;
import com.fentric.mapper.IotWarmMapper;
import com.fentric.pojo.IotEvent;
import com.fentric.pojo.IotRun;
import com.fentric.pojo.IotWarm;
import com.fentric.service.IotWarmService;
import com.fentric.utils.CodeUtils;
import com.fentric.utils.IOUtils;
import com.fentric.utils.SetObjComboFields;
import com.fentric.utils.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

import static com.fentric.modbus.DeviceDataPool.*;
import static com.fentric.modbus.ModuleEventDetection.EventIdMap;
import static com.fentric.modbus.ModuleWarmDetection.WarmDataMap;

@Slf4j
public class SPDModule extends FentricModule{
    //处理告警设备
    @Override
    public void handleWarmModule() {
        //1.解析warm告警模块数据(父类方法，可以重写)
        String warmData = getWarmData();
        //尝试获取缓存中的数据
        String cacheData = WarmDataMap.get(modbus.getDeviceId());
        log.info("缓存cacheData数据{}",cacheData);
        if (cacheData==null||"".equals(cacheData)){
            IotWarmMapper iotWarmMapper = SpringUtils.getBean("iotWarmMapper", IotWarmMapper.class);
            //查询数据库(查询该设备最新),将数据库数据放入缓存
            LambdaQueryWrapper<IotWarm> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(IotWarm::getd1020to1021);
            wrapper.eq(IotWarm::getDeviceId,modbus.getDeviceId());
            //根据创建时间降序,最保险为id排序
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
        String readHoldingRegistersResult = warmData;
        //和缓存数据不同，放入数据库中,和缓存
        log.info("readHoldingRegistersResult:{}",readHoldingRegistersResult);
        //缓存为null，和查询结果不同
        if (!readHoldingRegistersResult.equals(cacheData)){
            WarmDataMap.put(modbus.getDeviceId(),readHoldingRegistersResult);
            //放入数据库
            IotWarmService iotWarmServiceImpl = SpringUtils.getBean("iotWarmServiceImpl", IotWarmService.class);
            //todo 新建warmModbus
            Modbus warmModbus=modbus;
            warmModbus.setData(warmData);
            iotWarmServiceImpl.addWarmModbus(warmModbus);
        }
        //缓存相同就不操作
    }

    //处理运行状态
    @Override
    public void handleRunModule(){
        //切割出1022和1030-1079两个部分的数据
        String runData = getRunData();
        String d1022=runData.substring(0,4);
        String d1030_1079 = runData.substring(32);
        Integer[] ints_d1030_1079= new Integer[d1030_1079.length() / 4];
        for (int i=0;i<d1030_1079.length()/4;i++){
            //全部转换成0-65535之间数字
            ints_d1030_1079[i]=Integer.parseInt(d1030_1079.substring(i*4, i*4 + 4),16);
        }
        //遍历封装(报错的话，将只只会封装设备id和1022)
        try {
            IotRun iotRun = SetObjComboFields.setObj(IotRun.class, ints_d1030_1079, "d1030", "d1079");
            //设置设备id
            iotRun.setDeviceId(modbus.getDeviceId());
            iotRun.setd1022(d1022);
            //写入数据库
            IotRunMapper iotRunMapper = SpringUtils.getBean("iotRunMapper", IotRunMapper.class);
            iotRunMapper.insert(iotRun);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    //处理异常事件
    @Override
    public void handldEventModule() {
        //获取设备当前异常编号
        Integer eventId = Integer.parseInt(getEventIdData(),16);
        //获取缓存中记录编号
        Integer cacheEventId = EventIdMap.get(modbus.getDeviceId());
        //如果缓存获取不到，就从数据库中获取最新的
        if (cacheEventId==null){
            IotEventMapper iotEventMapper = SpringUtils.getBean("iotEventMapper", IotEventMapper.class);
            //查询数据库(查询该设备最新),将数据库数据放入缓存
            LambdaQueryWrapper<IotEvent> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(IotEvent::getD1090);
            wrapper.eq(IotEvent::getD1090,modbus.getDeviceId());
            wrapper.orderByDesc(IotEvent::getCreateTime).last("limit 1");
            IotEvent iotEvent = iotEventMapper.selectOne(wrapper);
            if (iotEvent!=null){
                Integer queryRecordId = iotEvent.getD1090();
                //数据库有数据就放入缓存中
                if (queryRecordId!=null){
                    EventIdMap.put(modbus.getDeviceId(),queryRecordId);
                    //再次读取缓存
                    cacheEventId=queryRecordId;
                }
            }
        }
        //再次查询缓存数据,为null说明第一次注入，默认查询一条记录(修改)
        if (cacheEventId==null){
            log.info("经过缓存的数据仍然为空{}",cacheEventId);
            Modbus write2080Modbus=createWriteSingleModbus(modbus.getSocket(),modbus.getSlaveId(),6,2080,eventId);
            //处理一次异常事件,eventId为当前异常事件编号
            handleOnceEvent(write2080Modbus,eventId);
        }else {//如果能从缓存中取得数据
            //todo  记得要刷入缓存
            //异常事件差值
            int eventCount = (eventId - cacheEventId)&0x0000FFFF;
            if (eventCount>16) eventCount=16;
            //从eventId往后数
            while (eventCount>0){
                int start = (eventId - eventCount+1)&0x0000FFFF;
                Modbus write2080Modbus=createWriteSingleModbus(modbus.getSocket(),modbus.getSlaveId(),6,2080,start);
                //如果查询失败直接退出
                boolean flag = handleOnceEvent(write2080Modbus, eventId);
                //处理异常事件失败
                if (!flag) return;
                eventCount--;
            }
            /*
            //0-------->cacheEventId===========>eventId------>65535
            if (eventCount>0){
                if (eventCount>16) eventCount=16;
                while (eventCount>0){
                    int start=eventId-eventCount+1;
                    Modbus write2080Modbus=createWriteSingleModbus(modbus.getSocket(),modbus.getSlaveId(),6,2080,start);
                    //如果查询失败直接退出
                    boolean flag = handleOnceEvent(write2080Modbus, eventId);
                    //处理异常事件失败
                    if (!flag) return;
                    eventCount--;
                }

            }
            //0=====>eventId---------->cacheEventId======>65535
            if (eventCount<0){
                int count=(eventId+cacheEventId+1+1)&0x0000FFFF;
                if (count>16) count=16;
                //从eventId往后数
                while (count>0){
                    int start = (eventId - count)&0x0000FFFF;
                    Modbus write2080Modbus=createWriteSingleModbus(modbus.getSocket(),modbus.getSlaveId(),6,2080,start);
                    //如果查询失败直接退出
                    boolean flag = handleOnceEvent(write2080Modbus, eventId);
                    //处理异常事件失败
                    if (!flag) return;
                    count--;
                }
            }*/
            //如果相等什么也不做
        }
    }
    //生成modbus读指令，节省代码量
    private Modbus createReadModbus(Socket socket,int slaveId,int functionId,int address,int queryLen){
        Modbus modbus = new Modbus();
        modbus.setSocket(socket);
        modbus.setSlaveId(slaveId);
        modbus.setFunctionId(functionId);
        modbus.setAddress(address);
        modbus.setQueryLen(queryLen);
        return modbus;
    }
    //生成modbus单个写指令
    private Modbus createWriteSingleModbus(Socket socket,int slaveId,int functionId,int address,int value){
        Modbus modbus = new Modbus();
        modbus.setSocket(socket);
        modbus.setSlaveId(slaveId);
        modbus.setFunctionId(functionId);
        modbus.setAddress(address);
        modbus.setWriteSingleValue(value);
        return modbus;
    }
    //处理单次事件编号,成功返回true，失败返回false
    private boolean handleOnceEvent(Modbus write2080Modbus,Integer eventId){
        try {
            write2080Modbus = IOUtils.writeSingleRegister(write2080Modbus);
            //返回非空就代表设置失败(设备不在线，不继续操作)
            if (write2080Modbus.getError()!=null){
                return false;
            }
            Modbus read1100_1165 = createReadModbus(modbus.getSocket(),modbus.getSlaveId(),3,1100,66);
            read1100_1165 = IOUtils.readHoldingRegisters(read1100_1165);
            Modbus read1166_1265 = createReadModbus(this.modbus.getSocket(), this.modbus.getSlaveId(),3,1166,100);
            read1166_1265 = IOUtils.readHoldingRegisters(read1166_1265);
            Modbus read1266_1365 = createReadModbus(this.modbus.getSocket(), this.modbus.getSlaveId(),3,1266,100);
            read1266_1365 = IOUtils.readHoldingRegisters(read1266_1365);
            //如果三次中有至少一次查询出来就继续查询
            if (read1100_1165.getError()!=null&&read1166_1265.getError()!=null&&read1266_1365.getError()!=null){
                return false;
            }else {//可能全部成功，可能存在失败
                if (read1100_1165.getError()!=null){
                    //再次查询
                    read1100_1165 = createReadModbus(modbus.getSocket(),modbus.getSlaveId(),3,1100,66);
                    if (read1100_1165.getError()!=null) return false;
                }
                if (read1166_1265.getError()!=null){
                    //再次查询
                    read1166_1265 = createReadModbus(modbus.getSocket(),modbus.getSlaveId(),3,1100,66);
                    if (read1166_1265.getError()!=null) return false;
                }
                if (read1266_1365.getError()!=null){
                    //再次查询
                    read1266_1365 = createReadModbus(modbus.getSocket(),modbus.getSlaveId(),3,1100,66);
                    if (read1266_1365.getError()!=null) return false;
                }
                //在此处说明全部成功
                StringBuilder resultStringBuilder = new StringBuilder();
                resultStringBuilder.append(read1100_1165.getData());
                resultStringBuilder.append(read1166_1265.getData());
                resultStringBuilder.append(read1266_1365.getData());
                Integer[] ints= new Integer[11];
                for (int i=0;i<11;i++){
                    ints[i]= Integer.parseInt(resultStringBuilder.substring(i*4, i*4 + 4),16);
                }
                //如果iotEvent反射构建对象失败，不会将数据存入数据库中
                try {
                    IotEvent iotEvent = SetObjComboFields.setObj(IotEvent.class, ints, "d1100", "d1110");
                    //设置deviceId
                    iotEvent.setDeviceId(modbus.getDeviceId());
                    //设置1090   (0-65535)当前事件号
                    iotEvent.setD1090(eventId);
                    //中间9个为空
                    //后面全为电流波形
                    String currentWaveForm = resultStringBuilder.substring(80);
                    iotEvent.setD1120to1365(currentWaveForm);
                    log.info("封装后的数据为{}",iotEvent);
                    IotEventMapper iotEventMapper = SpringUtils.getBean("iotEventMapper", IotEventMapper.class);
                    iotEventMapper.insert(iotEvent);
                    //将存入数据库的数据放入缓存
                    EventIdMap.put(modbus.getDeviceId(),iotEvent.getD1100());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    //todo 因为数据库中没有，所以算失败
                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (IOException e) {
            //socket的IO异常
            e.printStackTrace();
        }
        return true;
    }
}
