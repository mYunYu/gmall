package com.jju.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jju.gmall.pms.entity.Product;
import com.jju.gmall.pms.mapper.ProductMapper;
import com.jju.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jju.gmall.vo.PageInfoVo;
import com.jju.gmall.vo.product.PmsProductQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
@Component
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    ProductMapper productMapper;

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

        PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(), page.getPages(),
                param.getPageSize(),
                page.getRecords(),
                page.getCurrent());

        return pageInfoVo;
    }
}
