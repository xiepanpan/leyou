package com.leyou.order.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDTO;
import com.leyou.order.dto.CartDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: xiepanpan
 * @Date: 2019/8/20
 * @Description:
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;

    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        //新增订单
        Order order = new Order();
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());

        //用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        //是否已评价
        order.setBuyerRate(false);

        //收货人地址
        AddressDTO addressDTO = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addressDTO.getName());
        order.setReceiverAddress(addressDTO.getAddress());
        order.setReceiverCity(addressDTO.getCity());
        order.setReceiverDistrict(addressDTO.getDistrict());
        order.setReceiverMobile(addressDTO.getPhone());
        order.setReceiverState(addressDTO.getState());
        order.setReceiverZip(addressDTO.getZipCode());

        //计算金额
        //把CartDTO转化成map key为skuId value为sku数目
        Map<Long, Integer> numMap = orderDTO.getCarts().stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        Set<Long> skuIds = numMap.keySet();
        List<Sku> skuList = goodsClient.querySkuByIds(new ArrayList<Long>(skuIds));

        List<OrderDetail> orderDetailList = new ArrayList<>();

        long totalPay = 0L;
        for (Sku sku: skuList) {
            totalPay+= sku.getPrice()*numMap.get(sku.getId());

            //封装orderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            orderDetail.setNum(numMap.get(sku.getId()));
            orderDetail.setOrderId(orderId);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            orderDetailList.add(orderDetail);
        }

        order.setTotalPay(totalPay);
        //实付金额= 总金额+邮费-优惠金额
        order.setActualPay(totalPay+order.getPostFee()-0);
        //订单写入库
        int count = orderMapper.insertSelective(order);
        if (count!=1) {
            log.error("[创建订单] 创建订单失败,orderId:{}",orderId);
            throw new LeYouException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        count = orderDetailMapper.insertList(orderDetailList);
        if (count!=orderDetailList.size()) {
            log.error("[创建订单] 创建订单失败,orderId:{}",orderId);
            throw new LeYouException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count!=1) {
            log.error("[创建订单] 创建订单失败,orderId:{}",orderId);
            throw new LeYouException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        //减库存

        return null;
    }
}
