package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiepanpan
 * @Date: 2019/8/12
 * @Description:
 */
@Service
@Slf4j
public class PageService {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    CategoryClient categoryClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 准备商品详情页的数据
     * @param spuId
     * @return
     */
    public Map<String, Object> loadModel(Long spuId) {
        Map<String,Object> map = new HashMap<>();
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询skus
        List<Sku> skus = spu.getSkus();
        //查询详情
        SpuDetail detail = spu.getSpuDetail();
        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询商品分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格参数
        List<SpecGroup> specGroups = specificationClient.queryGroupByCid(spu.getCid3());
        map.put("spu",spu);
        map.put("skus",skus);
        map.put("detail",detail);
        map.put("brand",brand);
        map.put("categories",categories);
        map.put("specs",specGroups);
        return map;
    }

    public void createHtml(Long spuId) {
        //上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        //输出流
        File dest = new File("D:\\project\\leyou\\upload",spuId+".html");

        if (dest.exists()) {
            dest.delete();
        }

        try (PrintWriter writer = new PrintWriter(dest,"UTF-8")){
            templateEngine.process("item",context,writer);
        }catch (Exception e) {
            log.error("[静态页服务] 生成静态页异常!",e);
        }
    }

    public void deleteHtml(Long spuId) {
        File dest = new File("D:\\project\\leyou\\upload",spuId+".html");

        if (dest.exists()) {
            dest.delete();
        }
    }
}
