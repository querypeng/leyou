package com.leyou.service;

import com.leyou.Filter.UserInterceptor;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther ff
 * @create 2018-08-07 12:16
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "ly:cart:user:";

    public void addCart(Cart cart) {
        //获取用户
        UserInfo userInfo = UserInterceptor.getUser();
        //定义redis的key
        String Userkey = KEY_PREFIX + userInfo.getId();
        //获取redis的hash操作对象,绑定用户id到redis的key   <useId <skuId,value>> 结构
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(Userkey);

        //页面传过来的数量
        Integer cartNum = cart.getNum();
        //SkuId
        String hashkey = cart.getSkuId().toString();

        //判断是否存在这个商品
        if (hashOps.hasKey(hashkey)){
            //存在就修改数量
            String json = hashOps.get(hashkey).toString();
            cart = JsonUtils.parse(json, Cart.class);
            cart.setNum(cartNum + cart.getNum());
        }else{
            // 不存在，新增
            cart.setUserId(userInfo.getId());
        }
        // 写入Redis
        hashOps.put(hashkey, JsonUtils.serialize(cart));

    }

    public List<Cart> queryCarts() {
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PREFIX + user.getId();
        if (!redisTemplate.hasKey(key)){
            return null;
        }
       return redisTemplate.opsForHash().values(key).stream().map(o -> JsonUtils.parse(o.toString(),Cart.class)).collect(Collectors.toList());
    }
}
