package com.leyou.page.web;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author: xiepanpan
 * @Date: 2019/8/8
 * @Description: 商品详情页
 *
 */
@Controller
public class PageController {

    @Autowired
    PageService pageService;

    /**
     * 跳转到具体的详情页
     * @param model
     * @return
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id")Long spuId, Model model) {
        Map<String,Object> attributes = pageService.loadModel(spuId);
        model.addAllAttributes(attributes);
        return "item";
    }

}
