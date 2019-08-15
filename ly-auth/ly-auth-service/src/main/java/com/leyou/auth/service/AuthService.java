package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.test.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xiepanpan
 * @Date: 2019/8/15
 * @Description:
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProperties;

    public String login(String username, String password) {

        try {
            User user = userClient.queryUserByUsernameAndPwd(username, password);
            if (user == null) {
                throw new LeYouException(ExceptionEnum.INVALID_USERNAME_PASSWORD);

            }
            //私钥签名 生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username), jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            log.error("[授权中心] 用户名或密码错误，用户名称:{}",username,e);
            throw new LeYouException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }
}
