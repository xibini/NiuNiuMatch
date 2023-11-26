package com.fbs.partnermatch.service;
import java.util.Date;

import com.fbs.partnermatch.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;


    @Test
    void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue(); //操作Redis字符串数据结构的集合
        //增
        valueOperations.set("testString","test");
        valueOperations.set("testInt",1);
        valueOperations.set("testDouble",2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        valueOperations.set("testUser",user);

        //查
        Object testString = valueOperations.get("testString");
        Assertions.assertTrue("test".equals((String)testString));
        Object testInt = valueOperations.get("testInt");
        Assertions.assertTrue(1==(Integer) testInt);
        Object testDouble = valueOperations.get("testDouble");
        Assertions.assertTrue(2.0 == (Double)testDouble);
        System.out.println(valueOperations.get("testUser"));

    }
}
