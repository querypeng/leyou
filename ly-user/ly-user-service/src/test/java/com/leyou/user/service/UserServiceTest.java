package com.leyou.user.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @auther ff
 * @create 2018-08-02 17:13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void da(){
        redisTemplate.opsForValue().set("ly:user:verify:15156922483","122");
        String s = redisTemplate.opsForValue().get("ly:user:verify:15156922483");
        System.out.println("s = " + s);
    }

}