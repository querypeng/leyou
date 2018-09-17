package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @auther ff
 * @create 2018-08-02 19:49
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;
    @Test
    public void findUserByUsernameAndPassword() {
        ResponseEntity<User> user = userController.findUserByUsernameAndPassword("tom123", "tom123");
        System.out.println("user = " + user);
    }
}