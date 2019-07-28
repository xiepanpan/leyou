package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2019/7/27
 * @Description: 商品分类service
 */
@Service
public class CategoryService {

    /**
     *
     */
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByPid(Long pid) {
        //mapper 把非空属性作为查询条件
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categoryList = categoryMapper.select(category);
        //
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LeYouException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categoryList;

    }
}
