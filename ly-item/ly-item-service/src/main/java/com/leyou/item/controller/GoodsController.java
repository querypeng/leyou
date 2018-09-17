package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-22 11:56
 **/
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value="page", defaultValue = "1") Integer page,
            @RequestParam(value="rows", defaultValue = "5") Integer rows,
            @RequestParam(value="key", required = false) String key,
            @RequestParam(value="saleable", required = false) Boolean saleable){
        PageResult<Spu> result = goodsService.querySpuByPage(page,rows,key,saleable);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        // TODO 数据校验
        goodsService.saveGoods(spu);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        // TODO 数据校验
        if(spu.getId() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        goodsService.updateGoods(spu);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 根据spu的id查询详情
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> queryDetailBySpuId(@PathVariable("spuId")Long spuId){
        SpuDetail detail = goodsService.queryDetailBySpuId(spuId);
        if(detail == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(detail);
    }

    /**
     * 根据spu查询所有sku
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId){
        List<Sku> list = goodsService.querySkuBySpuId(spuId);
        if(CollectionUtils.isEmpty(list)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
    @GetMapping("spu/{spuId}")
    public ResponseEntity<Spu> querySpuById(@RequestParam("spuId")Long spuId){
            Spu spu = goodsService.querySpuById(spuId);
            if (spu==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        return ResponseEntity.ok(spu);
    }
}
