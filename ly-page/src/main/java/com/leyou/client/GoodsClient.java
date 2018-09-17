package com.leyou.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-26 09:57
 **/
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
