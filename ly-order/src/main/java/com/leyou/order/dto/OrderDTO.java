package com.leyou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2019/8/20
 * @Description:  dataTransferObject  数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    /**
     * 收货人地址id
     */
    @NotNull
    private Long addressId;

    /**
     * 付款方式
     */
    @NotNull
    private Integer paymentType;

    /**
     * 订单详情
     */
    @NotNull
    private List<CartDTO> carts;
}
