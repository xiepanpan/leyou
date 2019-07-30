package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author: xiepanpan
 * @Date: 2019/7/27
 * @Description:
 */
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {
}
