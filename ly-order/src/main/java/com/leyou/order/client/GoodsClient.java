package com.leyou.order.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: xiepanpan
 * @Date: 2019/8/21
 * @Description:
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
