package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2019/7/31
 * @Description: 规格参数
 */
@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid")Long cid) {
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 查询商品分类对应的规格参数模板
     * @param cid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecificationByCategoryId(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching
    ){
        return ResponseEntity.ok(specificationService.queryParmList(gid,cid,searching));
    }

    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid")Long cid) {
        return ResponseEntity.ok(specificationService.queryListByCid(cid));
    }
}
