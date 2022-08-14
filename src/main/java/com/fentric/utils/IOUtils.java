package com.fentric.utils;

import com.fentric.domain.Modbus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
public class IOUtils {
    /**
     * 读取
     * @param socket
     * @param codeByte
     * @return
     * @throws IOException
     */
    public static String getDataFromDevice(Socket socket,byte[] codeByte) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        outputStream.write(codeByte);
        byte[] bytes=new byte[1024];
        int len=0;
        socket.setSoTimeout(2000);
        len=inputStream.read(bytes);
        //while ((len=inputStream.read(bytes))!=-1){
        String data = CodeUtils.byteArrToHexStr(bytes, len);
        String temp = data.replace(" ", "");
        log.info("IOUtils中反馈{}",temp);
        //处理03读指令多余字节 (003F000C)
        if ("03".equals(temp.substring(2,4))) return temp.substring(6,temp.length()-4);
        //返回其他指令
        return temp.substring(6,temp.length()-4);
    }
    //todo 查询网关在线状态
    public static boolean queryGateWayStatus(Socket socket){
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("hello".getBytes());
            int len=0;
            byte[] bytes=new byte[1024];
            socket.setSoTimeout(3000);
            while ((len=inputStream.read(bytes))!=-1){
                String s = new String(bytes, 0, len);
                if (s=="ok") return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //接受设备注册码
    public static String receiveRegisterCode(Socket socket) throws IOException {
        //应该收到的注册码长度
        int shouldReceiveCodeLength=6;
        InputStream inputStream = socket.getInputStream();
        socket.setSoTimeout(3000);
        //真实接受的数据
        byte[] receiveBytes=new byte[shouldReceiveCodeLength];
        //缓冲池
        byte[] buf=new byte[shouldReceiveCodeLength];
        int count=0;
        //receiveBytes索引
        int index=0;
        while ((count=inputStream.read(buf))!=-1){
            //缓冲池一次装满了
            if (buf[shouldReceiveCodeLength-1]!=0){
                log.info("接受设备注册码...");
                return CodeUtils.byteArrToHexStr(buf);
            }
            //如果没有装满，就是有多帧
            for (int i=0;i<count;i++){
                receiveBytes[index+i]=buf[i];
            }
            index+=count;
            log.info("本次buf大小:{}",count);
            if (index==shouldReceiveCodeLength) break;
        }
        return new String(receiveBytes);
    }

    /**
     *  1.发送01 03 04 06 00 01 65 3B    16进制字符串指令
     *  2.返回01 03 （00 01）*2 （2字节） 校验2字节
     *  3.循环读取，如果长度相等，代表本次读取成功
     *  4.如果校验失败，就重新发送指令读取，重复3步骤
     *  4.3秒内读取不成功就失败
     *
     * @param socket
     * @param slaveId
     * @param address
     * @param len
     * @return  返回原始帧   如果返回空则通信失败
     */
    public static Modbus readHoldingRegisters(Modbus modbus) throws IOException {
        //本次通信为了稳定性尝试重发次数
        int sendCount=3;
        //本次指令应收到的字节长度
        int shouldReceiveResultByteLength=1+1+1+2*modbus.getQueryLen()+2;
        log.info("读保持寄存器应接受字节:{}",shouldReceiveResultByteLength);
        //生成指令
        String modbushexStr = CodeUtils.generateModbus(modbus.getSlaveId(), modbus.getFunctionId(), modbus.getAddress(), modbus.getQueryLen());
        byte[] codeByte = CodeUtils.hexStrToByteArr(modbushexStr);

        //modbus帧长不可能超过规定的，就算两帧同时到达，串口转以太网接口也会切开帧，（也就是说打包数据可能会把帧切开，不可能把帧合并）
        byte[] buf=new byte[shouldReceiveResultByteLength]; //此为缓存
        //真实接受字节
        byte[] receiveBytes=new byte[shouldReceiveResultByteLength];
        Socket socket=modbus.getSocket();
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        //错误信息
        StringBuilder errorMsg = new StringBuilder();
        while (sendCount>0){
            try {
                //准备接受指令
                int count=0;
                //receiveBytes索引
                int index=0;
                //发送指令
                outputStream.write(codeByte); //
                //超时时间
                socket.setSoTimeout(3000);  //
                //需要建立缓存buf，以防多帧情况
                while ((count=inputStream.read(buf))!=-1){   //
                    for (int i=0;i<count;i++){
                        receiveBytes[index+i]=buf[i];
                    }
                    index+=count;
                    log.info("本次buf大小:{}",count);
                    /*
                    //如果有校验位说明，数据传完(不能通过这样来判断)
                    if (receiveBytes[shouldReceiveResultByteLength-2]!=0&&receiveBytes[shouldReceiveResultByteLength-1]!=0) break;
                     */
                    //达到数量就成功
                    if (index==shouldReceiveResultByteLength) break;
                    //如果读取超时，重发(能否再次read)
                    //根据传输来的二进制数据校验？   String getCRC(byte[] bytes)
                }
                //校验得出来的值(里面排除了两个校验位,并且没有做高低位反转)
                int calResult = CodeUtils.getCRCInt(receiveBytes);
                log.info("计算得出的校验码值：{}",calResult);
                //设备传输过来的校验值(交换高低位)
                int realCheckResult=((0x000000ff & receiveBytes[shouldReceiveResultByteLength-1])<<8)+(0x000000ff&receiveBytes[shouldReceiveResultByteLength-2]);
                log.info("报文信息中的校验码值：{}",realCheckResult);
                //校验帧错误
                if (calResult!=realCheckResult){
                    log.info("第{}次出错原因：校验帧错误",(4-sendCount));
                    errorMsg.append("第"+(4-sendCount)+"次出错原因:"+"校验帧错误");
                    sendCount--;
                    //下一轮
                    continue;
                }
                //正常收到数据帧
                String data = CodeUtils.byteArrToHexStr(receiveBytes);
                log.info("设备反馈的16进制数据{}",data);
                //返回处理后的数据
                modbus.setData(CodeUtils.parseHoldingRegistersMessage(data));
                return modbus;
            } catch (IOException e) {
                errorMsg.append("第"+(4-sendCount)+"次出错原因:"+"反馈超时");
                log.info("第{}次出错原因：反馈超时",(4-sendCount));
                sendCount--;
                e.printStackTrace();
            }
        }
        log.info("错误信息为:{}",errorMsg.toString());
        modbus.setError(errorMsg.toString());
        //返回错误
        return modbus;
    }

    /**
     *  1.发送01 03 04 06 00 01 65 3B    16进制字符串指令
     *  2.返回01 03 （00 01）*2 （2字节） 校验2字节
     *  3.循环读取，如果长度相等，代表本次读取成功
     *  4.如果校验失败，就重新发送指令读取，重复3步骤
     *  4.3秒内读取不成功就失败
     *
     * @param socket
     * @param slaveId
     * @param address
     * @param len
     * @return  返回原始帧   如果返回空则通信失败
     */
    public static Modbus writeSingleRegister(Modbus modbus) throws IOException {
        //本次通信为了稳定性尝试重发次数
        int sendCount=3;
        //本次指令应收到的字节长度
        int shouldReceiveResultByteLength=8;
        log.info("写单个寄存器指令应接受字节:{}",shouldReceiveResultByteLength);
        //生成指令
        String modbushexStr = CodeUtils.generateModbus(modbus.getSlaveId(), modbus.getFunctionId(), modbus.getAddress(), modbus.getWriteSingleValue());
        byte[] codeByte = CodeUtils.hexStrToByteArr(modbushexStr);

        //modbus帧长不可能超过规定的，就算两帧同时到达，串口转以太网接口也会切开帧，（也就是说打包数据可能会把帧切开，不可能把帧合并）
        byte[] buf=new byte[shouldReceiveResultByteLength]; //此为缓存
        //真实接受的字节
        byte[] receiveBytes=new byte[shouldReceiveResultByteLength];
        Socket socket=modbus.getSocket();
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        //错误信息
        StringBuilder errorMsg = new StringBuilder();
        while (sendCount>0){
            try {
                //准备接受指令
                int count=0;
                //receiveBytes索引
                int index=0;
                //发送指令（此为设置指令和读取指令同样为8字节）
                outputStream.write(codeByte); //
                //超时时间
                socket.setSoTimeout(3000);  //
                //需要建立缓存buf，以防多帧情况
                while ((count=inputStream.read(buf))!=-1){   //
                    for (int i=0;i<count;i++){
                        receiveBytes[index+i]=buf[i];
                    }
                    index+=count;
                    log.info("本次buf大小:{}",count);
                    /*
                    //如果有校验位说明，数据传完(不能通过这样来判断)
                    if (receiveBytes[shouldReceiveResultByteLength-2]!=0&&receiveBytes[shouldReceiveResultByteLength-1]!=0) break;
                     */
                    //达到数量就成功
                    if (index==shouldReceiveResultByteLength) break;
                    //如果读取超时，重发(能否再次read)
                    //根据传输来的二进制数据校验？   String getCRC(byte[] bytes)
                }
                //校验得出来的值(里面排除了两个校验位,并且没有做高低位反转)
                int calResult = CodeUtils.getCRCInt(receiveBytes);
                log.info("计算得出的校验码值：{}",calResult);
                //设备传输过来的校验值(交换高低位)
                int realCheckResult=((0x000000ff & receiveBytes[shouldReceiveResultByteLength-1])<<8)+(0x000000ff&receiveBytes[shouldReceiveResultByteLength-2]);
                log.info("报文信息中的校验码值：{}",realCheckResult);
                //校验帧错误
                if (calResult!=realCheckResult){
                    log.info("第{}次出错原因：校验帧错误",(4-sendCount));
                    errorMsg.append("第"+(4-sendCount)+"次出错原因:"+"校验帧错误");
                    sendCount--;
                    //下一轮
                    continue;
                }
                //正常收到数据帧
                String data = CodeUtils.byteArrToHexStr(receiveBytes);
                log.info("设备反馈的16进制数据{}",data);
                modbus.setData(data);
                return modbus;
            } catch (IOException e) {
                errorMsg.append("第"+(4-sendCount)+"次出错原因:"+"读取超时");
                log.info("第{}次出错原因：读取超时",(4-sendCount));
                sendCount--;
                e.printStackTrace();
            }
        }
        log.info("错误信息为:{}",errorMsg.toString());
        modbus.setError(errorMsg.toString());
        //返回错误
        return modbus;
    }

    /**
     *  写入多个数据帧 01 10 07 D0 00 02 04 00 01 00 02 08 C2
     *  返回帧   01 10 07 D0 00 02 (校验码2个)
     *  注意：目前设备一次最多只能写8个
     * @param socket
     * @param slaveId
     * @param address
     * @param len
     * @return  返回原始帧   如果返回空则通信失败
     */
    public static Modbus writeMultiRegister(Modbus modbus) throws IOException {
        //本次通信为了稳定性尝试重发次数
        int sendCount=3;
        //本次指令应收到的字节长度
        int shouldReceiveResultByteLength=8;
        log.info("写多个寄存器指令应接受字节:{}",shouldReceiveResultByteLength);
        //生成指令
        String modbushexStr = CodeUtils.generateModbusByBatchWrite(modbus.getSlaveId(), modbus.getAddress(), modbus.getWriteMultiValues());
        //String modbushexStr = CodeUtils.generateModbus(modbus.getSlaveId(), modbus.getFunctionId(), modbus.getAddress(), modbus.getWriteValue());
        byte[] codeByte = CodeUtils.hexStrToByteArr(modbushexStr);

        //modbus帧长不可能超过规定的，就算两帧同时到达，串口转以太网接口也会切开帧，（也就是说打包数据可能会把帧切开，不可能把帧合并）
        byte[] buf=new byte[shouldReceiveResultByteLength]; //此为缓存
        //真实接受的字节
        byte[] receiveBytes=new byte[shouldReceiveResultByteLength];
        Socket socket=modbus.getSocket();
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        //错误信息
        StringBuilder errorMsg = new StringBuilder();
        while (sendCount>0){
            try {
                //准备接受指令
                int count=0;
                //receiveBytes索引
                int index=0;
                //发送指令（此为设置指令和读取指令同样为8字节）
                outputStream.write(codeByte); //
                //超时时间
                socket.setSoTimeout(5000);  //
                //需要建立缓存buf，以防多帧情况
                while ((count=inputStream.read(buf))!=-1){   //
                    for (int i=0;i<count;i++){
                        receiveBytes[index+i]=buf[i];
                    }
                    index+=count;
                    log.info("本次buf大小:{}",count);
                    /*
                    //如果有校验位说明，数据传完(不能通过这样来判断)
                    if (receiveBytes[shouldReceiveResultByteLength-2]!=0&&receiveBytes[shouldReceiveResultByteLength-1]!=0) break;
                     */
                    //达到数量就成功
                    if (index==shouldReceiveResultByteLength) break;
                    //如果读取超时，重发(能否再次read)
                    //根据传输来的二进制数据校验？   String getCRC(byte[] bytes)
                }
                //校验得出来的值(里面排除了两个校验位,并且没有做高低位反转)
                int calResult = CodeUtils.getCRCInt(receiveBytes);
                log.info("计算得出的校验码值：{}",calResult);
                //设备传输过来的校验值(交换高低位)
                int realCheckResult=((0x000000ff & receiveBytes[shouldReceiveResultByteLength-1])<<8)+(0x000000ff&receiveBytes[shouldReceiveResultByteLength-2]);
                log.info("报文信息中的校验码值：{}",realCheckResult);
                //校验帧错误
                if (calResult!=realCheckResult){
                    log.info("第{}次出错原因：校验帧错误",(4-sendCount));
                    errorMsg.append("第"+(4-sendCount)+"次出错原因:"+"校验帧错误");
                    sendCount--;
                    //下一轮
                    continue;
                }
                //正常收到数据帧
                String data = CodeUtils.byteArrToHexStr(receiveBytes);
                log.info("设备反馈的16进制数据{}",data);
                modbus.setData(data);
                return modbus;
            } catch (IOException e) {
                errorMsg.append("第"+(4-sendCount)+"次出错原因:"+"读取超时");
                log.info("第{}次出错原因：读取超时",(4-sendCount));
                sendCount--;
                e.printStackTrace();
            }
        }
        log.info("错误信息为:{}",errorMsg.toString());
        modbus.setError(errorMsg.toString());
        //返回错误
        return modbus;
    }


    //读取两次
    public static String setDeviceValue(Socket socket,byte[] codeByte) throws IOException{
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        outputStream.write(codeByte);
        byte[] bytes=new byte[1024];
        int len=0;
        int count=0;
        socket.setSoTimeout(2000);
        StringBuilder stringBuilder=new StringBuilder();
        while (count<2){
            len=inputStream.read(bytes);
            String data = CodeUtils.byteArrToHexStr(bytes, len);
            String temp = data.replace(" ", "");
            log.info("temp临时数据{}",temp);
            stringBuilder.append(temp);
            count++;
        }
        log.info("IOUtils中setDeviceValue反馈数据{}",stringBuilder.toString());
        //处理03读指令多余字节 (003F000C)
        //if ("03".equals(temp.substring(2,4))) return temp.substring(6,temp.length()-4);
        //返回原指令
        return stringBuilder.toString();
    }

}
