package com.fentric.controller;


import com.fentric.domain.ResponseResult;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * 复制BasicErrorController编写,更改白页和json默认逻辑
 *
 * 修改部分:无参构造器,error,errorhtml
 */
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class WhitePageAndJsonErrorController extends AbstractErrorController {
    public WhitePageAndJsonErrorController(){
        super(new DefaultErrorAttributes());
    }
    @RequestMapping
    @ResponseBody
    public ResponseResult error(HttpServletRequest request) {
        return new ResponseResult(404,"请求地址"+request.getRequestURI()+"不存在");
    }

}
