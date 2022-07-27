package com.fentric.constant;

public class ContantToken {
    //JwtUtils类中密钥
    public static final String SECRET="92$@rs#fa3";
    //令牌时间(分钟)   在JwtUtils类中
    public static final int EXPIRETIME=43200;  //定义一个月
    protected static final long MILLIS_SECOND = 1000;

    public static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    public static final Long MILLIS_HOUR = 60*MILLIS_MINUTE;
}
