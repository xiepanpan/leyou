package com.leyou.order.dto;

import lombok.Data;

/**
 * @author: xiepanpan
 * @Date: 2019/8/20
 * @Description:
 */
@Data
public class AddressDTO {
    private Long id;
    /**
     * 收货人姓名
     */
    private String name;
    /**
     * 电话
     */
    private String phone;
    /**
     * 省份
     */
    private String state;
    /**
     * 城市
     */
    private String city;
    /**
     * 区
     */
    private String district;
    /**
     * 街道
     */
    private String address;
    /**
     * 邮编
     */
    private String zipCode;
    /**
     * 是否默认
     */
    private Boolean isDefault;
}
