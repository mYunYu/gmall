package com.jju.gmall.pms;

import com.jju.gmall.pms.entity.Brand;
import com.jju.gmall.pms.entity.Product;
import com.jju.gmall.pms.service.BrandService;
import com.jju.gmall.pms.service.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPmsApplicationTests {

    @Autowired
    ProductService productService;

    @Autowired
    BrandService brandService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
//        Product product = productService.getById(1);
//        System.out.println(product.getName());

        //测试增删改在主库，查在从库
//        Brand brand = new Brand();
//        brand.setName("测试");
//        brandService.save(brand);

        //修改从库的某条数据进行查询，如果是从库的数据则表示ok
        Brand brand1 = brandService.getById(53);
        System.out.println(brand1.getName());
    }

    @Test
    public void test(){
        redisTemplate.opsForValue().set("hello", "world");

        String world = redisTemplate.opsForValue().get("hello");
        System.out.println(world);
    }


}
