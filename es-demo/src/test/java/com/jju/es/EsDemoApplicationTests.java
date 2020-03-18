package com.jju.es;

import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsDemoApplicationTests {

    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() {
        System.out.println(jestClient);
    }

    @Test
    public void add() throws IOException {
        User user = new User();
        user.setEmail("123@qq.com1");
        user.setUsername("admin1");

        Index index = new Index.Builder(user)
                .index("user")
                .type("info").build();

        DocumentResult execute = jestClient.execute(index);

        System.out.println("执行了"+ execute.getId() + "===>" + execute.isSucceeded());
    }

    @Test
    public void query() throws IOException {
        String queryJson = "{\n" +
                "  \"query\": {\"match_all\": {}}\n" +
                "}";
        Search search = new Search.Builder(queryJson).addIndex("user")
                .build();

        SearchResult execute = jestClient.execute(search);

        System.out.println("总记录数" + execute.getTotal() + "===最大得分：" + execute.getMaxScore());
        System.out.println("查到的数据：");
        List<SearchResult.Hit<User, Void>> hits = execute.getHits(User.class);
        hits.forEach(userVoidHit -> {
            User user = userVoidHit.source;
            System.out.println(user.getEmail() + "==>" + user.getUsername());
        });
    }

}

class User{
    private String username;
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
