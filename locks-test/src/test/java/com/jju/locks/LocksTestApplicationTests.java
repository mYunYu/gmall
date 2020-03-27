package com.jju.locks;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.JedisPool;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocksTestApplicationTests {

    @Autowired
    JedisPool jedisPool;


    /**
     *  springboot 默认redisTemplate和redisConnectionFactory不能和jedis共存
     */
    @Test
    public void contextLoads() {
        System.out.println(jedisPool);
    }

}
