package com.leyou.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2019/8/18
 * @Description:
 */
@Data
@ConfigurationProperties("leyou.filter")
public class FilterProperties {

    private List<String> allowPaths;
}
