package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @auther ff
 * @create 2018-08-03 16:39
 */
@FeignClient("user-service")
public interface UserClient extends UserApi{
}
