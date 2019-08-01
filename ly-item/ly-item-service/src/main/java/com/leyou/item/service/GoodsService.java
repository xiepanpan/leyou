package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: xiepanpan
 * @Date: 2019/7/30
 * @Description:  商品业务层
 */
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;


    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title","%"+key+"%");
        }
        //上下架过滤
        if (saleable!=null) {
            criteria.andEqualTo("saleable",saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");

        List<Spu> spus = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)) {
            throw new LeYouException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //解析分类和品牌
        loadCategoryAndBrandName(spus);

        //解析分页结果
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        return new PageResult<>(pageInfo.getTotal(),spus);


    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu:spus) {
            List<String> categoryNames = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            //集合拼接成字符串
            spu.setCname(StringUtils.join(categoryNames,"/"));
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }

    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        int count = spuMapper.insert(spu);
        if (count!=1) {
            throw new LeYouException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

        //新增spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);
        //保存sku和库存
        saveSkuAndStock(spu);


    }

    /**
     * 保存sku和库存
     * @param spu
     */
    private void saveSkuAndStock(Spu spu) {
        int count;//定义库存集合
        List<Stock> stockList = new ArrayList<>();
        //新增sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku:skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            count = skuMapper.insert(sku);
            if (count!=1) {
                throw new LeYouException(ExceptionEnum.GOODS_SAVE_ERROR);
            }

            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        }
        stockMapper.insertList(stockList);
    }


    public SpuDetail queryDetailById(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail==null) {
            throw new LeYouException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        return spuDetail;
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LeYouException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }

        //查询库存 sku赋值库存
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new LeYouException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
        }

        Map<Long,Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId,Stock::getStock));
        skuList.forEach(s->s.setStock(stockMap.get(s.getId())));
        return skuList;
    }

    @Transactional
    public void updateGoods(Spu spu) {
        if (spu.getId()==null) {
            throw new LeYouException(ExceptionEnum.GOODS_ID_CANNOT_BE_NULL);
        }
        //删除sku
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)) {
            //删除sku
            skuMapper.delete(sku);
            //删除stock
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count!=1) {
            throw new LeYouException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //修改detail
        spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        //新增sku和stock
        saveSkuAndStock(spu);
    }
}
