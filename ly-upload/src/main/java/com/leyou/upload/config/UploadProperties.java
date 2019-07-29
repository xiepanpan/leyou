package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2019/7/30
 * @Description:  自定义属性对应的实体类
 */
@Data
@ConfigurationProperties(prefix = "leyou.upload")
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;
}
