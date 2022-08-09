package com.fentric.modbus;

import com.fentric.service.DeviceService;
import com.fentric.utils.CodeUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;




public class ModbusByTCP implements Runnable{
    private Socket socket;
    InputStream input;
    OutputStream outputStream;


    public ModbusByTCP(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        sendMsg();
    }

    public void sendMsg(){
        try {
            //输入数据
            input = socket.getInputStream();
            //输出数据
            outputStream = socket.getOutputStream();
            while (true){
                //这里慢？
                //String take = messageQueue.take();
                //System.out.println("指令为take;"+take);
                //给modbus设备查询指令
                //01 06 08 34 00 01 0B A4   重启帧,原样返回
                byte[] bytes1 = CodeUtils.hexStrToByteArr(CodeUtils.generateModbus(1,3,1000,100));
                outputStream.write(bytes1);
                //读取指令
                System.out.println("start...");
                byte[] bytes=new byte[1024];
                int len=0;
                //spd未上电会阻塞
                socket.setSoTimeout(500);
                //没有获取到就会阻塞
                while ((len=input.read(bytes))!=-1){
                    String data = CodeUtils.byteArrToHexStr(bytes, len);
                    System.out.println(data);
                    //如果不是注册码，就直接break
                    if (!("F4 70 0C 73 3D 1B ".equals(data))) break;
                }
                System.out.println("休眠一会儿...");
                Thread.sleep(5000);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("outputStream...");
                outputStream.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
            //-------------
            try {
                System.out.println("input...");
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //-------------
            try {
                System.out.println("socket...");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
