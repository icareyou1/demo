package com.fentric.utils;

import java.lang.reflect.Field;

public class SetObjComboFields {
    /**
     *
     * @param clazz      封装类
     * @param paraValues  参数
     * @param startField  起始字段
     * @param endField    结束字段
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T>T setObj(Class<T> clazz,Integer[] paraValues,String startField,String endField) throws InstantiationException, IllegalAccessException {
        Object obj = clazz.newInstance();
        int index=0;
        //开始封装标记
        boolean flag=false;
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            //字段是按顺序遍历过来的
            if (fields[i].getName().contains(startField)) flag=true;
            if (!flag) continue;
            fields[i].setAccessible(true);
            fields[i].set(obj,paraValues[index++]);
            //结束字段封装完后跳出封装
            if (fields[i].getName().contains(endField)) break;
        }

        return (T)obj;
    }
}
