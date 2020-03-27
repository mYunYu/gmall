package com.jju.locks.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {

    @Bean
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        //单节点模式
        config.useSingleServer().setAddress("redis://192.168.197.40:6379");
        //多节点模式
//        config.useClusterServers().addNodeAddress("", "");
        return Redisson.create(config);
    }

}
