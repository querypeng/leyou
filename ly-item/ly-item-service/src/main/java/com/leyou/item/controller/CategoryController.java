package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-19 10:18
 **/
@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父id查询子类目
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryByParentId(@RequestParam("pid")Long pid){
        List<Category> list = this.categoryService.queryByParentId(pid);
        if(list == null || list.size() < 1){
            // 没有查到，返回404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryByIds(@RequestParam("ids")List<Long> ids){
        List<Category> list = this.categoryService.queryCategoryByIds(ids);
        if(list == null || list.size() < 1){
            // 没有查到，返回404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
