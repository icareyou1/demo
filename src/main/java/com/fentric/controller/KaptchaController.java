package com.fentric.controller;

import com.fentric.domain.ResponseResult;
import com.fentric.utils.JwtUtils;
import com.fentric.utils.RedisCache;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@Controller
public class KaptchaController {
    @Autowired
    private Producer producer;
    @Autowired
    private RedisCache redisCache;

    /**
     * 生成验证码
     */
    @GetMapping("/kaptchaImage")
    public ResponseResult createCode(HttpServletResponse response){
        //生成text验证码
        String code = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(code);
        //生成uuid返回前端
        String uuid = JwtUtils.getUUID();
        //将生成的验证码存入redis中,有效期为五分钟
        String redisKey="code:"+uuid;
        redisCache.setCacheObject(redisKey,code,5, TimeUnit.MINUTES);
        //封装验证码和uuid
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image,"jpg",outputStream);
        } catch (IOException e) {
            return new ResponseResult(500,"ImageIO.write错误",null);
        }
        Map<String, String> map = new HashMap<>();
        map.put("uuid",uuid);
        map.put("code",code);
        return new ResponseResult(200,"验证码获取成功",map);
    }
}
