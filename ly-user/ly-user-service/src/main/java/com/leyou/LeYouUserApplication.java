package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author: xiepanpan
 * @Date: 2019/7/28
 * @Description: 用户服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.user.mapper")
public class LeYouUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeYouUserApplication.class);
    }
}
