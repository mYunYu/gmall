package com.jju.gmall.admin.aop;

import com.jju.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 *  切面如何编写：
 *  1、导入切面场景，导入jar包：
 *  2、编写切面
 *      1)、@Aspect
 *      2）、切入点表达式
 *      3）、通知：
 *          前置通知：方法执行之前触发
 *          后置通知：方法执行之后触发
 *          返回通知：方法正常返回之后触发
 *          异常通知：方法出现异常触发
 *
 *          正常执行：前置通知---》返回通知---》后置通知
 *          异常执行：前置通知---》异常通知---》后置通知
 *
 *          环绕通知：4合1：会拦截方法的执行
 *
 */

//历用aop完成统一的数据校验，数据校验出错就返回给前端错误提示
@Slf4j
@Component
@Aspect
public class DataValidAspect {

    @Around("execution(* com.jju.gmall.admin..*Controller.*(..))")
    public Object validAround(ProceedingJoinPoint point) throws Throwable{
        //校验参数
        Object[] args = point.getArgs();
        for (Object obj : args) {
            if(obj instanceof BindingResult){
                BindingResult result = (BindingResult) obj;
                //检验错误数
                int errorCount = result.getErrorCount();
                if(errorCount > 0){
                    log.debug("参数校验出错信息：{}", result);
                    return new CommonResult().validateFailed(result);
                }
            }
        }
        //执行目标方法，就是反射的 method.invoke()
        return point.proceed(point.getArgs());
    }

}
