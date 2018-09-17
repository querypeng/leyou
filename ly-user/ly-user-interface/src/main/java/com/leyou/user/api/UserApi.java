package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @auther ff
 * @create 2018-08-03 15:48
 */
public interface UserApi {
    @GetMapping("/query")
    User findUserByUsernameAndPassword(
            @RequestParam("username")String username,
            @RequestParam("password")String password);
}
