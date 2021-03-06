package com.jju.gmall.pms.mapper;

import com.jju.gmall.pms.entity.ProductAttribute;
import com.jju.gmall.pms.entity.ProductAttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jju.gmall.to.es.EsProductAttributeValue;

import java.util.List;

/**
 * <p>
 * 存储产品参数信息的表 Mapper 接口
 * </p>
 *
 * @author mYunYu
 * @since 2020-03-12
 */
public interface ProductAttributeValueMapper extends BaseMapper<ProductAttributeValue> {

    List<EsProductAttributeValue> selectProductBaseAttributeAndValue(Long id);

    List<ProductAttribute> selectProductSaleAttributeName(Long id);
}
