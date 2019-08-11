package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 *  @author: xiepanpan
 *  @Date: 2019/8/11 19:46
 *  @Description:  商品详情页
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class LeYouPageController {
    public static void main(String[] args) {
        SpringApplication.run(LeYouPageController.class);
    }
}
