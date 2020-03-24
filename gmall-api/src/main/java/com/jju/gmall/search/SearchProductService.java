package com.jju.gmall.search;

import com.jju.gmall.vo.search.SearchParam;
import com.jju.gmall.vo.search.SearchResponse;

/**
 *  商品检索服务
 */
public interface SearchProductService {
    SearchResponse searchProduct(SearchParam searchParam);
}
