package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-28 10:09
 **/
public interface BrandApi {

    @GetMapping("brand/list")
    List<Brand> queryByIds(@RequestParam("ids")List<Long> ids);
}
