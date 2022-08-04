package com.fentric.modbus;

import java.io.*;
import java.net.Socket;

import static com.fentric.config.ServerSocketConfig.messageQueue;
import static com.fentric.config.ServerSocketConfig.threadPool;

public class ServerReceiveThread implements Runnable{
    private Socket socket;
    BufferedReader reader;
    BufferedWriter writer;

    public ServerReceiveThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //输入数据
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //输出数据
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true){
                String data = reader.readLine();
                System.out.println("------"+data);
                if ("close".equals(data)){
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
                    messageQueue.put(data);
                    writer.write("close");
                    writer.newLine();
                    writer.flush();
                }
                else {
                    writer.write("接受数据成功"+data);
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("write...");
                writer.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
            //-------------
            try {
                System.out.println("reader...");
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //-------------
            try {
                System.out.println("sout...");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
