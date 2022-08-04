package com.fentric.modbus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

//测试用例
public class ClientSendThread implements Runnable{
    private String host;

    private int port;

    public ClientSendThread(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(host,port);
            System.out.println("客户端连接成功...");
            //输出流写数据
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //输入流读数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true){
                Thread.sleep(3000);
                System.out.println("发送指令:");
                writer.write("modbus,01 03 03 E7 00 64 F4 52");
                writer.newLine();
                writer.flush();
                String data = reader.readLine();
                System.out.println("接收到服务端响应:"+data);
                if("close".equals(data)){
                    break;
                }
            }
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new ClientSendThread("192.168.12.201",9999)).start();
    }
}
