package com.leyou.item.service;

import com.leyou.item.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 *  @author: xiepanpan
 *  @Date: 2019/7/26
 *  @Description: 商品service
 */
@Service
public class ItemService {

    /**
     * 商品新增
     * @param item
     * @return
     */
    public Item saveItem(Item item) {
        int id = new Random().nextInt(100);
        item.setId(id);
        return item;
    }
}
