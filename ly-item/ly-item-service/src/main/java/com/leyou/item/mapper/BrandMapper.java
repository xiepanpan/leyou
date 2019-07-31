package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2019/7/28
 * @Description:  品牌接口层
 */
public interface BrandMapper extends Mapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid")Long cid,@Param("bid")Long bid);

    /**
     * 根据categoryid查询品牌
     * @param cid
     * @return
     */
    @Select("SELECT b.* FROM tb_brand b INNER JOIN tb_category_brand cb ON b.id=cb.brand_id WHERE cb.category_id=#{cid}")
    List<Brand> queryByCategoryId(@Param("cid")Long cid);
}
