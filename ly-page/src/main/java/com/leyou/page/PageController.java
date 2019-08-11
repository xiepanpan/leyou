package com.leyou.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: xiepanpan
 * @Date: 2019/8/8
 * @Description: 商品详情页
 *
 */
@Controller
public class PageController {

    /**
     * 跳转到具体的详情页
     * @param model
     * @return
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id")Long spuId, Model model) {
        model.addAttribute("msg","hello thymeleaf");
        return "item";
    }

}
