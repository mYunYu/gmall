package com.jju.gmall.pms;


import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
    1、配置整合dubbo
    2、配置整合MyBatisPlus

    logstash整合
    1、导入jar包
    2、导入日志配置
    3、在kibana建立app*索引
 */
@EnableDubbo        //开启dubbo的注解支持
@MapperScan(basePackages = "com.jju.gmall.pms.mapper")
@SpringBootApplication
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
