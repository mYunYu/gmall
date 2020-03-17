package com.jju.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jju.gmall.constant.SysCacheConstant;
import com.jju.gmall.pms.entity.ProductCategory;
import com.jju.gmall.pms.mapper.ProductCategoryMapper;
import com.jju.gmall.pms.service.ProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jju.gmall.vo.product.PmsProductCategoryWithChildrenItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
@Slf4j
@Component
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    ProductCategoryMapper productCategoryMapper;

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Override
    public List<PmsProductCategoryWithChildrenItem> listCategoryWithChildren(int i) {
        Object cacheMenu = redisTemplate.opsForValue().get(SysCacheConstant.CATEGORY_MENU_CACHE_KEY);
        List<PmsProductCategoryWithChildrenItem> items = null;
        if(cacheMenu != null){
            items = (List<PmsProductCategoryWithChildrenItem>) cacheMenu;
        }
        else{
            items = productCategoryMapper.listCategoryWithChildren(i);
            //加入缓存
            redisTemplate.opsForValue().set(SysCacheConstant.CATEGORY_MENU_CACHE_KEY, items);
        }

        return items;
    }
}
