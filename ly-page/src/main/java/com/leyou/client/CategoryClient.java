package com.leyou.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-26 09:38
 **/
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi{
}
