package com.fentric.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IOUtils {
    public static String getDataFromDevice(Socket socket,byte[] codeByte) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
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
        return temp.substring(6,temp.length()-4);
    }
}
