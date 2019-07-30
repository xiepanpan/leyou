package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2019/7/27
 * @Description: 商品品牌service
        */
@Service
public class BrandService {

    /**
     *
     */
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().orLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }
        // 排序
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy+(desc?" DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        List<Brand> brandList = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new LeYouException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //解析分页结果
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);
        return new PageResult<Brand>(pageInfo.getTotal(),brandList);

    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if (count!=1) {
            throw new LeYouException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //新增中间表
        for (Long cid:cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count!=1) {
                throw  new LeYouException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }

    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    public Brand queryById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
