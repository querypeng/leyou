package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-22 09:55
 **/
@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup t = new SpecGroup();
        t.setCid(cid);
        return groupMapper.select(t);
    }

    public List<SpecParam> queryParamByGid(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam t = new SpecParam();
        t.setGroupId(gid);
        t.setCid(cid);
        t.setGeneric(generic);
        t.setSearching(searching);
        return paramMapper.select(t);
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
        SpecGroup t = new SpecGroup();
        t.setCid(cid);
        //根据规格组id查询组,集合
        List<SpecGroup> groups = groupMapper.select(t);
        //遍历集合取出每个组
        for (SpecGroup group : groups) {
            //组参数对象中塞进组id
            SpecParam param = new SpecParam();
            param.setGroupId(group.getId());
            //根据参数id查询所有组参数详情
            List<SpecParam> specParamList = paramMapper.select(param);
            group.setParams(specParamList);
        }
        return groups;
    }
}
