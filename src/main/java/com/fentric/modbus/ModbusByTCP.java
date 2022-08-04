package com.fentric.modbus;

import com.fentric.utils.CRC16Utils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static com.fentric.config.ServerSocketConfig.messageQueue;


public class ModbusByTCP implements Runnable{
    private Socket socket;
    InputStream input;
    OutputStream outputStream;


    public ModbusByTCP(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //输入数据
            //reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            input = socket.getInputStream();
            //输出数据
            //writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            outputStream = socket.getOutputStream();
            while (true){
                long start = System.currentTimeMillis();
                String take = messageQueue.take();
                System.out.println("指令为take;"+take);
                //给modbus设备查询指令
                //01 06 08 34 00 01 0B A4   重启帧,原样返回
                byte[] bytes1 = CRC16Utils.hexStrToByteArr("01 03 03 E7 00 64 F4 52");
                outputStream.write(bytes1);
                //读取指令
                System.out.println("start...");
                byte[] bytes=new byte[1024];
                int len=0;
                //spd未上电会阻塞
                socket.setSoTimeout(10);
                while ((len=input.read(bytes))!=-1){

                    System.out.println(CRC16Utils.byteArrToHexStr(bytes,len));
                    Thread.sleep(3000);
                    break;
                }

                System.out.println("休眠一会儿...");

                /*if ("close".equals(data)){
                    writer.write("close");
                    writer.newLine();
                    writer.flush();
                    break;
                }else if ("query".equals(data)){
                    System.out.println("当前threadpool:"+threadPool.getActiveCount());

                    //处理modbus指令
                }else if (data!=null&&data.startsWith("modbus")){
                    String[] split = data.split(",");
                    System.out.println("接受的modbus指令:"+split[1]);
                }
                else {
                    writer.write("接受数据成功"+data);
                    writer.newLine();
                    writer.flush();
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    public void socketServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Socket accept = serverSocket.accept();
        InetAddress inetAddress = accept.getInetAddress();
        System.out.println("连接的客户端ip为:"+inetAddress.getHostAddress());
        //成功获取消息,会自动断开吗?心跳包
        OutputStream outputStream = accept.getOutputStream();
        byte[] bytes1 = CRC16Utils.hexStrToByteArr("01 03 03 E7 00 64 F4 52");
        outputStream.write(bytes1);
        InputStream inputStream = accept.getInputStream();
        byte[] bytes=new byte[1024];
        int len=0;
        while ((len=inputStream.read(bytes))!=-1){
            //System.out.println(new String(bytes,0,len));
            System.out.println(CRC16Utils.byteArrToHexStr(bytes,len));

        }
        accept.close();
        serverSocket.close();
    }
}
