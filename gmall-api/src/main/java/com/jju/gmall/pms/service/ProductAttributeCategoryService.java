package com.jju.gmall.pms.service;

import com.jju.gmall.pms.entity.ProductAttributeCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jju.gmall.vo.PageInfoVo;

/**
 * <p>
 * 产品属性分类表 服务类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
public interface ProductAttributeCategoryService extends IService<ProductAttributeCategory> {

    /**
     *  分页查询所有的属性分类
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfoVo productAttributeCategoryPageInfo(Integer pageNum, Integer pageSize);
}
