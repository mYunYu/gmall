package com.jju.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jju.gmall.pms.entity.ProductAttribute;
import com.jju.gmall.pms.mapper.ProductAttributeMapper;
import com.jju.gmall.pms.service.ProductAttributeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jju.gmall.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 商品属性参数表 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
@Service
@Component
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {

    @Autowired
    ProductAttributeMapper productAttributeMapper;

    @Override
    public PageInfoVo getCategoryAttributes(Long cid, Integer type, Integer pageSize, Integer pageNum) {
        //构造查询条件
        QueryWrapper<ProductAttribute> queryWrapper = new QueryWrapper<ProductAttribute>().eq("product_attribute_category_id", cid)
                .eq("type", type);
        //分页查询
        IPage<ProductAttribute> page = productAttributeMapper.selectPage(new Page<>(pageNum, pageSize),
                queryWrapper);

        return PageInfoVo.getVo(page, pageSize.longValue());
    }
}
