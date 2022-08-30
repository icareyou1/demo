package com.fentric.controller;

import com.fentric.domain.ResponseResult;
import com.fentric.utils.JwtUtils;
import com.fentric.utils.RedisCache;
import com.fentric.utils.WebUtils;
import com.google.code.kaptcha.Producer;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class CaptchaController {
    @Autowired
    private RedisCache redisCache;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public ResponseResult createCode(HttpServletResponse response){
        //如果使用容器注入,生成重复代码
        //生成计算结果
        ArithmeticCaptcha captcha = WebUtils.captcha();
        String code = captcha.text();
        //生成表达式
        String arithmeticString = captcha.getArithmeticString();
        //生成uuid返回前端
        String uuid = JwtUtils.getUUID();
        //将生成的验证码存入redis中,有效期为五分钟
        String redisKey="code:"+uuid;
        redisCache.setCacheObject(redisKey,code,5, TimeUnit.MINUTES);
        //封装验证码和uuid
        String image = captcha.toBase64();
        Map<String, String> map = new HashMap<>();
        map.put("uuid",uuid);
        map.put("codeImage",image);
        //todo 为了方便测试,开放验证码答案,后期删除
        map.put("codeValue",code);
        return new ResponseResult(200,"验证码获取成功",map);
    }
}
