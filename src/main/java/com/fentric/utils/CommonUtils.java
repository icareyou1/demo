package com.fentric.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    //字符串是否为null或空串
    public static boolean isNullOrEmptyStr(Object obj){
        return obj==null||"".equals(obj);
    }
    //判断id不为空,且大于0  (只校验Long和Integer类型)
    public static boolean isValidateId(Object obj){
        if (obj==null){
            return false;
        }
        if (obj instanceof Long){
            return (Long)obj>0;
        }
        if (obj instanceof Integer){
            return (Integer)obj>0;
        }
        return false;
    }
    //判断status是否合法
    public static boolean isValidateStatus(Object obj){
        return Objects.equals(obj,"0")||Objects.equals(obj,"1");
    }

    //判断传入的所有对象都为null
    public static boolean everyoneIsNull(Object... obj){
        for (Object o : obj) {
            if (o!=null){
                return false;
            }
        }
        return true;
    }
    //验证手机号
    public static boolean checkPhone(String phone){
        //以1开头,中间两个可匹配 最后八个数字
        Pattern p = Pattern.compile("^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$");
        if(p.matcher(phone).matches()){
            return true;
        }
        return false;
    }
    //验证邮箱
    public static boolean checkEmail(String email) {
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(email);
        if (m.matches()){
            return true;
        }
        return false;
    }
    //验证ip地址合法性
    public static boolean checkIp(String ip){
        String regEx1 = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(ip);
        if (m.matches()){
            return true;
        }
        return false;
    }
}
