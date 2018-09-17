package com.leyou.search.service;

import com.leyou.LySearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class IndexServiceTest {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private IndexService indexService;

    @Test
    public void testCreateIndex() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    @Test
    public void loadData() {
        // 分页数据
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询spu
            PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, null, true);
            List<Spu> spus = result.getItems();

            List<Goods> goodsList = new ArrayList<>();

            for (Spu spu : spus) {
                Goods goods = indexService.buildGoods(spu);
                goodsList.add(goods);
            }

            goodsRepository.saveAll(goodsList);

            size = spus.size();
            page++;
        } while (size == 100);
    }
}