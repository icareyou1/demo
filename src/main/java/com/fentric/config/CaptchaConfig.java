package com.fentric.config;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Configuration
public class CaptchaConfig {
    @Bean
    public ArithmeticCaptcha captcha(){
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(160,60,2);
        //有默认字体可以不同设置
        captcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        //设置类型
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        return captcha;
    }
}
