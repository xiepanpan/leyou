package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: xiepanpan
 * @Date: 2019/8/18
 * @Description:
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //用户购物车信息Redis前缀
    private static final String KEY_PREFIX = "cart:userid:";


    /**
     * 添加购物车 存储方式 使用 Redis的map类型 key field value
     * @param cart
     */
    public void addCart(Cart cart) {
        UserInfo userInfo = UserInterceptor.getUser();
        String key = KEY_PREFIX+userInfo.getId();
        String hashKey = cart.getSkuId().toString();
        //记录原先数量
        Integer num = cart.getNum();
        //所有的field
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        if (operations.hasKey(hashKey)) {
            String json = operations.get(hashKey).toString();
            cart = JsonUtils.parse(json, Cart.class);
            cart.setNum(cart.getNum()+num);
        }
        operations.put(hashKey,JsonUtils.serialize(cart));
    }

    public List<Cart> queryCartList() {
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PREFIX+user.getId();
        if (!redisTemplate.hasKey(key)) {
            //key不存在 返回购物车为空
            throw  new LeYouException(ExceptionEnum.CART_NOT_FOUND);
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Cart> list = operations.values().stream()
                .map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());

        return list;
    }

    public void updateCartNum(Long skuId, Integer num) {
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PREFIX+user.getId();
        String hashKey = skuId.toString();
        //获取操作
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        //判断是否存在
        if (!operations.hasKey(hashKey)) {
            throw new LeYouException(ExceptionEnum.CART_NOT_FOUND);
        }

        //查询购物车
        Cart cart = JsonUtils.parse(operations.get(hashKey).toString(), Cart.class);
        cart.setNum(num);
        //写回Redis
        operations.put(hashKey,JsonUtils.serialize(cart));
    }

    public void deleteCart(Long skuId) {
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PREFIX+user.getId();
        //删除
        redisTemplate.opsForHash().delete(key,skuId.toString());
    }
}
