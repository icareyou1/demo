package com.fentric.utils;

import java.util.Objects;

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
}
