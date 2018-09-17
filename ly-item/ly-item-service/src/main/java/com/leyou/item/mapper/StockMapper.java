package com.leyou.item.mapper;

import com.leyou.item.pojo.Stock;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-23 10:57
 **/
public interface StockMapper extends Mapper<Stock>, InsertListMapper<Stock>, DeleteByIdListMapper<Stock, Long> {
}
