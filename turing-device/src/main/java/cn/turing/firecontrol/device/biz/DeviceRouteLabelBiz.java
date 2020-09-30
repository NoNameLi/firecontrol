package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.entity.DeviceRouteLabel;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import cn.turing.firecontrol.device.mapper.DeviceRouteLabelMapper;
import cn.turing.firecontrol.device.vo.LabelCountVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceRouteLabelBiz extends BusinessBiz<DeviceRouteLabelMapper,DeviceRouteLabel> {

    public List<Integer> selectByRouteId(Integer routeId,String routeFlag){
        return mapper.selectByRouteId(routeId,routeFlag);
    }

    public void deleteByLabel( Integer routeId,String routeFlag,  String labelIds){
        mapper.deleteByLabel(routeId, routeFlag,labelIds);
    }

    public LabelCountVo getIndoorCount(Integer routeId) {
        Map<String,Object> map = new HashMap();
        map.put("routeId",routeId);
        return mapper.getIndoorCount(map);
    }

    public LabelCountVo getOutdoorCount(Integer routeId) {
        Map<String,Object> map = new HashMap();
        map.put("routeId",routeId);
        return mapper.getOutdoorCount(map);
    }

    public DeviceRouteLabel selectByLabelId(Integer labelId, String labelFlag, String routeFlag) {
        Map<String,Object> map = new HashMap();
        map.put("labelId",labelId);
        map.put("labelFlag",labelFlag);
        map.put("routeFlag",routeFlag);
        return mapper.selectByLabelId(map);
    }

    public List<Integer> getByRouteId(Integer routeId, String routeFlag, String tenantId) {
        Map<String,Object> map = new HashMap();
        map.put("routeId",routeId);
        map.put("routeFlag",routeFlag);
        map.put("tenantId",tenantId);
        return mapper.getByRouteId(map);
    }

    public Integer selectByLabelIdCount( String labelFlag,String routeFlag,Integer labelId){
        return mapper.selectByLabelIdCount(labelFlag,routeFlag,labelId);
    }

    public Integer selectByLabelIdResultRouteId( String labelFlag, String routeFlag, Integer labelId){
        return mapper.selectByLabelIdResultRouteId(labelFlag,routeFlag,labelId);
    }
}