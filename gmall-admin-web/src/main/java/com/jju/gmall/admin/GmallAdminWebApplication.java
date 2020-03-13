package com.jju.gmall.admin;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 *  VO:（View Object/Value Object）：视图对象
 *  DAO：（Database Access Object）：数据库访问对象：专门用来对数据库进行crud的对象。XxxMapper
 *  POJO：(Plain Old Java Object)：古老的单纯的java对象。javaBean（封装数据的）
 *  DO：（Data Object）：数据对象----POJO（Database Object）：数据库对象（专门用来封装数据表的实体类）
 *  TO：（Transfer Object）：传输对象：
 *          服务之间互调，数据传输封装对象
 *  DTO：（Data Transfer Object）
 */

@EnableDubbo
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //不进行数据源自动配置
public class GmallAdminWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallAdminWebApplication.class, args);
    }

}
