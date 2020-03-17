package com.jju.gmall.pms.service;

import com.jju.gmall.pms.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jju.gmall.vo.PageInfoVo;
import com.jju.gmall.vo.product.PmsProductParam;
import com.jju.gmall.vo.product.PmsProductQueryParam;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
public interface ProductService extends IService<Product> {

    /**
     *  根据复杂查询条件返回分页数据
     * @param productQueryParam
     * @return
     */
    PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam);

    /**
     * 保存商品数据
     * @param productParam
     */
    void saveProduct(PmsProductParam productParam);
}
