package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @auther ff
 * @create 2018-08-01 20:07
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "ly:user:verify:";

    public Boolean chckData(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return userMapper.selectCount(user) == 0;
    }

    public void sendCode(String phone) {
        //生成验证码
        String code = NumberUtils.generateCode(6);
        //将code存入redis
        String key = KEY_PREFIX + phone;
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
        //发送短信
        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
    }

    public boolean register(User user, String code) {
        //验证code
        String key = KEY_PREFIX + user.getPhone();
        String cachecode = redisTemplate.opsForValue().get(key);
        if (!StringUtils.equals(cachecode,code)){
            return false;
        }
        //code正确加密密码，储存
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //加密密码
        String password = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(password);
        user.setCreated(new Date());
        int i = userMapper.insert(user);
        return i==1;
    }

    public User findUserByUsernameAndPassword(String username, String password) {
        User u = new User();
        u.setUsername(username);
        User user = userMapper.selectOne(u);
        if (user==null){
            return null;
        }
        String dbpass = CodecUtils.md5Hex(password, user.getSalt());
        if (!StringUtils.equals(dbpass,user.getPassword())){
            return null;
        }
        return user;
    }
}
