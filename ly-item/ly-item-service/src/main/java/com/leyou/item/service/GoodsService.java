package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-22 11:56
 **/
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper detailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, String key, Boolean saleable) {
        // 分页
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 1、过滤key
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        // 2、过滤saleable
        if(saleable != null){
            criteria.andEqualTo("saleable", saleable);
        }
        // 3、过滤已删除商品
        criteria.andEqualTo("valid", true);
        // 查询
        List<Spu> list = spuMapper.selectByExample(example);

        // 处理商品分类和品牌名称
        handleCategoryAndBrandName(list);

        // 封装结果
        PageInfo<Spu> info = new PageInfo<>(list);
        return new PageResult<>(info.getTotal(), list);
    }

    private void handleCategoryAndBrandName(List<Spu> list) {
        for (Spu spu : list) {
            // 查询分类名称
            List<String> names = categoryService.queryCategoryByIds(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).stream()
                    .map(c -> c.getName()).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/"));

            // 查询品牌名称
            Brand brand = brandService.queryById(spu.getBrandId());
            spu.setBname(brand.getName());// TODO 设置品牌名称
        }
    }

    @Transactional
    public void saveGoods(Spu spu) {
        try{
            // 补全SPU数据
            spu.setId(null);
            spu.setSaleable(true);
            spu.setValid(true);
            spu.setCreateTime(new Date());
            spu.setLastUpdateTime(spu.getCreateTime());
            // 新增SPU
            int count = spuMapper.insert(spu);
            if(count == 0){
                throw new RuntimeException("新增spu失败!");
            }
            // 新增SpuDetail
            SpuDetail detail = spu.getSpuDetail();
            detail.setSpuId(spu.getId());
            count = detailMapper.insert(detail);
            if(count == 0){
                throw new RuntimeException("新增spuDetail失败!");
            }
            // 新增sku和库存
            insertSkuAndStock(spu);
            //rabbitmq发送消息
            amqpTemplate.convertAndSend("item.insert","spu.getId");
        }catch (Exception e){
            throw new RuntimeException("新增商品失败");
        }
    }

    private void insertSkuAndStock(Spu spu) {
        int count;// 新增Sku
        List<Sku> skus = spu.getSkus();
        // 准备库存对象集合
        List<Stock> stocks = new ArrayList<>();
        for (Sku sku : skus) {
            // 补全sku数据
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            count = skuMapper.insert(sku);
            if(count == 0){
                throw new RuntimeException("新增sku失败!");
            }
            // 准备stock对象
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        }
        // 新增stock
        count = stockMapper.insertList(stocks);
        if(count < stocks.size()){
            throw new RuntimeException("新增stock失败!");
        }
    }

    public SpuDetail queryDetailBySpuId(Long spuId) {
        return detailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku t = new Sku();
        t.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(t);
        // 查询库存
        for (Sku sku : skus) {
            Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
            if(stock != null) {
                sku.setStock(stock.getStock());
            }
        }
        return skus;
    }

    @Transactional
    public void updateGoods(Spu spu) {
        try{
            // 修改spu
            spu.setValid(null);
            spu.setCreateTime(null);
            spu.setSaleable(null);
            spu.setLastUpdateTime(new Date());
            spuMapper.updateByPrimaryKeySelective(spu);
            // 修改spuDetail
            detailMapper.updateByPrimaryKey(spu.getSpuDetail());
            // 删除sku和stock
            // 查询以前的sku
            List<Sku> skus = querySkuBySpuId(spu.getId());
            if(CollectionUtils.isNotEmpty(skus)) {
                List<Long> ids = skus.stream().map(sku -> sku.getId()).collect(Collectors.toList());
                skuMapper.deleteByIdList(ids);
                stockMapper.deleteByIdList(ids);
            }
            // 新增sku和stock
            insertSkuAndStock(spu);

            //发送rabbit消息
            amqpTemplate.convertAndSend("item.update","spu.getId");
        }catch (Exception e){
            throw new RuntimeException("修改商品失败！", e);
        }
    }

    public Spu querySpuById(Long spuId) {
        return spuMapper.selectByPrimaryKey(spuId);
    }
}
