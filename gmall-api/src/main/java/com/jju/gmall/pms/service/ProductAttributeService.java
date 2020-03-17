package com.jju.gmall.pms.service;

import com.jju.gmall.pms.entity.ProductAttribute;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jju.gmall.vo.PageInfoVo;

/**
 * <p>
 * 商品属性参数表 服务类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
public interface ProductAttributeService extends IService<ProductAttribute> {

    /**
     *  查询某个属性分类下的所有属性和参数
     * @param cid
     * @param type
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfoVo getCategoryAttributes(Long cid, Integer type, Integer pageSize, Integer pageNum);
}
