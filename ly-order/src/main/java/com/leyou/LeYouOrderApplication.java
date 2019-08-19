package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.leyou.order.mapper")
public class LeYouOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeYouOrderApplication.class);
    }
}
