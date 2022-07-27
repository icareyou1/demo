package com.fentric.config;

import com.fentric.filter.CodeFilter;
import com.fentric.filter.JwtAuthenticationTokenFilter;
import com.fentric.handler.LogoutSuccessHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 新版本中WebSecurityConfigurerAdapter 过时
 * protected void configure(AuthenticationManagerBuilder auth) 认证
 * protected void configure(HttpSecurity http) 授权
 *
 * 新用法无需继承,直接定义配置类,注入
 */
@Configuration
//开启认证
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    //spring security 配置类
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    //token认证过滤器
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    //认证异常处理器
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    //授权异常处理器
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    //加密,可以通过实现PasswordEncoder类,BCryptPasswordEncoder有实现它
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //生成全局的authenticationManager
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http
                .csrf().disable()//CSRF禁用,
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//不通过session获取securityContext
                .and().authorizeRequests()
                .mvcMatchers("/captchaImage").anonymous()
                .mvcMatchers("/sysUser/login").anonymous()//放行资源写在前面
                .anyRequest()).authenticated();//表示所有资源必须认证之后才能访问
        //http.formLogin();//开启表单认证  开启表单认证,并且访问/login登录才会经过UsernamePasswordAuthenticationFilter类
        //        .and().logout().logoutUrl("/logout").logoutSuccessHandler(new LogoutSuccessHandlerImpl());
                //.loginPage("/login.html")     //指定登录页面
                //.loginProcessingUrl("/dologin")  //指定处理登录请求
                //.usernameParameter("username")
                //.passwordParameter("password")
                //.successForwardUrl("/hello")      //请求成功后默认跳转/hello,forward始终跳转至hello
                //.defaultSuccessUrl("/index")    //redirect跳转,如果之前有被拦截的请求会回到之前请求   添加true就总是跳转至/index

        //解析jwt
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //其他方案畅想:1.可以在认证和授权处理器前再加验证码处理器
        //          2.或者写controller
        //http.addFilterAfter(codeFilter,JwtAuthenticationTokenFilter.class);

        //认证异常处理器和授权异常处理器
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler);
        //允许跨域
        http.cors();
        return (SecurityFilterChain)http.build();
    }
    /*@Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return new WebSecurityCustomizer() {
            @Override
            public void customize(WebSecurity web) {
                web.ignoring().antMatchers();
            }
        };
    }*/
}
