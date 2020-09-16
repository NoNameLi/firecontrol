package cn.turing.firecontrol.admin.biz;

import cn.turing.firecontrol.admin.entity.Channel;
import cn.turing.firecontrol.admin.entity.ChannelTenant;
import cn.turing.firecontrol.admin.mapper.ChannelMapper;
import cn.turing.firecontrol.admin.mapper.ChannelTenantMapper;
import cn.turing.firecontrol.admin.vo.ChannelDto;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import com.ace.cache.annotation.CacheClear;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Service
public class ChannelBiz extends BusinessBiz<ChannelMapper,Channel> {

    @Autowired
    private ChannelTenantMapper channelTenantMapper;

    public List<Map<String,Object>> tree(String tenantId){
        List<Map<String,Object>> channels = mapper.queryAll(tenantId);
        Map<String,List<Map<String,Object>>> map = new HashMap<String,List<Map<String,Object>>>();
        for(Map<String,Object> c : channels){
            String type = (String)c.get("channelType");
            List<Map<String,Object>> cs = map.get(type);
            if( cs== null){
                cs = new ArrayList<Map<String,Object>>();
            }
            cs.add(c);
            map.put(type,cs);
        }
        List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
        Set<Map.Entry<String,List<Map<String,Object>>>> entrySet = map.entrySet();
        Map<String,Object> map1;
        for(Map.Entry<String,List<Map<String,Object>>> e : entrySet){
            map1 = new HashMap<String,Object>();
            map1.put("type",e.getKey());
            map1.put("channels",e.getValue());
            res.add(map1);
        }
        return res;
    }

    public void addChannelTenant(ChannelTenant channelTenant){
        channelTenantMapper.insertSelective(channelTenant);
    }

    @CacheClear(keys = {"permission:menu", "permission"})
    public void deleteByTenant(String tenantId){
        if(ValidatorUtils.hasAnyBlank(tenantId)){
            throw new ParamErrorException("参数不能为空");
        }
        channelTenantMapper.deleteByTenant(tenantId);
    }

    public List<Map<String,Object>> list(String tenantId){
        return mapper.list(tenantId);
    }

    public List<ChannelDto> getByIds(String ids){
        return  mapper.getByIds(ids);
    }




}