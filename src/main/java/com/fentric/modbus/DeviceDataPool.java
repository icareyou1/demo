package com.fentric.modbus;

import com.fentric.domain.DeviceStatus;
import com.fentric.domain.Modbus;
import com.fentric.domain.UserCode;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DeviceDataPool {
    //done 设备socket连接池,设备id
    //public static Map<Long, Socket> SocketMap=new HashMap<>();
    public static Map<Long, DeviceStatus> DeviceStatusMap=new HashMap<>();
    //用户指令队列
    public static LinkedBlockingQueue<UserCode> UserCodeMQ = new LinkedBlockingQueue<>();
    //用户指令采集数据结果Map
    public static Map<Long,UserCode> UserCodeDatamap=new HashMap<>();
    //todo 定时采集数据队列（数据库） 线程封装数据库
    public static LinkedBlockingQueue<Modbus> DeviceDataMQ = new LinkedBlockingQueue<>();
    //阻塞队列保存,没有线程执行的任务
    public static final ThreadPoolExecutor ThreadPool=
            //15，20，100
            new ThreadPoolExecutor(15,20,100, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());
    public static final int DEVICEONLINE=1;
    public static final int DEVICEOFFLINE=2;
    public static final int GATEWAYOFFLINE=3;
    public static final int SERVEROFFLINE=4;


    /**
     * add(e):当队列已满时，再添加元素会抛出异常IllegalStateException
     * offer(e):添加成功，返回true，否则返回false
     * put:(e):当队列已满时，再添加元素会使线程变为阻塞状态
     * offer(e, time,unit):当队列已满时，在末尾添加数据，如果在指定时间内没有添加成功，返回false，反之是true
     * 删除元素
     *
     * remove(e):返回true表示已成功删除，否则返回false
     * poll():如果队列为空返回null，否则返回队列中的第一个元素
     * take():获取队列中的第一个元素，如果队列为空，获取元素的线程变为阻塞状态
     * poll(time, unit)：当队列为空时，线程被阻塞，如果超过指定时间，线程退出
     * 检查元素
     *
     * element():获取队头元素，如果元素为null，抛出NoSuchElementException
     * peek():获取队头元素，如果队列为空返回null，否则返回目标元素
     * ArrayBlockingQueue
     */
}
