package com.leyou.item.mapper;

import com.leyou.item.pojo.Sku;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-23 10:57
 **/
public interface SkuMapper extends Mapper<Sku>, DeleteByIdListMapper<Sku,Long> {
}
