package com.fentric.utils;


import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;


public class WebUtils
{
    /**
     * 将字符串渲染到客户端
     * 
     * @param response 渲染对象
     * @param string 待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string)
    {
        try
        {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static ArithmeticCaptcha captcha(){
        //方式一:如果不想创建新对象ArithmeticCaptcha 产生新的随机数,就必须修改,这个jar包的源码,
        //public abstract class ArithmeticCaptchaAbstract 有方法 protected char[] alphas()

        //方式二:自己继承重写
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(160,60,2);
        captcha.checkAlpha();
        //有默认字体可以不同设置
        captcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        //设置类型
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        return captcha;
    }
}
