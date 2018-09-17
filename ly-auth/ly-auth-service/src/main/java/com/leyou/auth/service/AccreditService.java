package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @auther ff
 * @create 2018-08-03 15:42
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AccreditService {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserClient userClient;

    public String authentication(String username, String password) {
        try {
            //查询用户
            User user = userClient.findUserByUsernameAndPassword(username, password);
            if (user==null){
                return null;
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            //生成token返回
            return JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
        }catch (Exception e) {
            return null;
        }

    }
}
