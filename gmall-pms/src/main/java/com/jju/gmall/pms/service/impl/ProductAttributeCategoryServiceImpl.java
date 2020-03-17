package com.jju.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jju.gmall.pms.entity.ProductAttributeCategory;
import com.jju.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.jju.gmall.pms.service.ProductAttributeCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jju.gmall.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
@Component
@Service
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {

    @Autowired
    ProductAttributeCategoryMapper productAttributeCategoryMapper;

    @Override
    public PageInfoVo productAttributeCategoryPageInfo(Integer pageNum, Integer pageSize) {
        IPage<ProductAttributeCategory> page = productAttributeCategoryMapper.selectPage(new Page<ProductAttributeCategory>(pageNum, pageSize), null);

//        PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(), page.getPages(), pageSize.longValue(), page.getRecords(), page.getCurrent());

        return PageInfoVo.getVo(page, pageSize.longValue());
    }
}
