package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.datahandler.entity.NoticeRule;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleSensor;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleUser;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.mapper.NoticeRuleSensorMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/03/18 10:24
 *
 * @Description TODO
 * @Version V1.0
 */
@Service
public class NoticeRuleSensorBiz extends BusinessBiz<NoticeRuleSensorMapper, NoticeRuleSensor> {
    @Autowired
    private IDeviceFeign iDeviceFeign;

    @Autowired
    private NoticeRuleBiz noticeRuleBiz;


    /**
     * 查询推送规则已绑定的传感器列表
     * @param pageNo
     * @param limit
     * @param noticeRuleId
     * @param manufacturer
     * @param equipmentType
     * @param model
     * @param sensorNo
     * @return
     */
    public TableResultResponse listBindedSensor(Integer pageNo, Integer limit, Long noticeRuleId,String manufacturer,
                                                String equipmentType, String model, String sensorNo){
        NoticeRule rule = noticeRuleBiz.selectById(noticeRuleId);
        if(rule == null || "1".equals(rule.getDelFlag())){
            throw new IllegalArgumentException("对应规则不存在");
        }
        NoticeRuleSensor dto = new NoticeRuleSensor();
        dto.setNoticeRuleId(noticeRuleId);
        dto.setDelFlag("0");
        List<NoticeRuleSensor> rsList = this.mapper.select(dto);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rsList.size();i++){
            sb.append(rsList.get(i).getSensorId());
            if(i != rsList.size() - 1){
                sb.append(",");
            }
        }

        if(rsList.size()  == 0){
            return new TableResultResponse(0,new ArrayList());
        }

