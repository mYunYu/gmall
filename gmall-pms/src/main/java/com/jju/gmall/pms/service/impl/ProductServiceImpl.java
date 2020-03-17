package com.jju.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jju.gmall.pms.entity.*;
import com.jju.gmall.pms.mapper.*;
import com.jju.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jju.gmall.vo.PageInfoVo;
import com.jju.gmall.vo.product.PmsProductParam;
import com.jju.gmall.vo.product.PmsProductQueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
@Slf4j
@Component
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;

    @Autowired
    ProductFullReductionMapper productFullReductionMapper;

    @Autowired
    ProductLadderMapper productLadderMapper;

    @Autowired
    SkuStockMapper skuStockMapper;

    //当前线程共享同样的数据====>同一次调用，只要上面方法的数据，下面要用，就可以使用ThreadLocal实现共享数据
    ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //使用map这样存，就是ThreadLocal的原理
//    Map<Thread, Long> map = new HashMap<>();

    @Override
    public PageInfoVo productPageInfo(PmsProductQueryParam param) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if(param.getBrandId() != null){
            queryWrapper.eq("brand_id", param.getBrandId());
        }
        if(!StringUtils.isEmpty(param.getKeyword())){
            queryWrapper.like("name", param.getKeyword());
        }
        if(param.getProductCategoryId() != null){
            queryWrapper.eq("product_category_id", param.getProductCategoryId());
        }
        if(!StringUtils.isEmpty(param.getProductSn())){
            queryWrapper.like("product_sn", param.getProductSn());
        }
        if(param.getPublishStatus() != null){
            queryWrapper.eq("publish_status", param.getPublishStatus());
        }
        if(param.getVerifyStatus() != null){
            queryWrapper.eq("verify_status", param.getVerifyStatus());
        }

        IPage<Product> page = productMapper.selectPage(new Page<Product>(param.getPageNum(),
                        param.getPageSize()),
                        queryWrapper);

        return PageInfoVo.getVo(page, param.getPageSize());
    }

    /**
     *  考虑事务：
     *  1）哪些东西是一定要回滚的，哪些东西即使出错了也不必要回滚的
     *      商品的核心信息（基本数据、sku）保存的时候，不要受到别的无关信息的影响。
     *      无关信息出问题，核心信息也不用回滚的
     *
     *  2）事务的传播行为：propagation:当前方法的事务【是否要和别人共用一个事务】如何传播下去(里面的方法如果用事务，是否和它共用一个事务)
     *          Propagation propagation() default Propagation.REQUIRED;
     *
     *          REQUIRED(必须),
     *                  必须，如果以前有事务，就和之前的事务共用一个事务，没有就创建一个事务
     *          REQUIRES_NEW(总是用新的事务),
     *                  创建一个新的事务，如果以前有事务，暂停前面的事务
     *
     *          SUPPORTS(支持),
     *                  之前有事务，就以事务的方式运行，没有事务也可以
     *
     *          MANDATORY(强制),
     *                  一定要有事务，如果没事务，就报错
     *
     *          NOT_SUPPORTED(不支持),
     *                  不支持在事务内运行，如果已经有事务了，就挂起当前存在的事务
     *
     *          NEVER(从不使用),
     *                  不支持在事务内运行，如果已经有事务了，抛异常
     *
     *          NESTED(6);
     *                  开启一个子事务（MySQL不支持），需要支持还原点功能的数据库才行
     *
     *
     *
     *
     * 实例：
     * 外事务{
     *
     *     A(); REQUIRED        A
     *     B(); REQUIRES_NEW    B
     *     try{
     *         C(); REQUIRED        C
     *     }catch(Exception e){
     *         //C出异常
     *     }
     *     D(); REQUIRES_NEW    D
     *
     *     //给数据库存 ====外
     * }
     *
     * 场景1：
     *      A方法出现了异常：由于异常机制导致代码停止，下面无法执行，数据库什么都没有
     *
     * 场景2：
     *      C方法出现了异常：A回滚，B成功，C回滚，D无法执行，外无法执行
     *
     * 场景3：
     *      外执行成功后，int i = 10/0; 出现异常：B，D都成功，A，C，外都执行了但必须回滚
     *
     * 场景4：
     *      D方法出现了异常：外事务感知到异常，A，C回滚，外无法执行，D自己回滚，B成功
     *
     * 场景5：
     *      C用try-catch执行：C出了异常回滚，由于异常被捕获，外事务没有感知异常，A、B、D都成功，C自己回滚
     *
     *
     *  总结：
     *      在传播行为过程中，只要REQUIRES_NEW被执行过就一定成功，不管后面出不出问题，异常机制还是一样的，出现异常代码以后不执行
     *  REQUIRES只要感觉到异常就一定会回滚，和外事务是什么传播行为无关
     *
     *  传播行为总是来定义，当一个事务存在的时候，它内部的事务该怎么执行
     *
     *
     *
     *  如何让某些操作可以不回滚
     *
     *
     *
     *
     * 事务Spring中是怎么做的？
     * TransactionManager；
     * AOP做；
     *
     * 动态代理。
     *  hahaServiceProxy.saveBaseInfo();
     *
     *  A{
     *      A(){
     *          B(); //1,2,3
     *          C(); //4,5,6
     *          D(); //7,8,9
     *      }
     *  }
     *
     *  自己类调用自己类里面的方法，就是一个复制粘贴。归根到底，只是给
     *  controller{
     *      serviceProxy.a();
     *  }
     *  对象.方法()才能加上事务。
     *
     *
     *  A(){
     *      //1,2,3,4,5,6,7,8,9
     *      //
     *  }
     *
     *  A{
     *      A(){
     *          hahaService.B();
     *          hahaService.C();
     *          hahaService.D();
     *
     *      }
     *  }
     *
     *  事务的问题：
     *      Service自己调用自己的方法，无法加上真正的自己内部调整的各个事务
     *      解决：如果是  对象.方法()那就好了
     *       1）、要是能拿到ioc容器，从容器中再把我们的组件获取一下，用对象调方法。
     *
     *
     *
     *
     *
     *  事务：
     *     如果发现事务加不上，开启基于注解的事务功能  @EnableTransactionManagement
     *     如果要真的开启什么功能就显式的加上这个注解
     *
     *     事务的最终解决方案：
     *         1）、普通加事务。导入jdbc-starter，@EnableTransactionManagement，加@Transactional
     *         2）、方法自己调用自己类里面的加不上事务。
     *             1）、导入aop包，开启代理对象的相关功能
     *
     *             2）、获取到当前类真正的代理对象，去掉方法即可
     *                 1）、@EnableAspectJAutoProxy(exposeProxy = true):暴露代理对象
     *                 2）、AopContext.currentProxy()：获取当前代理对象
     *
     *
     *
     *
     *
     *
     *
     *
     * 复习：事务传播行为，
     * ====================================================================
     * 隔离级别：解决读写加锁问题的（数据底层的方案）。  MySQL默认级别：可重复读（快照）；
     *
     * 读未提交：脏读
     * 读已提交：
     * 可重复读：
     * 串行化：
     *
     * ===========================================================
     * 异常回滚策略
     * 异常：
     *      运行时异常（不受检查异常）
     *          ArithmeticException ......
     *      编译时异常（受检异常）
     *            FileNotFound；1）要么throw要么try- catch
     *
     * 运行的异常默认是一定回滚
     *      noRollbackFor：指定哪些异常不会回滚
     *          如： noRollbackFor = NullPointerException.class
     * 编译时异常默认是不回滚的；
     *      rollbackFor：指定哪些异常一定回滚的。
     *          如： rollbackFor = FileNotFoundException.class,
     *
     *
     * @param productParam
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void saveProduct(PmsProductParam productParam) {
        ProductServiceImpl proxy = (ProductServiceImpl) AopContext.currentProxy();

        //1、pms_product:保存商品基本信息
        proxy.saveProductInfo(productParam);

        //5、pms_sku_stock:sku_库存表
        proxy.saveSkuStock(productParam);


        //2、pms_product_attribute_value:保存这个商品对应的所有属性的值
        proxy.saveProductAttributeValue(productParam);

        //3、pms_product_full_reduction:保存商品的满减信息
        proxy.saveProductFullReduction(productParam);

        //4、pms_product_ladder:阶梯价格
        proxy.saveProductLadder(productParam);
    }

    //5、pms_sku_stock:sku_库存表
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSkuStock(PmsProductParam productParam) {
        List<SkuStock> skuStockList = productParam.getSkuStockList();
        for (int i = 1; i <= skuStockList.size(); i++) {
            SkuStock skuStock = skuStockList.get(i - 1);
            if(StringUtils.isEmpty(skuStock.getSkuCode())){
                //skuCode必须有,为空则生成
                //生成规则：商品id_sku自增id
                skuStock.setSkuCode(threadLocal.get() + "_" + i);
            }
            skuStock.setProductId(threadLocal.get());
            skuStockMapper.insert(skuStock);
        }

        log.info("当前线程---id={}, name={}", Thread.currentThread().getId(), Thread.currentThread().getName());
    }

    //4、pms_product_ladder:阶梯价格
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductLadder(PmsProductParam productParam) {
        List<ProductLadder> productLadderList = productParam.getProductLadderList();
        productLadderList.forEach((productLadder) -> {
            productLadder.setProductId(threadLocal.get());
            productLadderMapper.insert(productLadder);
        });

        log.info("当前线程---id={}, name={}", Thread.currentThread().getId(), Thread.currentThread().getName());
    }

    //3、pms_product_full_reduction:保存商品的满减信息
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductFullReduction(PmsProductParam productParam) {
        List<ProductFullReduction> fullReductionList = productParam.getProductFullReductionList();
        fullReductionList.forEach((productFullReduction) ->{
            productFullReduction.setProductId(threadLocal.get());
            productFullReductionMapper.insert(productFullReduction);
        });

        log.info("当前线程---id={}, name={}", Thread.currentThread().getId(), Thread.currentThread().getName());
    }

    //2、pms_product_attribute_value:保存这个商品对应的所有属性的值
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductAttributeValue(PmsProductParam productParam) {
        List<ProductAttributeValue> valueList = productParam.getProductAttributeValueList();
        valueList.forEach((productAttributeValue) -> {
            productAttributeValue.setProductId(threadLocal.get());
            productAttributeValueMapper.insert(productAttributeValue);
        });

        log.info("当前线程---id={}, name={}", Thread.currentThread().getId(), Thread.currentThread().getName());
    }

    //1、pms_product:保存商品基本信息
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductInfo(PmsProductParam productParam) {
        Product product = new Product();
        BeanUtils.copyProperties(productParam, product);
        productMapper.insert(product);
        log.debug("商品的id：{}", product.getId());

        threadLocal.set(product.getId());

        log.info("当前线程---id={}, name={}", Thread.currentThread().getId(), Thread.currentThread().getName());
    }

}
