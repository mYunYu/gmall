package com.jju.gmall.search;

import com.jju.gmall.vo.search.SearchParam;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchApplicationTests {

    @Autowired
    JestClient jestClient;

    @Autowired
    SearchProductService searchProductService;

    @Test
    public void testDsl(){
        SearchParam searchParam = new SearchParam();
        searchParam.setKeyword("手机");
        searchParam.setBrand(new String[]{"苹果"});
        searchParam.setCatelog3(new String[]{"19","20"});
        searchParam.setPriceFrom(5000);
        searchParam.setPriceTo(10000);
        searchParam.setProps(new String[]{"45:4.7", "46:4G"});
        searchProductService.searchProduct(searchParam);
    }

    @Test
    public void contextLoads() throws IOException {

        Search search = new Search
                .Builder("")
                .addIndex("product")
                .addType("info")
                .build();
        SearchResult execute = jestClient.execute(search);
        System.out.println(execute.getTotal());
    }

}
