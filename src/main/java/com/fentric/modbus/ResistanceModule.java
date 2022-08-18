package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.Modbus;
import com.fentric.mapper.IotEventMapper;
import com.fentric.mapper.IotRunMapper;
import com.fentric.pojo.IotEvent;
import com.fentric.pojo.IotRun;
import com.fentric.pojo.IotWarm;
import com.fentric.service.IotWarmService;
import com.fentric.utils.IOUtils;
import com.fentric.utils.SetObjComboFields;
import com.fentric.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.fentric.modbus.DeviceDataPool.ThreadPool;
import static com.fentric.modbus.ModuleEventDetection.EventIdMap;
import static com.fentric.modbus.ModuleWarmDetection.WarmDataMap;

@Slf4j
public class ResistanceModule extends FentricModule{
    private List<StringBuilder> EventDataList=new ArrayList<>();
    //处理告警模块
    @Override
    public void handleWarmModule() {
        //1.解析warm告警模块数据(父类方法，可以重写)
        String warmData = getWarmData();
        //尝试获取缓存中的数据
        String cacheData = WarmDataMap.get(modbus.getDeviceId());
        log.info("获取告警模块缓存量数据{}",cacheData);
        if (cacheData==null||"".equals(cacheData)){
            //查询再IotWarm表中 根据设备号查询d1020to1021字段  ，返回IotWarm
            IotWarmService iotWarmService = SpringUtils.getBean("iotWarmServiceImpl", IotWarmService.class);
            //如果没有查到返回null
            IotWarm iotWarm = iotWarmService.queryLatestD1020to1021ByDeviceId(modbus.getDeviceId());
            //成功从数据库中查询
            if (iotWarm!=null){
                String queryData = iotWarm.getd1020to1021();
                if (queryData!=null){
                    WarmDataMap.put(modbus.getDeviceId(),queryData);
                    //再次读取缓存
                    cacheData=queryData;
                    log.info("再次获取告警模块缓存量数据{}",cacheData);
                }
            }
        }
        String readHoldingRegistersResult = warmData;
        //和缓存数据不同，放入数据库中,和缓存
        log.info("告警模块中readHoldingRegistersResult:{}",readHoldingRegistersResult);
        //即使缓存为null和查询结果也会不同
        if (!readHoldingRegistersResult.equals(cacheData)){
            //放入数据库
            IotWarmService iotWarmServiceImpl = SpringUtils.getBean("iotWarmServiceImpl", IotWarmService.class);
            //done 新建warmModbus  (只使用了  设备号和  d1020to1021)这两个字段
            Modbus warmModbus=new Modbus();
            warmModbus.setDeviceId(modbus.getDeviceId());
            warmModbus.setData(warmData);
            iotWarmServiceImpl.addWarmModbus(warmModbus);
            //更新缓存
            WarmDataMap.put(modbus.getDeviceId(),readHoldingRegistersResult);
        }
        //和缓存相同就不操作
    }

    //处理运行状态
    @Override
    public void handleRunModule(){
        //切割出1022和1030-1035两个部分的数据
        String runData = getRunData();
        String d1022=runData.substring(0,4);
        String d1030_1035 = runData.substring(32,56);
        Integer[] ints_d1030_1035= new Integer[d1030_1035.length() / 4];
        for (int i=0;i<ints_d1030_1035.length;i++){
            //全部转换成0-65535之间数字
            ints_d1030_1035[i]=Integer.parseInt(d1030_1035.substring(i*4, i*4 + 4),16);
        }
        //遍历封装(报错的话，将只只会封装设备id和1022)
        try {
            IotRun iotRun = SetObjComboFields.setObj(IotRun.class, ints_d1030_1035, "d1030", "d1035");
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

    //处理异常事件(查询完所有再进行数据封装)
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
            wrapper.eq(IotEvent::getDeviceId,modbus.getDeviceId());
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
        log.info("当前事件号:{},缓存事件号:{}",eventId,cacheEventId);
        //再次查询缓存数据,为null说明第一次注入，默认查询一条记录(修改)
        if (cacheEventId==null){
            log.info("经过缓存的数据仍然为空{}",cacheEventId);
            Modbus write2080Modbus=createWriteSingleModbus(modbus.getSocket(),modbus.getSlaveId(),6,2080,eventId);
            //处理一次异常事件,eventId为当前异常事件编号
            handleOnceEvent(write2080Modbus,eventId);
        }else {//如果能从缓存中取得数据
            //异常事件差值
            int eventCount = (eventId - cacheEventId)&0x0000FFFF;
            log.info("异常事件count差值{}",eventCount);
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
            //代表列表中没有数据，不开启新线程
            if (EventDataList.size()==0){
                log.info("设备{},事件列表中没有数据",modbus.getDeviceId());
                return;
            }
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("============设备{},handleEventData线程开启==================",modbus.getDeviceId());
                    handleEventData();
                    log.info("============设备{},handleEventData线程结束==================",modbus.getDeviceId());
                }
            });
            //eventCount==0什么也不做,表示没有异常事件发生
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
                    read1100_1165 = IOUtils.readHoldingRegisters(read1100_1165);
                    if (read1100_1165.getError()!=null) return false;
                }
                if (read1166_1265.getError()!=null){
                    //再次查询
                    read1166_1265 = createReadModbus(modbus.getSocket(),modbus.getSlaveId(),3,1100,66);
                    read1166_1265 = IOUtils.readHoldingRegisters(read1166_1265);
                    if (read1166_1265.getError()!=null) return false;
                }
                if (read1266_1365.getError()!=null){
                    //再次查询
                    read1266_1365 = createReadModbus(modbus.getSocket(),modbus.getSlaveId(),3,1100,66);
                    read1266_1365 = IOUtils.readHoldingRegisters(read1266_1365);
                    if (read1266_1365.getError()!=null) return false;
                }
                //在此处说明全部成功
                StringBuilder resultStringBuilder = new StringBuilder();
                resultStringBuilder.append(read1100_1165.getData());
                resultStringBuilder.append(read1166_1265.getData());
                resultStringBuilder.append(read1266_1365.getData());
                //如果此线程比较慢，没有及时更新？？？ 基本不存在，因为DetectDeviceOnline结束后线程休眠
                EventDataList.add(resultStringBuilder);
            }
        } catch (IOException e) {
            //socket的IO异常(出现机会少)
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //处理一次异常事件编号
    private void handleEventData(){
        for (StringBuilder resultStringBuilder : EventDataList) {
            //新线程处理进数据库(resultStringBuilder,eventId,DeviceId)
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
                iotEvent.setD1090(Integer.parseInt(getEventIdData(),16));
                //中间9个为空
                //后面全为电流波形
                String currentWaveForm = resultStringBuilder.substring(80);
                iotEvent.setD1120to1365(currentWaveForm);
                log.info("封装后的数据为{}",iotEvent);

                IotEventMapper iotEventMapper = SpringUtils.getBean("iotEventMapper", IotEventMapper.class);
                iotEventMapper.insert(iotEvent);
                //将存入数据库的数据放入缓存
                EventIdMap.put(modbus.getDeviceId(),iotEvent.getD1100());
                //todo 如果下面捕获到异常(????)
            } catch (InstantiationException e) {//实例化不成功报错
                e.printStackTrace();
            } catch (IllegalAccessException e) {//没有setAccessible(true)会出现这个异常
                e.printStackTrace();
            }
        }
    }
}
