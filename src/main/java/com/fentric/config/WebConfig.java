package com.fentric.config;

import com.fentric.handler.LoginParamHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 进行跨域设置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //允许跨域请求的域名
                .allowedOriginPatterns("*")
                //是否允许cookie
                .allowCredentials(false)
                //设置允许的请求方式
                .allowedMethods("GET","POST","DELETE","PUT")
                //设置允许的header属性
                .allowedHeaders("*")
                //跨域允许时间
                .maxAge(1800L);
    }
    /**
     * 登录接口参数解析器,这种方法可以不用封装pojo更加优雅
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginParamHandlerMethodArgumentResolver());
    }
}
