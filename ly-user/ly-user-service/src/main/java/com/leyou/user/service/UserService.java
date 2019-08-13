package com.leyou.user.service;

import com.leyou.LeYouUserApplication;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xiepanpan
 * @Date: 2019/8/13
 * @Description:
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Boolean checkUserData(String data, Integer type) {
        User user = new User();
        //判断数据类型
        switch (type) {
            case 1:
                user.setUsername(data);
            case 2:
                user.setPhone(data);
            default:
                throw new LeYouException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        return userMapper.selectCount(user)==0;
    }
}
