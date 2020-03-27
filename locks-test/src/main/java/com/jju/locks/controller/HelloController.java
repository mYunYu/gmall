package com.jju.locks.controller;

import com.jju.locks.service.RedisIncrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    RedisIncrService redisIncrService;

    @GetMapping("/inc")
    public String incr(){
//        redisIncrService.incr();
//        redisIncrService.incrDistribute();
        redisIncrService.useRedissonForLock();
        return "ok";
    }

}
