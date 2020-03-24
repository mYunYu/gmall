package com.jju.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.jju.gmall.constant.EsConstant;
import com.jju.gmall.search.SearchProductService;
import com.jju.gmall.to.es.EsProduct;
import com.jju.gmall.vo.search.SearchParam;
import com.jju.gmall.vo.search.SearchResponse;
import com.jju.gmall.vo.search.SearchResponseAttrVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Component
public class SearchProductServiceImpl implements SearchProductService {

    @Autowired
    JestClient jestClient;

    @Override
    public SearchResponse searchProduct(SearchParam searchParam) {

        //1、构建检索条件
        String dsl = buildDsl(searchParam);
        log.info("商品检索的详细数据：{}", dsl);

        Search search = new Search
                .Builder(dsl)
                .addIndex(EsConstant.PRODUCT_INDEX)
                .addType(EsConstant.PRODUCT_INFO_ES_TYPE)
                .build();

        SearchResult searchResult = null;
        try {
            //2、检索
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //3、将返回的searchResult转为SearchResponse
        SearchResponse searchResponse = buildSearchResult(searchResult);
        searchResponse.setPageSize(searchParam.getPageSize());
        searchResponse.setPageNum(searchParam.getPageNum());

        return searchResponse;
    }

    /**
     *  将返回的searchResult转为SearchResponse
     * @param searchResult
     * @return
     */
    private SearchResponse buildSearchResult(SearchResult searchResult) {
        SearchResponse searchResponse = new SearchResponse();

        MetricAggregation aggregations = searchResult.getAggregations();

        //region  聚合品牌

        TermsAggregation brandAgg = aggregations.getTermsAggregation("brand_agg");
        List<String> brandNames = new ArrayList<>();

        List<TermsAggregation.Entry> buckets = brandAgg.getBuckets();
        buckets.forEach(bucket ->{
            String keyAsString = bucket.getKeyAsString();
            brandNames.add(keyAsString);
        });

        SearchResponseAttrVo attrVo = new SearchResponseAttrVo();
        attrVo.setName("品牌");
        attrVo.setValue(brandNames);

        //可供选择的品牌
        searchResponse.setBrand(attrVo);

        //endregion

        //region 聚合分类

        TermsAggregation categoryAgg = aggregations.getTermsAggregation("category_agg");
        List<String> categoryValue = new ArrayList<>();

        categoryAgg.getBuckets().forEach(bucket -> {
            String categoryName = bucket.getKeyAsString();

            TermsAggregation categoryIdAgg = bucket.getTermsAggregation("categoryId_agg");
            String categoryId = categoryIdAgg.getBuckets().get(0).getKeyAsString();

            Map<String, String> map = new HashMap<>();
            map.put("id", categoryId);
            map.put("name", categoryName);
            String categoryInfoJson = JSON.toJSONString(map);

            categoryValue.add(categoryInfoJson);
        });

        SearchResponseAttrVo categoryVo = new SearchResponseAttrVo();
        categoryVo.setName("分类");
        categoryVo.setValue(categoryValue);
        //可供选择的分类
        searchResponse.setCatelog(categoryVo);

        //endregion

        //region 聚合属性

        TermsAggregation attrNameAgg = aggregations.getChildrenAggregation("attr_agg")
                                                    .getTermsAggregation("attrName_agg");
        List<SearchResponseAttrVo> attrList = new ArrayList<>();
        attrNameAgg.getBuckets().forEach(bucket -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();

            //属性名称
            String attrName = bucket.getKeyAsString();
            searchResponseAttrVo.setName(attrName);

            //属性id
            TermsAggregation attrIdAgg = bucket.getTermsAggregation("attrId_agg");
            String attrId = attrIdAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setProductAttributeId(Long.parseLong(attrId));

            //属性的所涉及的所有值
            TermsAggregation attrValueAgg = bucket.getTermsAggregation("attrValue_agg");
            List<String> valueList = new ArrayList<>();
            attrValueAgg.getBuckets().forEach(valueBucket -> {
                valueList.add(valueBucket.getKeyAsString());
            });
            searchResponseAttrVo.setValue(valueList);

            attrList.add(searchResponseAttrVo);
        });

        //所有可以筛选的属性
        searchResponse.setAttrs(attrList);

        //endregion

        //region 封装检索到的商品数据（包含设置标题高亮）

        List<SearchResult.Hit<EsProduct, Void>> hits = searchResult.getHits(EsProduct.class);
        List<EsProduct> esProductList = new ArrayList<>();
        hits.forEach(hit -> {
            EsProduct esProduct = hit.source;
            //提取到高亮结果
            Map<String, List<String>> highlightMap = hit.highlight;
            String highlightTitle = highlightMap.get("skuProductInfos.skuTitle").get(0);
            //设置高亮结果
            esProduct.setName(highlightTitle);

            esProductList.add(esProduct);
        });

        //将查到的记录分封装
        searchResponse.setProducts(esProductList);

        //endregion

        searchResponse.setTotal(searchResult.getTotal());

        return searchResponse;
    }

