package com.jju.gmall.to.es;

import com.jju.gmall.pms.entity.SkuStock;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EsSkuProductInfo extends SkuStock implements Serializable {

    private String skuTitle;    //sku的特定标题

    /**
     *  每个sku不同的属性以及它的值
     *
     *  颜色：黑色
     *  内存：128g
     */
    List<EsProductAttributeValue> attributeValues;
}
