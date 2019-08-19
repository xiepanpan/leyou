package com.leyou.order.service;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xiepanpan
 * @Date: 2019/8/20
 * @Description:
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    public Long createOrder(OrderDTO orderDTO) {
        return null;
    }
}
