package com.jju.locks.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisIncrService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedissonClient redissonClient;


    public void useRedissonForLock(){
        //获取一把锁,只要各个代码，用的锁名一样即可
        RLock lock = redissonClient.getLock("lock");

        //加锁
        lock.lock();

        Jedis jedis = jedisPool.getResource();
        String num = jedis.get("num");
        Integer i = Integer.parseInt(num);
        i += 1;
        jedis.set("num", i.toString());
        jedis.close();

        //解锁
        lock.unlock();

    }


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


        //1、加锁
//        String token = UUID.randomUUID().toString();
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
//        if(lock){
//            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//            String num = valueOperations.get("num");
//            if(num != null){
//                Integer i = Integer.parseInt(num);
//                i = i + 1;
//                valueOperations.set("num", i.toString());
//            }
//
//            //删除锁
//            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(script);
//            redisTemplate.execute(redisScript, Arrays.asList("lock"), token);
//            System.out.println("删除锁完成");
//        }
//        else{
//            incrDistribute();
//        }


        /**
         *  锁的考虑：
         *      1、自旋：
         *          自旋次数：
         *          自旋超时：
         *      2、锁设置
         *          锁粒度：细；记录级别；
         *              1、各自服务各自锁
         *              2、分析好粒度，不要锁住无关数据，一种数据一种锁，一条数据一个锁
         *      3、锁类型：
         *          读写锁、
         *
         *
         *
         * 查询商品详情：进缓存--》击穿、穿透、雪崩
         *
         *  查商品
         *  public Product productInfo(){
                 Product cache = jedis.get("product-1");
         *       if(cache != null){
         *            return cache;
         *       }
         *       else{
         *         //查数据库
         *         //不能直接查询，需要加锁
         *         String lock = jedis.set("lock", token, SetParams.setParams().ex(3).nx());
         *         if(lock){
         *             Product product = getFromDB();
         *             jedis.set("product-1", product);
         *          }
         *          else{
         *              return productInfo();
         *          }
         *       }
         *   }
         *
         *
         *
         */

        Jedis jedis = jedisPool.getResource();

        try{
            String token = UUID.randomUUID().toString();
            String lock = jedis.set("lock", token, SetParams.setParams().ex(3).nx());
            if(lock != null && lock.equalsIgnoreCase("OK")){
                String num = jedis.get("num");
                Integer i = Integer.parseInt(num);
                i += 1;
                jedis.set("num", i.toString());

                //删除锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList("lock"), Collections.singletonList(token));
                System.out.println("删除锁ok");
            }
            else{
                //需要考虑重试次数
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                incrDistribute();
            }
        }finally {
            jedis.close();
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
