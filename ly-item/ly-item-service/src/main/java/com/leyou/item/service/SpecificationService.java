package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiepanpan
 * @Date: 2019/7/31
 * @Description: 规格参数service
 */
@Service
public class SpecificationService {

    @Autowired
    private SpecParamMapper specParamMapper;
    @Autowired
    private SpecGroupMapper specGroupMapper;


    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)) {
            throw new LeYouException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    public List<SpecParam> queryParmList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> list = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)) {
            throw new LeYouException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        List<SpecGroup> specGroups = queryGroupByCid(cid);
        //查询当前分类的参数
        List<SpecParam> specParams = queryParmList(null, cid, null);

        //先把规格参数变为map map的key为规格组id 值为组下所有参数
        Map<Long,List<SpecParam>> map = new HashMap<>();
        for (SpecParam specParam:specParams) {
            if (!map.containsKey(specParam.getGroupId())) {
                //组id在map中不存在 就新增一个list
                map.put(specParam.getGroupId(),new ArrayList<>());
            }
            map.get(specParam.getGroupId()).add(specParam);
        }

        //填充param到group
        for (SpecGroup specGroup:specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }
}
