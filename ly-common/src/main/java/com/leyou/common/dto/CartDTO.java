package com.leyou.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: xiepanpan
 * @Date: 2019/8/20
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    /**
     * 商品skuId
     */
    private Long skuId;
    /**
     * 购买数量
     */
    private Integer num;
}
