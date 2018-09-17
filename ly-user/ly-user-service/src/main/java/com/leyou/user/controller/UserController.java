package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import com.netflix.ribbon.proxy.annotation.Http;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @auther ff
 * @create 2018-08-01 20:06
 */
@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;

    /*
    * 实现用户数据的校验，主要包括对：手机号、用户名、邮箱的唯一性校验。
    * */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(
            @PathVariable(value = "data")String data,
            @PathVariable(value = "type",required = false)Integer type){
        if (type==null){
           type=1;
        }
        Boolean bool = userService.chckData(data,type);
        if (bool==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(bool);
    }

    /*
    * 发送短信验证码
    * */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam(value = "phone",required = true)String phone){
        String regx = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        if (phone.matches(regx)){
            userService.sendCode(phone);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /*
    * 注册
    * */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code")String code){
        //简单的校验手机号的长度，是否符合规则
        String regx = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        if (StringUtils.length(user.getPhone())!=11 || !user.getPhone().matches(regx)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
       boolean bool =  userService.register(user,code);
       if (!bool){
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }
       return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*
    * 查询功能，根据参数中的用户名和密码查询指定用户
    * */
    @GetMapping("/query")
    public ResponseEntity<User> findUserByUsernameAndPassword(
            @RequestParam("username")String username,
            @RequestParam("password")String password){
        User user = userService.findUserByUsernameAndPassword(username,password);
        if (user==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(user);
    }
}
