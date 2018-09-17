/*
package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import com.leyou.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*/
/**
 * @auther ff
 * @create 2018-07-29 22:41
 *//*

@Service
public class PageService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger log = LoggerFactory.getLogger(PageService.class);

    private static final String destPath = "E:\\ruanjian\\nginx-1.12.2\\html\\item";

    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> map = new HashMap<>();
        try {
            //1.spu
            Spu spu = goodsClient.querySpuById(spuId);
            map.put("spu", spu);
            //2.spuDetail
            SpuDetail detail = goodsClient.queryDetailBySpuId(spuId);
            map.put("detail", detail);
            //3.sku
            List<Sku> skus = goodsClient.querySkuBySpuId(spuId);
            map.put("skus", skus);
            //4.商品分类
            List<Category> categories = categoryClient.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            map.put("categories", categories);
            //5.品牌
            List<Brand> brands = brandClient.queryByIds(Arrays.asList(spu.getBrandId()));
            map.put("brand", brands.get(0));
            //6.规格参数
            List<SpecGroup> specs = specificationClient.querySpecsByCid(spu.getCid3());
            map.put("specs", specs);
            return map;
        } catch (Exception e) {
            log.error("查询失败spuId" + spuId, e);
            return map;
        }
    }

    public void createHtml(Long id) throws RuntimeException {
        // 创建上下文，
        Context context = new Context();
        // 把数据加入上下文
        context.setVariables(loadModel(id));

        // 创建输出流，关联到一个临时文件
        File temp = new File(id + ".html");
        // 目标页面文件
        File dest = createPath(id);
        // 备份原页面文件
        File bak = new File(id + "_bak.html");
        try (PrintWriter writer = new PrintWriter(temp, "UTF-8")) {
            // 利用thymeleaf模板引擎生成 静态页面
            templateEngine.process("item", context, writer);

            if (dest.exists()) {
                // 如果目标文件已经存在，先备份
                dest.renameTo(bak);
            }
            // 将新页面覆盖旧页面
            FileCopyUtils.copy(temp,dest);
            // 成功后将备份页面删除
            bak.delete();
        } catch (IOException e) {
            // 失败后，将备份页面恢复
            bak.renameTo(dest);
            // 重新抛出异常，声明页面生成失败
            throw new RuntimeException(e);
        } finally {
            // 删除临时页面
            if (temp.exists()) {
                temp.delete();
            }
        }

    }
    private File createPath(Long id) {
        if (id == null) {
            return null;
        }
        File dest = new File(this.destPath);
        if (!dest.exists()) {
            dest.mkdirs();
        }
        return new File(dest, id + ".html");
    }


    public void syncCreateHtml(Long id){
        ThreadUtils.execute(() -> {
            try {
                createHtml(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteHtml(Long spuId) {
        File path = createPath(spuId);
        path.deleteOnExit();
    }
}*/
package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import com.leyou.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-29 10:40
 **/
@Service
public class PageService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specClient;

    private static final Logger logger = LoggerFactory.getLogger(PageService.class);

    private static final String destPath ="E:\\ruanjian\\nginx-1.12.2\\html\\item";
    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {
        // 准备存放数据的容器
        Map<String, Object> map = new HashMap<>();
        try {
            // 1、spu
            Spu spu = goodsClient.querySpuById(spuId);
            map.put("spu", spu);

            // 2、spuDetail
            SpuDetail detail = goodsClient.queryDetailBySpuId(spuId);
            map.put("detail", detail);

            // 3、sku
            List<Sku> skus = goodsClient.querySkuBySpuId(spuId);
            map.put("skus", skus);

            // 4、商品分类
            List<Long> ids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
            List<Category> categories = categoryClient.queryByIds(ids);
            map.put("categories", categories);

            // 5、品牌
            List<Brand> brands = brandClient.queryByIds(Arrays.asList(spu.getBrandId()));
            map.put("brand", brands.get(0));

            // 6、规格参数
            List<SpecGroup> specs = specClient.querySpecsByCid(spu.getCid3());
            map.put("specs", specs);

            return map;
        } catch (Exception e){
            logger.error("加载数据出错。spuId:{}", spuId, e);
            return map;
        }
    }

    public void createHtml(Long id) throws RuntimeException {
        // 创建上下文，
        Context context = new Context();
        // 把数据加入上下文
        context.setVariables(loadModel(id));

        // 创建输出流，关联到一个临时文件
        File temp = new File(id + ".html");
        // 目标页面文件
        File dest = createPath(id);
        // 备份原页面文件
        File bak = new File(id + "_bak.html");
        try (PrintWriter writer = new PrintWriter(temp, "UTF-8")) {
            // 利用thymeleaf模板引擎生成 静态页面
            templateEngine.process("item", context, writer);

            if (dest.exists()) {
                // 如果目标文件已经存在，先备份
                dest.renameTo(bak);
            }
            // 将新页面覆盖旧页面
            FileCopyUtils.copy(temp,dest);
            // 成功后将备份页面删除
            bak.delete();
        } catch (IOException e) {
            // 失败后，将备份页面恢复
            bak.renameTo(dest);
            // 重新抛出异常，声明页面生成失败
            throw new RuntimeException(e);
        } finally {
            // 删除临时页面
            if (temp.exists()) {
                temp.delete();
            }
        }

    }

    public void syncCreateHtml(Long spuId){
        ThreadUtils.execute(() -> createHtml(spuId));
    }

    private File createPath(Long id) {
        if (id == null) {
            return null;
        }
        File dest = new File(this.destPath);
        if (!dest.exists()) {
            dest.mkdirs();
        }
        return new File(dest, id + ".html");
    }

    public void deleteHtml(Long spuId) {
        File file = createPath(spuId);
        file.deleteOnExit();
    }
}
