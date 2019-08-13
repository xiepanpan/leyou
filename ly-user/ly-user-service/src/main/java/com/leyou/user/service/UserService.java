package com.leyou.user.service;

import com.leyou.LeYouUserApplication;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiepanpan
 * @Date: 2019/8/13
 * @Description:
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify:phone:";

    public Boolean checkUserData(String data, Integer type) {
        User user = new User();
        //判断数据类型
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new LeYouException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        return userMapper.selectCount(user) == 0;
    }

    public void sendCode(String phone) {
        //生成key
        String key =KEY_PREFIX+phone;
        //生成验证码
        String code = NumberUtils.generateCode(6);
        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        //发送验证码
        amqpTemplate.convertAndSend("leyou.sms.exchange","sms.verify.code",msg);
        //保存验证码到Redis中
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
    }
}
