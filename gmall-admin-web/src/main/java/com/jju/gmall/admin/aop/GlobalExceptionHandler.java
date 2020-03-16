package com.jju.gmall.admin.aop;

import com.jju.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 *  统一处理所有异常，给前端返回500的异常
 */
@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public Object handlerException(Exception exception){
        log.error("系统全局异常感知，信息：{}", exception.getStackTrace());
        return new CommonResult().validateFailed("系统异常");
    }


    @ExceptionHandler(value = {NullPointerException.class})
    public Object handlerNullPointerException(Exception exception){
        log.error("系统全局异常感知，信息：{}", exception.getStackTrace());
        return new CommonResult().validateFailed("空指针异常");
    }

    //后续可以根据系统的异常分别进行处理

}
