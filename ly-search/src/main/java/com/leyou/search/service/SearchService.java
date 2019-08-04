package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: xiepanpan
 * @Date: 2019/8/4
 * @Description:
 */
@Service
public class SearchService {

    @Autowired
    CategoryClient categoryClient;
    @Autowired
    BrandClient brandClient;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    SpecificationClient specificationClient;
    @Autowired
    GoodsRepository goodsRepository;

    /**
     * 构建商品索引库
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu) {
        Long spuId = spu.getId();
        //拼接搜索字段
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)) {
            throw new LeYouException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<Object> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand==null) {
            throw new LeYouException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        String all = spu.getTitle()+ StringUtils.join(names," ")+brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LeYouException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //对sku处理
        List<Map<String,Object>> skus = new ArrayList<>();
        //价格集合
        Set<Long> priceList = new HashSet<>();
        for (Sku sku:skuList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //取sku的第一张图片
            map.put("image",StringUtils.substringBefore(sku.getImages(),","));
            skus.add(map);
            // 处理价格
            priceList.add(sku.getPrice());
        }

        //查询规格参数
        List<SpecParam> params = specificationClient.querySpecificationByCategoryId(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)) {
            throw new LeYouException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查看商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        ////获取通用规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        //规格参数 把key为规格参数的名字 value为规格参数的值
        Map<String,Object> specs = new HashMap<>();
        for (SpecParam specParam:params) {
            String key = specParam.getName();
            Object value ="";
            //判断是否为通用规格属性
            if (specParam.getGeneric()) {
                value = genericSpec.get(specParam.getId());
                if (specParam.getNumeric()) {
                    //处理成段
                    System.out.println(value);
                    if (value!=null) {
                        value = chooseSegment(value.toString(),specParam);
                    }
                }
            }else {
                value = specialSpec.get(specParam.getId());
            }
            specs.put(key,value);
        }

        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid1());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spuId);
        //设置搜索字段
        goods.setAll(all);
        //所有sku价格的集合
        goods.setPrice(priceList);
        goods.setSkus(JsonUtils.serialize(skus));
        //所有可搜索的规格参数
        goods.setSpecs(specs);
        goods.setSubTitle(spu.getSubTitle());
        return goods;

    }

    /**
     * 数值转化成段 来放到索引中
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest searchRequest) {

        int page = searchRequest.getPage()-1;
        int size = searchRequest.getSize();

//        Pageable pageable = new PageRequest(page,size);
        //创建查询构造器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //0 结果过滤  只展示这几个字段的数据
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //1 分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page,size));
        //2 过滤
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("all",searchRequest.getKey()));
        // 3 查询
        Page<Goods> result = goodsRepository.search(nativeSearchQueryBuilder.build());

        //总条数
        long total = result.getTotalElements();
        //总页数
//        int totalPage = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //总页数
        Integer pageSize = searchRequest.getSize();
        int totalPage = 0;
        if (total%pageSize==0) {
            totalPage = (int) (total/pageSize);
        }else {
            totalPage = (int) (total/pageSize+1);
        }
        return new PageResult(total, totalPage,goodsList);

    }
}
