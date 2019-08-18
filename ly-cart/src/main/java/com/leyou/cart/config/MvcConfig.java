package com.leyou.cart.config;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: xiepanpan
 * @Date: 2019/8/18
 * @Description:
 */
@EnableConfigurationProperties(JwtProperties.class)
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor(jwtProperties)).addPathPatterns("/**");
    }
}
