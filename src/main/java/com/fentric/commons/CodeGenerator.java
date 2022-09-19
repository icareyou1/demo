package com.fentric.commons;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

/**
 * 代码生成工具
 * 1.依赖jar包：
 * --------------------------------------------------------------
 * <dependency>
 * <groupId>com.baomidou</groupId>
 * <artifactId>mybatis-plus</artifactId>
 * <version>3.5.1</version>
 * </dependency>
 * <!--pojo，mapper等代码生成器-->
 * <dependency>
 * <groupId>com.baomidou</groupId>
 * <artifactId>mybatis-plus-generator</artifactId>
 * <version>3.5.3</version>
 * </dependency>
 * <dependency>
 * <groupId>org.apache.velocity</groupId>
 * <artifactId>velocity-engine-core</artifactId>
 * <version>2.3</version>
 * </dependency>
 * ----------------------------------------------------------
 */

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/fentric_002", "root", "root")
                .globalConfig(builder -> {
                    builder.author("zhouqi") // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            //.fileOverride() // 覆盖已生成文件
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.fentric")// 设置父包名
                            //.moduleName("system") // 设置父包模块名
                            .entity("pojo")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // 设置Xml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("iot_device_tag")//设置需要生成的表名,不配置全部
                            .serviceBuilder()
                            .formatServiceFileName("%sService"); // 设置需要生成的表名
                    //.addTablePrefix("article");
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}