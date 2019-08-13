package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xiepanpan
 * @Date: 2019/8/13
 * @Description:  短信服务配置类
 */
@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String vertifyCodeTemplate;
}
