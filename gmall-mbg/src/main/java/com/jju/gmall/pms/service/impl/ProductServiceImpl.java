package com.jju.gmall.pms.service.impl;

import com.jju.gmall.pms.entity.Product;
import com.jju.gmall.pms.mapper.ProductMapper;
import com.jju.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-16
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
