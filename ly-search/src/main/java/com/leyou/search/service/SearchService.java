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
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
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
@Slf4j
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
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

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

    public SearchResult search(SearchRequest searchRequest) {

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
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("all", searchRequest.getKey());
        QueryBuilder basicQuery = buildBasicQuery(searchRequest);
        nativeSearchQueryBuilder.withQuery(basicQuery);
        //3 商品分类和品牌
        // 3.1 聚合分类
        String categoryAggName = "category_agg";
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 3.2 聚合品牌
        String brandAggName = "brand_agg";
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 4 查询
        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);

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

        //5 解析聚合结果
        Aggregations aggregations = result.getAggregations();
        List<Category> categorys = parseCategoryAgg(aggregations.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggregations.get(brandAggName));

        //6 规格参数的聚合
        List<Map<String,Object>> specs = null;
        if (categorys!=null&& categorys.size()==1) {
            specs =buildSpecificationAgg(categorys.get(0).getId(),basicQuery);
        }

        return new SearchResult(total, totalPage,goodsList,categorys,brands,specs);

    }

    /**
     * 条件过滤
     * @param searchRequest
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest searchRequest) {
        //创建布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()));
        //过滤条件
        Map<String, String> map = searchRequest.getFilter();
        for (Map.Entry<String,String> entry:map.entrySet()) {
            String key = entry.getKey();
            if (!"cid3".equals(key)&&!"brandId".equals(key)) {
                key = "specs."+key+".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 聚合规格参数
     * @param cid
     * @param matchQueryBuilder
     * @return
     */
    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder matchQueryBuilder) {
        List<Map<String,Object>> specs = new ArrayList<>();
        //查询需要聚合的规格参数
        List<SpecParam> specParams = specificationClient.querySpecificationByCategoryId(null, cid, true);
        // 聚合
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(matchQueryBuilder);
        //集合
        for (SpecParam specParam:specParams) {
            String name = specParam.getName();
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }
        //获取结果
        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
        //解析结果
        Aggregations aggregations = result.getAggregations();
        for (SpecParam specParam:specParams) {
            String name = specParam.getName();
            Aggregation aggregation = aggregations.get(name);
            StringTerms stringTerms = (StringTerms)aggregations.get(name);
            List<String> options = stringTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            //准备map
            Map<String,Object> map = new HashMap<>();
            map.put("k",name);
            map.put("options",options);
            specs.add(map);
        }

        return specs;
    }

    private List<Brand> parseBrandAgg(LongTerms longTerms) {
        try {
            List<Long> ids = longTerms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        } catch (Exception e) {
            log.error("查询品牌异常",e);
            return null;
        }
    }

    private List<Category> parseCategoryAgg(LongTerms longTerms) {
        try {
            List<Long> ids = longTerms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        } catch (Exception e) {
            return null;
        }
    }

    public void createOrUpdateIndex(Long spuId) {
        Spu spu = goodsClient.querySpuById(spuId);
        //构建goods
        Goods goods = buildGoods(spu);
        //存入索引库
        goodsRepository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
