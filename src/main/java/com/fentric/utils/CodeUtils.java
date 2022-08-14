package com.fentric.utils;

import com.fentric.domain.Modbus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
@Slf4j
public class CodeUtils {

    /**
     * 计算CRC16校验码
     *
     * @param data 需要校验的字符串
     * @return 校验码
     */
    public static String getCRCChcek(String data) {
        data = data.replace(" ", "");
        int len = data.length();
        if (!(len % 2 == 0)) {
            return "0000";
        }
        int num = len / 2;
        byte[] para = new byte[num];
        for (int i = 0; i < num; i++) {
            int value = Integer.valueOf(data.substring(i * 2, 2 * (i + 1)), 16);
            para[i] = (byte) value;
        }
        return getCRC(para);
    }

    /**
     * @param bytes 字节数组
     * @return {@link String} 校验码
     */
    public static String getCRC(byte[] bytes) {
        //CRC寄存器全为1
        int CRC = 0x0000ffff;
        //多项式校验值
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //结果转换为16进制
        String result = Integer.toHexString(CRC).toUpperCase();
        if (result.length() != 4) {
            StringBuffer sb = new StringBuffer("0000");
            result = sb.replace(4 - result.length(), 4, result).toString();
        }
        //交换高低位
        return result.substring(2, 4) + result.substring(0, 2);
    }
    //排除校验码,返回int值
    public static int getCRCInt(byte[] bytes){
        //CRC寄存器全为1
        int CRC = 0x0000ffff;
        //多项式校验值
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        //排除两个校验码
        for (i = 0; i < bytes.length-2; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return CRC;
    }

    /**
     * 翻转16位的高八位和低八位字节
     * @param src 翻转数字
     * @return 翻转结果
     */
    private static int revert(int src) {
        int lowByte = (src & 0xFF00) >> 8;
        int highByte = (src & 0x00FF) << 8;
        return lowByte | highByte;
    }

    /**
     * 字节数组 转 16进制
     * 格式: "hello".getBytes() --->  68 65 6C 6C 6F
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex.toUpperCase() + ' ');
        }
        return sb.toString();
    }

    /**
     * 字符串 转 16进制值
     * 格式: "hello"  ----> 68 65 6C 6C 6F
     */
    public static String stringToHexStr(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     *  16进制字符串 转 字符串
     *  格式:68 65 6C 6C 6F----> hello
     */
    public static String hexStrToString(String hexStr){
        return new String(hexStrToByteArr(hexStr));
    }

    /**
     * 二进制 转 16进制
     * 格式:  "hello".getBytes() ----> 68 65 6C 6C 6F
     */
    public static String byteArrToHexStr(byte[] b) {
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            //System.out.println(hex.toUpperCase());
            sb.append(hex.toUpperCase()+" ");
        }
        return sb.toString();
    }
    //上方重载形式,如果定义接受byte为1024就要指定获取长度
    public static String byteArrToHexStr(byte[] b,int len) {
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < len; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            //System.out.println(hex.toUpperCase());
            sb.append(hex.toUpperCase()+" ");
        }
        return sb.toString();
    }

    /**
     *   16进制字符串  转  二进制数组
     *  格式:  68 65 6C 6C 6F  ---->  new String(bytes)
     */
    public static byte[] hexStrToByteArr(String strIn) {
        byte[] arrB = strIn.replace(" ","").getBytes();
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * 16进制 转 二进制字符串
     * 格式: 68 65 6C 6C 6F --->  0110 1000 0110 0101 0110 1100 0110 1100 0110 1111
     */
    public static String hexStrTobinStr(String hexString) {
        hexString= hexString.replace(" ", "");
        if (hexString == null || hexString.length() % 2 != 0) {
            return null;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4)+" ";
        }
        return bString;
    }

