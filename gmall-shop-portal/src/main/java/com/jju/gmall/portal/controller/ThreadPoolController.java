package com.jju.gmall.portal.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class ThreadPoolController {

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    /**
     *  查看线程池对象信息
     * @return
     */
    @GetMapping("/thread/status")
    public String threadPoolStatus(){
        return JSON.toJSONString(threadPoolExecutor);
    }

}
