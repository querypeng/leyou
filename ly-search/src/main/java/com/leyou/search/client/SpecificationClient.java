package com.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-26 09:59
 **/
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
