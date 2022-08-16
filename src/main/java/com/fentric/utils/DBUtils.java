package com.fentric.utils;

import com.fentric.domain.Modbus;
import com.fentric.mapper.IotRunMapper;
import com.fentric.mapper.IotWarmMapper;
import com.fentric.pojo.IotRun;
import com.fentric.pojo.IotWarm;
import com.fentric.service.IotRunService;

public class DBUtils {
    /*//将告警模块modbus结果存入表中
    public static void insertWarmModbus(Modbus modbus){
        IotWarmMapper iotWarmMapper = SpringUtils.getBean("iotWarmMapper", IotWarmMapper.class);
        //放入数据库
        IotWarm iotWarm = new IotWarm();
        iotWarm.setDeviceId(modbus.getDeviceId());
        iotWarm.setd1020to1021(modbus.getData());
        iotWarmMapper = SpringUtils.getBean("iotWarmMapper", IotWarmMapper.class);
        iotWarmMapper.insert(iotWarm);
    }*/

    //将运行模块modbus结果存入表中
    public static void insertRunModbus(Modbus modbus) throws InstantiationException, IllegalAccessException {
        //切割出1022和1030-1079两个部分的数据
        String data = modbus.getData();
        String d1022=data.substring(0,4);
        String d1030_1079 = data.substring(32);
        Integer[] ints_d1030_1079= new Integer[d1030_1079.length() / 4];
        for (int i=0;i<d1030_1079.length()/4;i++){
            //因为这里的数据都比较小
            ints_d1030_1079[i]=Integer.parseInt(d1030_1079.substring(i*4, i*4 + 4),16);
        }
        //todo 将数据放入，数据库处理队列（预先处理数据放入对应库中）
        IotRunMapper iotRunMapper = SpringUtils.getBean("iotRunMapper", IotRunMapper.class);
        //done 如何遍历封装？
        IotRun iotRun = SetObjComboFields.setObj(IotRun.class, ints_d1030_1079, "d1030", "d1079");
        //设置设备id
        iotRun.setDeviceId(modbus.getDeviceId());
        iotRun.setd1022(d1022);
        //写入数据库
        iotRunMapper.insert(iotRun);
    }
}
