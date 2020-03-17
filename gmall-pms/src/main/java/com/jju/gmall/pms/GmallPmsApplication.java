package com.jju.gmall.pms;


import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
    1、配置整合dubbo
    2、配置整合MyBatisPlus

    logstash整合
    1、导入jar包
    2、导入日志配置
    3、在kibana建立app*索引


    事务：
    如果发现事务加不上，开启基于注解的事务功能  @EnableTransactionManagement
    如果要真的开启什么功能就显式的加上这个注解

    事务的最终解决方案：
        1）、普通加事务。导入jdbc-starter，@EnableTransactionManagement，加@Transactional
        2）、方法自己调用自己类里面的加不上事务。
            1）、导入aop包，开启代理对象的相关功能

            2）、获取到当前类真正的代理对象，去掉方法即可
                1）、@EnableAspectJAutoProxy(exposeProxy = true):暴露代理对象
                2）、AopContext.currentProxy()：获取当前代理对象
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableTransactionManagement
@EnableDubbo        //开启dubbo的注解支持
@MapperScan(basePackages = "com.jju.gmall.pms.mapper")
@SpringBootApplication
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