    /**
     *  构建DSL语句
     * @param searchParam
     * @return
     */
    private String buildDsl(SearchParam searchParam) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1、查询
        //1.1、检索
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("skuProductInfos.skuTitle", searchParam.getKeyword());
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(
                    "skuProductInfos",
                    matchQuery,
                    ScoreMode.None);

            boolQuery.must(nestedQuery);
        }

        //1.2、过滤
        //1.2.1、按照属性过滤，按照品牌过滤，按照分类过滤
        //按照三级分类条件过滤
        if(searchParam.getCatelog3() != null && searchParam.getCatelog3().length > 0){
            boolQuery.filter(QueryBuilders.termsQuery("productCategoryId", searchParam.getCatelog3()));
        }
        //按照品牌条件过滤
        if(searchParam.getBrand() != null && searchParam.getBrand().length > 0){
            boolQuery.filter(QueryBuilders.termsQuery("brandName.keyword", searchParam.getBrand()));
        }
        //按照所有的筛选属性进行过滤
        if(searchParam.getProps() != null && searchParam.getProps().length > 0){
            String[] props = searchParam.getProps();
            for (String prop : props) {
                //2:4g-3g  2号属性的值是4g或者3g
                String[] split = prop.split(":");
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("attrValueList.productAttributeId", split[0]))
                        .must(QueryBuilders.termsQuery("attrValueList.value.keyword", split[1].split("-")));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrValueList", boolQueryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        //价格区间过滤
        if(searchParam.getPriceFrom() != null || searchParam.getPriceTo() != null){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            if(searchParam.getPriceFrom() != null){
                rangeQuery.gte(searchParam.getPriceFrom());
            }
            if(searchParam.getPriceTo() != null){
                rangeQuery.lte(searchParam.getPriceTo());
            }
            boolQuery.filter(rangeQuery);
        }

        builder.query(boolQuery);

        //2、高亮
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuProductInfos.skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            builder.highlighter(highlightBuilder);
        }

        //3、聚合
        //按照品牌的
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandName.keyword");
        brandAgg.subAggregation(AggregationBuilders.terms("brandId").field("brandId"));

        //按照分类的
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("category_agg").field("productCategoryName.keyword");
        categoryAgg.subAggregation(AggregationBuilders.terms("categoryId_agg").field("productCategoryId"));

        //按照属性的
        NestedAggregationBuilder attrNestedAgg = AggregationBuilders.nested("attr_agg", "attrValueList");
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrName_agg").field("attrValueList.name");
        //聚合attrValue的值
        attrNameAgg.subAggregation(AggregationBuilders.terms("attrValue_agg").field("attrValueList.value.keyword"));
        //聚合attrId的值
        attrNameAgg.subAggregation(AggregationBuilders.terms("attrId_agg").field("attrValueList.productAttributeId"));
        attrNestedAgg.subAggregation(attrNameAgg);

        builder.aggregation(brandAgg);
        builder.aggregation(categoryAgg);
        builder.aggregation(attrNestedAgg);

        //4、分页
        builder.from((searchParam.getPageNum() - 1) * searchParam.getPageSize());
        builder.size(searchParam.getPageSize());

        //5、排序
        if(!StringUtils.isEmpty(searchParam.getOrder())){
            //order=1:asc  排序规则   0:asc    
            //0：综合排序  1：销量  2：价格
            String order = searchParam.getOrder();
            String[] split = order.split(":");
            if(split[0].equals("0")){
                //综合排序，默认排序
            }
            if(split[0].equals("1")){
                //销量
                FieldSortBuilder saleSortBuilder = SortBuilders.fieldSort("sale");
                if(split[1].equalsIgnoreCase("asc")){
                    saleSortBuilder.order(SortOrder.ASC);
                }
                else{
                    saleSortBuilder.order(SortOrder.DESC);
                }
                builder.sort(saleSortBuilder);
            }
            if(split[0].equals("2")){
                //价格
                FieldSortBuilder priceSortBuilder = SortBuilders.fieldSort("price");
                if(split[1].equalsIgnoreCase("asc")){
                    priceSortBuilder.order(SortOrder.ASC);
                }
                else{
                    priceSortBuilder.order(SortOrder.DESC);
                }
                builder.sort(priceSortBuilder);
            }
        }

        return builder.toString();
    }
}
