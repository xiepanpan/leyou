package com.leyou.order.enums;

/**
 * 订单状态枚举类
 */
public enum OrderStatusEnum {
    UN_PAY(1,"未付款"),
    PAYED(2,"已付款,未发货"),
    DELIVERED(3,"已发货,未确认"),
    SUCCESS(4,"交易成功"),
    CLOSED(5,"交易关闭"),
    RATED(6,"已评价"),
    ;
    private int code;
    /**
     * 说明 description
     */
    private String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public int value() {
        return this.code;
    }
}
