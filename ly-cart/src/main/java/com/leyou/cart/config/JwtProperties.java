package com.leyou.cart.config;

import com.leyou.auth.test.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author: xiepanpan
 * @Date: 2019/8/15
 * @Description:  读取配置文件到类属性中
 */
@Data
@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {

    private String pubKeyPath;
    /**
     * 公钥
     */
    private PublicKey publicKey;

    private String cookieName;
    /**
     * 对象一旦实例化 就应该读取到公钥和私钥
     */
    @PostConstruct
    public void init() throws Exception {
        //读取公钥
        this.publicKey= RsaUtils.getPublicKey(pubKeyPath);
    }

}
