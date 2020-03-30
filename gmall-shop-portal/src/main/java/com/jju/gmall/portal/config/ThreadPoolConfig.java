package com.jju.gmall.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  配置当前系统的线程池信息
 */
@Configuration
public class ThreadPoolConfig {


    //核心业务线程池
    @Bean("mainThreadPoolExecutor")
    public ThreadPoolExecutor mainThreadPoolExecutor(PoolProperties poolProperties){
        LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(poolProperties.getQueueSize());

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolProperties.getCoreSize(),
                poolProperties.getMaximumPoolSize(),
                10,
                TimeUnit.MINUTES,
                blockingQueue);

        return threadPoolExecutor;
    }

    //非核心业务线程池
    @Bean("otherThreadPoolExecutor")
    public ThreadPoolExecutor otherThreadPoolExecutor(PoolProperties poolProperties){
        LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(poolProperties.getQueueSize());

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolProperties.getCoreSize(),
                poolProperties.getMaximumPoolSize(),
                10,
                TimeUnit.MINUTES,
                blockingQueue);

        return threadPoolExecutor;
    }

}
