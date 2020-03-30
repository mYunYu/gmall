package com.jju.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jju.gmall.pms.entity.Product;
import com.jju.gmall.pms.service.ProductService;
import com.jju.gmall.to.CommonResult;
import com.jju.gmall.to.es.EsProduct;
import com.jju.gmall.to.es.EsSkuProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class ProductItemController {

    @Reference
    ProductService productService;

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Qualifier("otherThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor otherThreadPoolExecutor;


    /**
     *  数据库（商品的基本信息表、商品的属性表、商品的促销表）和 es（info/attr/sale）
     *
     *  高并发系统的优化
     *  1、加缓存
     *  2、开异步
     *
     * @return
     */
    public EsProduct productInfo2(){
        //1、商品的基本数据（名称，介绍）
//        threadPoolExecutor.submit(
//            new Thread(()->{
//                System.out.println("查询商品的基本数据");
//            })
//        );

        CompletableFuture<Product> productInfo = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性数据");
            Product product = null;
            return product;
        }, threadPoolExecutor).whenComplete((r, e) -> {
            System.out.println("处理结果：" + r);
            System.out.println("处理异常：" + e);
        });


        //2、商品的属性数据
//        new Thread(()->{
//            System.out.println("查询商品的属性数据");
//        }).start();

        CompletableFuture<EsSkuProductInfo> skuProductInfo = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性数据");
            EsSkuProductInfo esSkuProductInfo = null;
            return esSkuProductInfo;
        }, threadPoolExecutor).whenComplete((r, e) -> {
            System.out.println("处理结果：" + r);
            System.out.println("处理异常：" + e);
        });


        //3、商品的营销数据
        new Thread(()->{
            System.out.println("查询商品的营销数据");
        }).start();

        //4、商品的配送数据
        new Thread(()->{
            System.out.println("查询商品的配送数据");
        }).start();

        //5、商品的增值服务数据
        new Thread(()->{
            System.out.println("查询商品的增值服务数据");
        }).start();

        return null;
    }

    /**
     *  商品的详情
     * @param id
     * @return
     */
    @GetMapping("/item/{id}.html")
    public CommonResult productInfo(@PathVariable("id") Long id){
        EsProduct esProduct = productService.productAllInfo(id);

        return new CommonResult().success(esProduct);
    }

    /**
     *  商品sku信息详情
     * @param id
     * @return
     */
    @GetMapping("/item/sku/{id}.html")
    public CommonResult productSkuInfo(@PathVariable("id") Long id){
        EsProduct esProduct = productService.productSkuInfo(id);

        return new CommonResult().success(esProduct);
    }

}
