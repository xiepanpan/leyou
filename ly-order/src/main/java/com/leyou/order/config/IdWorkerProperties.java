package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xiepanpan
 * @Date: 2019/8/20
 * @Description:  id生成器配置属性
 */
@Data
@ConfigurationProperties(prefix = "leyou.worker")
public class IdWorkerProperties {

    /**
     * 当前机器id
     */
    private long workerId;

    /**
     * 序列号
     */
    private long dataCenterId;

}
