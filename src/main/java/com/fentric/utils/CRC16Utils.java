package com.fentric.utils;

import java.util.Base64;

public class CRC16Utils {

    /**
     * 一个字节包含位的数量 8
     */
    private static final int BITS_OF_BYTE = 8;

    /**
     * 多项式
     */
    private static final int POLYNOMIAL = 0xA001;

    /**
     * 初始值
     */
    private static final int INITIAL_VALUE = 0xFFFF;

    /**
     * CRC16 编码
     * @param bytes 编码内容
     * @return 编码结果
     */
    public static String crc16(int[] bytes) {
        int res = INITIAL_VALUE;
        for (int data : bytes) {
            res = res ^ data;
            for (int i = 0; i < BITS_OF_BYTE; i++) {
                res = (res & 0x0001) == 1 ? (res >> 1) ^ POLYNOMIAL : res >> 1;
            }
        }
        return convertToHexString(revert(res));
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

    private static String convertToHexString(int src) {
        return Integer.toHexString(src);
    }
    //转化字符串为十六进制编码
   public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }
    // 转化十六进制编码为字符串
    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
//--------------------------------------
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
    //上方重载形式
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

        //
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



    public static void main(String[] args) {
        byte[] bytes1 = hexStrToByteArr("01 03 03 E7 00 64 F4 52");
        System.out.println(byteArrToHexStr("01 03 03 E7 00 64 F4 52".getBytes()));
        System.out.println(hexStrToString("10 77 F7 8D 9C 62 E9 FD EB 2A A8 B1 97 13 5A 1E 2C D2 CE 13 12 1C 68 5B 66 95 0F 14 AB 01 AF 4C FD 79 1F 4A FB 39 A4 2B 38 E8 01 BB 07 14 40 4B C2 49"));
    }
}
