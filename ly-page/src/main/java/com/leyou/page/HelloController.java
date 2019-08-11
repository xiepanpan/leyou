package com.leyou.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: xiepanpan
 * @Date: 2019/8/8
 * @Description:
 *
 */
@Controller
public class HelloController {

    @GetMapping("hello")
    public String toHello(Model model) {
        model.addAttribute("msg","hello thymeleaf");
        return "hello";
    }

}
