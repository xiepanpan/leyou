package com.leyou.auth.config;

import com.leyou.auth.test.RsaUtils;
import lombok.Data;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author: xiepanpan
 * @Date: 2019/8/15
 * @Description:  读取配置文件到类属性中
 */
@Data
@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {

    private String secret;

    private String pubKeyPath;
    private String priKeyPath;
    private int expire;
    /**
     * 公钥
     */
    private PublicKey publicKey;
    /**
     * 私钥
     */
    private PrivateKey privateKey;

    private String cookieName;
    private Integer cookieMaxAge;

    /**
     * 对象一旦实例化 就应该读取到公钥和私钥
     */
    @PostConstruct
    public void init() throws Exception {
        File publicPath = new File(pubKeyPath);
        File priPath = new File(priKeyPath);
        if (!publicPath.exists()||!priPath.exists()) {
            //生成公钥和私钥
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
        }
        //读取公钥私钥
        this.publicKey= RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey=RsaUtils.getPrivateKey(priKeyPath);
    }

}
