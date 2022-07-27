package com.fentric.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

//如果有属性为null,则不进行json序列化
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T>{
    //状态码
    private Integer code;
    //提示信息
    private String msg;
    //数据结果
    private T data;

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
