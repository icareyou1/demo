package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.mapper.IotEventMapper;
import com.fentric.mapper.IotWarmMapper;
import com.fentric.pojo.IotEvent;
import com.fentric.pojo.IotWarm;
import com.fentric.utils.CodeUtils;
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

import static com.fentric.modbus.DeviceDataPool.SocketMap;
@Slf4j
public class ModuleEventDetection implements Runnable{
    //记录编号缓存(0-65535)
    public static Map<Long,Short> RecordIdMap=new HashMap<>();
    @Override
    public void run() {
        //key为设备id，所有在线设备
        Set<Long> keys = SocketMap.keySet();
        IotEventMapper iotEventMapper = SpringUtils.getBean("iotEventMapper", IotEventMapper.class);
        //先查询当前记录编号
        byte[] codeByte = CodeUtils.hexStrToByteArr(CodeUtils.generateModbus(1, 3, 1091, 1));
        for (Long key : keys) {
            //如果socket掉线服务器不知道，只有读不到的数据知道
            Socket socket = SocketMap.get(key);
            try {
                /*OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                outputStream.write(codeByte);
                byte[] bytes=new byte[1024];
                int len=0;
                socket.setSoTimeout(500);
                //while ((len=inputStream.read(bytes))!=-1){
                String data = CodeUtils.byteArrToHexStr(bytes, len);
                String temp = data.replace(" ", "");
                //直接将记录编号16进制(如00CF)转换成编号
                Short recordId = Short.parseShort(temp.substring(6,temp.length()-4),16);*/
                Short recordId = Short.parseShort(IOUtils.getDataFromDevice(socket, codeByte),16);
                //获取缓存中记录编号
                Short cacheRecordId = RecordIdMap.get(key);
                //如果缓存获取不到，就从数据库中获取最新的
                if (cacheRecordId==null){
                    //查询数据库(查询该设备最新),将数据库数据放入缓存
                    LambdaQueryWrapper<IotEvent> wrapper = new LambdaQueryWrapper<>();
                    wrapper.select(IotEvent::getd1091);
                    wrapper.eq(IotEvent::getd1091,key);
                    wrapper.orderByDesc(IotEvent::getCreateTime).last("limit 1");
                    IotEvent iotEvent = iotEventMapper.selectOne(wrapper);
                    if (iotEvent!=null){
                        Short queryRecordId = iotEvent.getd1091();
                        //数据库有数据就放入缓存中
                        if (queryRecordId!=null){
                            RecordIdMap.put(key,queryRecordId);
                            //再次读取缓存
                            cacheRecordId=queryRecordId;
                        }
                    }
                }
                //如果缓存中的数据仍然为空
                if (cacheRecordId==null){
                    log.info("经过缓存的数据仍然为空{}",cacheRecordId);
                    //如果没有记录，那么代表设备刚刚激活，默认只查询一条
                    //1.首先设置设备记录编号(2081)为recorId
                    String set2081HexStr = CodeUtils.generateModbus(1, 6, 2081, recordId);
                    byte[] set2081 = CodeUtils.hexStrToByteArr(set2081HexStr);
                    //会返回同指令一样的数据
                    String result = IOUtils.getDataFromDevice(socket, set2081);
                    if (result!=null&&set2081HexStr.equals(result)) System.out.println("设置成功");
                    //2.查询1100至1365,分三次查询

                }else {//如果能从缓存中取到数据
                    int RecordCount=recordId-cacheRecordId;
                    //如果recordId>cacheRecordId
                    if (RecordCount>0){
                        if (RecordCount>16){//查询16条数据

                        }else {//查询subNum条数据

                        }
                    }
                    //如果recordId<cacaheRecordId
                    if (RecordCount<0){
                        if ((RecordCount+65536)>16){//查询16条数据

                        }else {//查询  RecordCount+65536条数据

                        }
                    }
                    //如果相等什么都不做
                }
                /*if (cacheData==null||"".equals(cacheData)){
                    //查询数据库(查询该设备最新),将数据库数据放入缓存
                    LambdaQueryWrapper<IotWarm> wrapper = new LambdaQueryWrapper<>();
                    wrapper.select(IotWarm::getd1020to1021);
                    wrapper.eq(IotWarm::getWarmId,key);
                    //根据创建时间降序
                    wrapper.orderByDesc(IotWarm::getCreateTime).last("limit 1");
                    IotWarm iotWarm = iotWarmMapper.selectOne(wrapper);
                    //System.out.println("查询到的数据为："+iotWarm);
                    if (iotWarm!=null){
                        String queryData = iotWarm.getd1020to1021();
                        if (queryData!=null){
                            warmDataMap.put(key,queryData);
                            //再次读取缓存
                            cacheData=warmDataMap.get(key);
                        }
                    }
                }
                //和缓存数据不同，放入数据库中,和缓存
                log.info("sqlData{}",sqlData);
                if (!sqlData.equals(cacheData)){
                    warmDataMap.put(key,sqlData);
                    //放入数据库
                    IotWarm iotWarm = new IotWarm();
                    iotWarm.setDeviceId(key);
                    iotWarm.setd1020to1021(sqlData);
                    iotWarmMapper = SpringUtils.getBean("iotWarmMapper", IotWarmMapper.class);
                    iotWarmMapper.insert(iotWarm);
                }

                //解析数据测试
                String[] strs= new String[sqlData.length() / 4];
                for (int i=0;i<sqlData.length()/4;i++){
                    //因为这里的数据都比较小，直接转换即可
                    strs[i] = CodeUtils.hexStrTobinStr(sqlData.substring(i * 4, i * 4 + 4));
                }
                System.out.println("warm模块获取数据:");
                for (String anStr : strs) {
                    System.out.println(anStr);
                }*/

                //    break;
                //}
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