    /**
     *   同网络助手保持同步
     * @param slaveId  从站id(默认为1)
     * @param funcId     查询功能id
     * @param registerId   直接和表对应
     * @param len     读取一个寄存器位,返回两个(一个寄存器16位)
     * @return
     */
    //不能批量写入
    public static String generateModbus(int... args){
        StringBuilder modbusWithNochecked = new StringBuilder();
        for (int i=0;i<args.length;i++){
            String hexStr = Integer.toHexString(args[i]);
            hexStr=hexStr.length() % 2 == 1 ? "0"+hexStr : hexStr;
            //寄存器和读取长度不足
            if (i>=2&&hexStr.length()<4) modbusWithNochecked.append("00");
            modbusWithNochecked.append(hexStr);
        }
        String crcChcek = getCRCChcek(modbusWithNochecked.toString());
        modbusWithNochecked.append(crcChcek);
        return modbusWithNochecked.toString().toUpperCase();
    }
    //从站号，16，寄存器地址，写入数据个数,写入数据长度,写入数据,校验码
    public static String generateModbusByBatchWrite(int slaveId,int registerId,int[] data){
        StringBuilder modbusWithNochecked = new StringBuilder();
        //从站号
        String slaveIdHex = Integer.toHexString(slaveId);
        modbusWithNochecked.append(slaveIdHex.length()==1?"0"+slaveIdHex:slaveIdHex);
        //16的功能号
        modbusWithNochecked.append("10");
        String hexStr = Integer.toHexString(registerId);
        //次数少直接拼接没关系
        while (hexStr.length()<4){
            hexStr="0"+hexStr;
        }
        modbusWithNochecked.append(hexStr);
        //数据长度1
        int length = data.length;
        int hexStrlen = Integer.toHexString(length).length();
        for (int i=0;i<4-hexStrlen;i++){
            modbusWithNochecked.append("0");
        }
        modbusWithNochecked.append(Integer.toHexString(length));
        //处理数据长度2
        String temp = Integer.toHexString(length * 2);
        temp=temp.length()==1?"0"+temp:temp;
        modbusWithNochecked.append(temp);
        //处理数据部分
        for (int i=0;i<length;i++){
            String hexData = Integer.toHexString(data[i]);
            hexData=hexData.length()%2==1?"0"+hexData:hexData;
            if (hexData.length()!=4) modbusWithNochecked.append("00");
            modbusWithNochecked.append(hexData);
        }
        String crcChcek = getCRCChcek(modbusWithNochecked.toString());
        modbusWithNochecked.append(crcChcek);
        return modbusWithNochecked.toString().toUpperCase();
    }
    //获取报文荷载信息(针对读保持寄存器有意义)
    public static String parseHoldingRegistersMessage(String data){
        log.info("待解析的原始16进制字符串报文（最后多一空格）:{}",data);
        String temp = data.replace(" ", "");
        //不是查询指令就返回null
        if (!"03".equals(temp.substring(2,4))) return null;
        return temp.substring(6,temp.length()-4);
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(9999);
        Socket accept = serverSocket.accept();
        System.out.println("新设备接入:"+accept.getInetAddress().getHostAddress()+":"+accept.getPort());
        InputStream inputStream = accept.getInputStream();
        byte[] buf=new byte[1024];
        int len=0;
        len = inputStream.read(buf);
        System.out.println("设备注册mac："+byteArrToHexStr(buf,len));
        Modbus modbus = new Modbus();
        modbus.setSocket(accept);
        modbus.setSlaveId(1);
        //modbus.setFunctionId(3);
        //modbus.setAddress(1030);
        //modbus.setQueryLen(1);
        //写单个寄存器

        //modbus.setAddress(2000);
        //modbus.setWriteSingleValue(855);


        long startTime=System.currentTimeMillis();
        long currentTime;
        while (true){
            /*Thread.sleep(10001);
            //应该要大于socket超时时间；如果取值相等socket超时，这里必须马上发送指令
            //查询100个
            currentTime=System.currentTimeMillis();
            if (currentTime-startTime>10000){
                modbus.setFunctionId(3);
                modbus.setAddress(1100);
                modbus.setQueryLen(100);
                modbus=IOUtils.readHoldingRegisters(modbus);
                System.out.println("完整的modbus："+modbus);
                startTime=currentTime;
            }*/
            //Thread.sleep(10001);
            //写多个
            currentTime=System.currentTimeMillis();
            //todo  明天解决批量写入问题
            if (currentTime-startTime>1000){
                int[] ints=new int[8];
                for (int i=0;i<8;i++){
                    ints[i]=1;
                    if (i==1) ints[i]=2;
                }
                modbus.setFunctionId(16);
                modbus.setWriteMultiValues(ints);
                modbus=IOUtils.writeMultiRegister(modbus);
                System.out.println("完整的modbus："+modbus);
                startTime=currentTime;
            }
            /*Thread.sleep(10001);
            //写单个
            currentTime=System.currentTimeMillis();
            if (currentTime-startTime>10000){
                int i = new Random().nextInt(65536);
                modbus.setFunctionId(6);
                modbus.setWriteSingleValue(i);
                modbus = IOUtils.writeSingleRegister(modbus);
                System.out.println("完整的modbus："+modbus);
                startTime=currentTime;
            }*/

        }
    }
}
