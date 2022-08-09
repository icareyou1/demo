package com.fentric.modbus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.mapper.IotWarmMapper;
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
        //key为设备id，所有在线设备
        Set<Long> keys = SocketMap.keySet();
        IotWarmMapper iotWarmMapper = SpringUtils.getBean("iotWarmMapper", IotWarmMapper.class);
        //先查询告警量
        byte[] codeByte = CodeUtils.hexStrToByteArr(CodeUtils.generateModbus(1, 3, 1020, 2));
        for (Long key : keys) {
            //如果socket突然掉线？ 服务器不知道
            Socket socket = SocketMap.get(key);
            try {
                String sqlData = IOUtils.getDataFromDevice(socket, codeByte);
                /*OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                outputStream.write(codeByte);
                byte[] bytes=new byte[1024];
                int len=0;
                socket.setSoTimeout(500);
                len=inputStream.read(bytes);
                //while ((len=inputStream.read(bytes))!=-1){
                String data = CodeUtils.byteArrToHexStr(bytes, len);
                String temp = data.replace(" ", "");
                //处理modbus多余字节 (003F000C)
                String sqlData = temp.substring(6,temp.length()-4);*/

                //如果读取失败，不用走这里也行
                String cacheData = WarmDataMap.get(key);
                log.info("缓存数据{}",cacheData);
                if (cacheData==null||"".equals(cacheData)){
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
                            WarmDataMap.put(key,queryData);
                            //再次读取缓存
                            cacheData=queryData;
                        }
                    }
                }
                //和缓存数据不同，放入数据库中,和缓存
                log.info("sqlData{}",sqlData);
                if (!sqlData.equals(cacheData)){
                    WarmDataMap.put(key,sqlData);
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
                }
                //break;
                //}
            } catch (IOException e) {
                System.out.println("读取超时...");
                e.printStackTrace();
            }


        }
    }
}
