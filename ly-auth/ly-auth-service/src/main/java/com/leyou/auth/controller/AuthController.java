package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.AccreditService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import com.leyou.common.utils.CookieUtils2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @auther ff
 * @create 2018-08-03 15:01
 */
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AccreditService accreditService;

    @Autowired
    private JwtProperties jwtProperties;

    private static final String COOK_NAME = "LY_TOKEN";

    /*
    * 登录，分发token到cookie
    * */
    @PostMapping("/accredit")
    public ResponseEntity<Void> authentication(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            HttpServletRequest request, HttpServletResponse response
            ){
        //将token写入cookie
       String token =  accreditService.authentication(username,password);
       if (StringUtils.isBlank(token)){
           return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
       }
       //将token写入cookie
        CookieUtils2.newBuilder(request,response).httpOnly().build(COOK_NAME,token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
    * 校验cookie，解析token以及持久，顶部显示用户名
    * */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("LY_TOKEN") String token,
            HttpServletRequest request,HttpServletResponse response
            ){
        //解析token
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            if (userInfo==null){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            //token过期，重新写入
            String token1 = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            CookieUtils2.newBuilder(request,response).httpOnly().build(COOK_NAME,token1);
            //返回UserInfo
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
