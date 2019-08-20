package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2019/8/4
 * @Description:  商品类接口
 */
public interface GoodsApi {


    /**
     * 根据spu id 查询详情detail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id")Long spuId);

    /**
     * 根据spu查询下面所有的sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/{id}")
    List<Sku> querySkuBySpuId(@RequestParam("id")Long spuId);

    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable",required =false)Boolean saleable,
            @RequestParam(value = "key", required= false)String key
    );

    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id")Long id);

    /**
     * 根据skuid查询sku信息
     * @param ids
     * @return
     */
    @GetMapping("sku/list/{ids}")
    List<Sku> querySkuByIds(@RequestParam("ids")List<Long> ids);
}
