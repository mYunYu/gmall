package com.jju.locks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisIncrService {

    @Autowired
    StringRedisTemplate redisTemplate;

    public void incrDistribute(){

        /**
         *  1、String lock = get("lock")
         *            if(lock == null){
         *               set("lock","1");
         *               执行业务
         *               del("lock");
         *          }
         *          setnx是原子操作，判断带保存
         *          Integer lock = setnx("lock", "111");  //0代表没有保存数据，1代表保存数据成功
         *         分布式锁的核心：保证原子性
         *
         *
         *   AOP
         *   最终的分布式锁的代码：
         *    @Lock
         *    public void hello(){
         *        String token = uuid;
         *        String lock = redis.setnx("lock", token, 10s);
         *        if(lock == "ok"){
         *              //执行业务逻辑
         *              //lua脚本删除锁
         *        }
         *        else{
         *              hello(); //自旋
         *        }
         *    }
         */

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String num = valueOperations.get("num");
        if(num != null){
            Integer i = Integer.parseInt(num);
            i = i + 1;
            valueOperations.set("num", i.toString());
        }
    }

    /**
     *  进程内肯定可以，单机。分布式不行
     */
    public synchronized void incr() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String num = valueOperations.get("num");
        if(num != null){
            Integer i = Integer.parseInt(num);
            i = i + 1;
            valueOperations.set("num", i.toString());
        }
    }
}