        Map<String,Object> map = new HashMap<>();
        map.put("ids",sb.toString());
        map.put("manufacturer",manufacturer);
        map.put("equipmentType",equipmentType);
        map.put("model",model);
        map.put("sensorNo",sensorNo);
        map.put("buildingId",null);
        map.put("excludeIds",null);
        map.put("channelId",rule.getChannelId());
        return iDeviceFeign.querySensors(pageNo,limit,JSONObject.toJSONString(map));
    }

    /**
     * 查询推送规则已绑定的消防主机传感器列表
     * @param page
     * @param limit
     * @param noticeRuleId
     * @param buildingId
     * @param channelId
     * @param series
     * @param serverIp
     * @param port
     * @param sensorLoop
     * @param address
     * @return
     */
    public TableResultResponse listFireMainBindedSensor(Integer page,Integer limit,Long noticeRuleId,
                                                        Integer buildingId,Integer channelId,
                                                        String series,String serverIp,String port,
                                                        String sensorLoop,String address){
        NoticeRule rule = noticeRuleBiz.selectById(noticeRuleId);
        if(rule == null || "1".equals(rule.getDelFlag())){
            throw new IllegalArgumentException("对应规则不存在");
        }
        NoticeRuleSensor dto = new NoticeRuleSensor();
        dto.setNoticeRuleId(noticeRuleId);
        dto.setDelFlag("0");
        List<NoticeRuleSensor> rsList = this.mapper.select(dto);
        if(rsList.size()  == 0){
            return new TableResultResponse(0,new ArrayList());
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rsList.size();i++){
            sb.append(rsList.get(i).getSensorId());
            if(i != rsList.size() - 1){
                sb.append(",");
            }
        }

        Map<String,Object> map = new HashMap<>();
        map.put("ids",sb.toString());
        map.put("serverIp",serverIp);
        map.put("port",port);
        map.put("series",series);
        map.put("sensorLoop",sensorLoop);
        map.put("address",address);
        map.put("buildingId",buildingId);
        map.put("channelId",channelId);
        map.put("excludeIds",null);
        return iDeviceFeign.listFireMainSensorByIds(page,limit,JSONObject.toJSONString(map));
        //return iDeviceFeign.querySensors(page,limit,JSONObject.toJSONString(map));
    }

    public TableResultResponse listFireMainUnBindedSensor(Integer page,Integer limit,Long noticeRuleId,Integer buildingId,Integer channelId,String code){
        NoticeRule rule = noticeRuleBiz.selectById(noticeRuleId);
        if(rule == null || "1".equals(rule.getDelFlag())){
            throw new IllegalArgumentException("对应规则不存在");
        }
        NoticeRuleSensor dto = new NoticeRuleSensor();
        dto.setNoticeRuleId(noticeRuleId);
        dto.setDelFlag("0");
        List<NoticeRuleSensor> rsList = this.mapper.select(dto);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rsList.size();i++){
            sb.append(rsList.get(i).getSensorId());
            if(i != rsList.size() - 1){
                sb.append(",");
            }
        }

        Map<String,Object> map = new HashMap<>();
        map.put("ids",null);
        map.put("channelId",channelId);
        map.put("buildingId",buildingId);
        map.put("excludeIds",sb.toString());
        map.put("code",code);
        return iDeviceFeign.listFireMainSensorByIds(page,limit,JSONObject.toJSONString(map));
    }

    /**
     * 查询推送规则未绑定的消防主机传感器列表
     * @param page
     * @param limit
     * @param noticeRuleId
     * @param buildingId
     * @param channelId
     * @return
     */
    public TableResultResponse listFireMainBindedSensor(Integer page,Integer limit,Long noticeRuleId,
                                                        Integer buildingId,Integer channelId){
        NoticeRule rule = noticeRuleBiz.selectById(noticeRuleId);
        if(rule == null || "1".equals(rule.getDelFlag())){
            throw new IllegalArgumentException("对应规则不存在");
        }
        NoticeRuleSensor dto = new NoticeRuleSensor();
        dto.setNoticeRuleId(noticeRuleId);
        dto.setDelFlag("0");
        List<NoticeRuleSensor> rsList = this.mapper.select(dto);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rsList.size();i++){
            sb.append(rsList.get(i).getSensorId());
            if(i != rsList.size() - 1){
                sb.append(",");
            }
        }

        Map<String,Object> map = new HashMap<>();
        map.put("buildingId",buildingId);
        map.put("excludeIds",sb.toString());
        return iDeviceFeign.listFireMainSensorByIds(page,limit,JSONObject.toJSONString(map));
    }

    /**
     *
     * @param pageNo
     * @param limit
     * @param buildingId　建筑ID
     * @return
     */
    public TableResultResponse listUnBindedSensorForIndoorSystem(Integer pageNo, Integer limit, Long buildingId,Long channelId,String code){
        //首先查询出所有已关联传感器的ID
        NoticeRuleSensor example = new NoticeRuleSensor();
        example.setDelFlag("0");
        example.setChannelId(channelId);
        List<NoticeRuleSensor> rsList = this.selectList(example);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rsList.size();i++){
            sb.append(rsList.get(i).getSensorId());
            if(i != rsList.size() - 1){
                sb.append(",");
            }
        }
        Map<String,Object> map = new HashMap<>();
      //  map.put("ids",sb.toString());
      //  map.put("manufacturer",manufacturer);
      //  map.put("equipmentType",equipmentType);
      //  map.put("model",model);
      //  map.put("sensorNo",sensorNo);
        map.put("buildingId",buildingId);
        map.put("excludeIds",sb.toString());
        map.put("channelId",channelId);
        map.put("code",code);
        return iDeviceFeign.querySensors(pageNo,limit,JSONObject.toJSONString(map));
     }

    /**
     *
     * @param pageNo
     * @param limit
     * @param groupId　设备组ID
     * @return
     */
    public TableResultResponse listUnBindedSensorForNestedSystem(Integer pageNo, Integer limit, Long groupId,Long channelId){
        //首先查询出所有已关联传感器的ID
        NoticeRuleSensor example = new NoticeRuleSensor();
        example.setDelFlag("0");
        example.setChannelId(channelId);
        List<NoticeRuleSensor> rsList = this.selectList(example);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rsList.size();i++){
            sb.append(rsList.get(i).getSensorId());
            if(i != rsList.size() - 1){
                sb.append(",");
            }
        }

        Map<String,Object> map = new HashMap<>();
        //  map.put("ids",sb.toString());
        //  map.put("manufacturer",manufacturer);
        //  map.put("equipmentType",equipmentType);
        //  map.put("model",model);
        //  map.put("sensorNo",sensorNo);
        map.put("groupId",groupId);
        map.put("excludeIds",sb.toString());
        map.put("channelId",channelId);
        return iDeviceFeign.queryNestedSensor(pageNo,limit,JSONObject.toJSONString(map));
    }

    /**
     *
     * @param pageNo
     * @param limit
     * @param code　建筑ID
     * @return
     */
    public TableResultResponse listUnBindedSensorForOutdoorSystem(Integer pageNo, Integer limit, String code,Long channelId){
        //首先查询出所有已关联传感器的ID
        NoticeRuleSensor example = new NoticeRuleSensor();
        example.setDelFlag("0");
        example.setChannelId(channelId);
        List<NoticeRuleSensor> rsList = this.selectList(example);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rsList.size();i++){
            sb.append(rsList.get(i).getSensorId());
            if(i != rsList.size() - 1){
                sb.append(",");
            }
        }

        Map<String,Object> map = new HashMap<>();
        //  map.put("ids",sb.toString());
        //  map.put("manufacturer",manufacturer);
        //  map.put("equipmentType",equipmentType);
        //  map.put("model",model);
        //  map.put("sensorNo",sensorNo);
        map.put("code",code);
        map.put("excludeIds",sb.toString());
        map.put("channelId",channelId);

        return iDeviceFeign.queryOutdoorSensor(pageNo,limit,JSONObject.toJSONString(map));
    }

    /**
     * 删除推送规则绑定的传感器
     * @param noticeRuleId 规则ID
     * @param sensorIds 传感器ID列表
     */
    @Transactional
    public void delBindedSensor(Long noticeRuleId,String sensorIds){
        NoticeRule rule = noticeRuleBiz.selectById(noticeRuleId);
        if(rule == null || "1".equals(rule.getDelFlag())){
            throw new IllegalArgumentException("对应规则不存在");
        }
        this.mapper.deleteByIds(noticeRuleId,sensorIds);
    }

    @Transactional
    public void batchAdd(Long noticeRuleId,String sensorIds){
        NoticeRule rule = noticeRuleBiz.selectById(noticeRuleId);
        if(rule == null || "1".equals(rule.getDelFlag())){
            throw new IllegalArgumentException("对应规则不存在");
        }

        for(String str : sensorIds.split(",")){
            Long sensorId = Long.valueOf(str);
            //检查该设备是否已被某规则关联
            NoticeRuleSensor example = new NoticeRuleSensor();
            example.setDelFlag("0");
            example.setChannelId(rule.getChannelId().longValue());
            example.setSensorId(sensorId);
            if(this.mapper.select(example).size() > 0){
                //已绑定的直接跳过
                continue;
            }
            NoticeRuleSensor rsEntity = new NoticeRuleSensor();
            rsEntity.setSensorId(sensorId);
            rsEntity.setChannelId(rule.getChannelId().longValue());
            rsEntity.setDelFlag("0");
            rsEntity.setNoticeRuleId(noticeRuleId);
            this.insertSelective(rsEntity);
        }

    }

    /**
     * 用于消息推送（非租户隔离接口）
     * @param sensorId
     * @param channelId
     * @return
     */
    public NoticeRuleSensor queryBySensorIdAndChannelId(Long sensorId,Integer channelId){
        return this.mapper.queryBySensorIdAndChannelId(sensorId,channelId);
    }

    /**
     * 删除推送规则与所有传感器的绑定关系
     * @param noticeRuleId
     * @return
     */
    public int deleteByNoticeRuleId(Long noticeRuleId){
        return mapper.deleteByNoticeRuleId(noticeRuleId);
    }
}
