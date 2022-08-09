package com.fentric;

import com.fentric.config.ServerSocketConfig;
import com.fentric.service.IotRunService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@MapperScan(basePackages = "com.fentric.mapper")
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        //new ServerSocketConfig().createServerSocket();
    }

}
