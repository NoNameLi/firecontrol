package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.mapper.DeviceBuildingMapper;
import cn.turing.firecontrol.device.mapper.DeviceFireMainMapper;
import cn.turing.firecontrol.device.mapper.DeviceFireMainSensorMapper;
import cn.turing.firecontrol.device.mapper.DeviceNetworkingUnitMapper;
import cn.turing.firecontrol.device.vo.CountVo;
import cn.turing.firecontrol.device.vo.FireMainSensorVo;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
public class DeviceFireMainSensorBiz extends BusinessBiz<DeviceFireMainSensorMapper,DeviceFireMainSensor> {

    @Autowired
    private DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceBuildingMapper deviceBuildingMapper;
    @Autowired
    private DeviceFireMainSensorBiz dfmsBiz;
    @Autowired
    private DeviceNetworkingUnitMapper dnuMapper;

    public TableResultResponse<Map> selectQuery(Query query, String ids, DeviceFireMainSensor deviceFireMainSensor) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        //当ids为空或者-1是查询全部
        if(!("-1".equals(ids)||ids == null)){
            String[] idstr =ids.split(",");
            List idlist = Arrays.asList(idstr);
            map.put("ids",idlist);
        }
        map.put("deviceFireMainSensor",deviceFireMainSensor);
        List<Map> list = mapper.selectQuery(map);
        for(int i=0;i<list.size();i++){
            Map temp = list.get(i);
            String status = (String) temp.get("status");
            //color  0=黄色   1= 红色  2=绿色  3= 灰色
            if("0".equals(status)){
                temp.put("status","故障");
                temp.put("color","#F5A623");
            }else if("1".equals(status)){
                temp.put("status","报警");
                temp.put("color","#FF001F");
            }else if("2".equals(status)){
                temp.put("status","正常");
                temp.put("color","#4F9600");
            }else if("3".equals(status)) {
                temp.put("status","未启用");
                temp.put("color","#757575");
            }else if("4".equals(status)){
                temp.put("status","离线");
                temp.put("color","#F5A623");
            }
            String positionSign = (String) temp.get("positionSign");
            if(positionSign==null||"".equals(positionSign)){
                temp.put("positionSign","未标记");
            }else {
                temp.put("positionSign","已标记");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }



    //根据消防主机id查看绑定的传感器的个数
    public Integer selectByFireMainIdCount(Integer fireMainId){
        return mapper.selectByFireMainIdCount(fireMainId);
    }

    //根据消防主机id查看绑定的传感器信息
    public List<DeviceFireMainSensor> selectByFireMainId(Integer fireMainId){
        return mapper.selectByFireMainId(fireMainId);
    }

    //根据建筑id查询绑定的传感器信息
    public List<DeviceFireMainSensor> selectByBuildingIdQuery(Integer buildingId){
        return mapper.selectByBuildingIdQuery(buildingId);
    }

    //根据ip+端口+回路+地址查询传感器的信息
    public List<DeviceFireMainSensor> selectIgnoreTenantIpAndPortAndSensor(String serverIp, String port, String sensorLoop, String address){
        return mapper.selectIgnoreTenantIpAndPortAndSensor(serverIp,port,sensorLoop,address);
    }

    public List<DeviceFireMainSensor> selectByBuildingId(Integer buildingId, Integer channelId) {
        Map<String,Object> map = new HashMap();
        map.put("buildingId",buildingId);
        map.put("channelId",channelId);
        return mapper.selectByBuildingId(map);
    }

    public String getBuildingStatus(String bName, Integer channelId) {
        DeviceBuilding deviceBuilding = dbBiz.selectByBname(bName);
        Integer callCount = dfmsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"1");//报警个数
        Integer faultCount = dfmsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"0");//故障个数
        Integer offlineCount = dfmsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"4");//离线个数
        if((faultCount + offlineCount) == 0 && callCount == 0){
            return "2";//正常
        }
        if((faultCount + offlineCount) !=0 && callCount==0){
            return "0";//故障
        }
        if(callCount !=0){
            return "1";//报警
        }
        return null;
    }

    public Integer selectStatusCount(Integer buildId, Integer floor, Integer channelId, String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("status",status);
        return mapper.selectStatusCount(map);
    }

    public Integer selectByChannelIdAndStatusAndBuilding(Integer channelId, String status, String buildingId, String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("channelId",channelId);
        map.put("status",status);
        map.put("buildingId",buildingId);
        map.put("tenantId",tenantId);
        return mapper.selectByChannelIdAndStatusAndBuilding(map);

    }

    public List<DeviceFireMainSensor> getSensorStatusByBuildAndFloor(Integer buildId, Integer floor, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        return mapper.getSensorStatusByBuildAndFloor(map);
    }

    //修改传感器的状态
    public void updateSensorStatus(Long id,String status){
        mapper.updateSensorStatus(id,status);
    }

    //当主机一样时回路+地址判重
    public Integer selectByCount(Integer fireMainId,String sensorLoop,String address){
        DeviceFireMainSensor deviceFireMainSensor = new DeviceFireMainSensor();
        deviceFireMainSensor.setFireMainId(fireMainId);
        deviceFireMainSensor.setAddress(address);
        deviceFireMainSensor.setSensorLoop(sensorLoop);
        return mapper.selectByCount(deviceFireMainSensor);
    }

    public Integer selectCountByType(String series, Integer buildId, Integer floor, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("series",series);
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        return mapper.selectCountByType(map);
    }

    public TableResultResponse getNotEnabledSensorList(Query query, Integer channelId, Integer buildingId, Integer floor) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("channelId",channelId);
        map.put("buildingId",buildingId);
        map.put("floor",floor);
        map.put("status","3");
        List<Map<String,String>> list = mapper.getNotEnabledSensorList(map);
        //JSONObject jsonObject = iUserFeign.selectById(channelId);
        for(Map<String,String> sensorMap:list){
            //sensorMap.put("channelName",jsonObject.getJSONObject("data").getString("channelName"));
            if(StringUtils.isNotBlank(sensorMap.get("positionSign"))){
                sensorMap.put("positionSign","true");
            }else{
                sensorMap.put("positionSign","false");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public TableResultResponse selectByFloorGetSensor(Query query, Integer channelId, Integer buildingId, Integer floor, String status) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceFireMainSensor deviceFireMainSensor = new DeviceFireMainSensor();
        deviceFireMainSensor.setBuildingId(buildingId);
        deviceFireMainSensor.setChannelId(channelId);
        deviceFireMainSensor.setFloor(floor);
        deviceFireMainSensor.setStatus(status);
        List<Map> list = mapper.selectByFloorGetSensor(deviceFireMainSensor);
        for(int i=0;i<list.size();i++){
            Map temp = list.get(i);
            String statusname = (String) temp.get("status");
            //color  0=黄色   1= 红色  2=绿色  3= 灰色
            if("0".equals(statusname)){
                temp.put("status","故障");
                temp.put("color","#F5A623");
            }else if("1".equals(statusname)){
                temp.put("status","报警");
                temp.put("color","#FF001F");
            }else if("2".equals(statusname)){
                temp.put("status","正常");
                temp.put("color","#4F9600");
            }else if("4".equals(statusname)){
                temp.put("status","离线");
                temp.put("color","#F5A623");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    // 假删除 根据消防主机的id  批量假删除
    public void deleteByFireMain(Integer fireMainId){
        mapper.deleteByFireMain(fireMainId);
    }

    public TableResultResponse getAllBuildingList(Query query, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> lists = new ArrayList<>();
        List<DeviceBuilding> list = deviceBuildingMapper.selectQuery(null);
        for(int i=0;i<list.size();i++){
            //获取安全责任人
            Map<String,Object> map = new HashMap<>();
            DeviceBuilding deviceBuilding=list.get(i);
            DeviceNetworkingUnit deviceNetworkingUnit=dnuMapper.selectByPrimaryKey(deviceBuilding.getOid());
            map.put("id",deviceBuilding.getId());
            map.put("bName",deviceBuilding.getBName());
            map.put("bAddress",deviceBuilding.getBAddress());
            map.put("safeDutyName",deviceNetworkingUnit.getSafeDutyName());
            map.put("safeDutyPhone",deviceNetworkingUnit.getSafeDutyPhone());

            //获取传感器的数量状态
            Integer faultCount = dfmsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"0");
            Integer faultTempCount = dfmsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"4");
            Integer callCount = dfmsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"1");
            Integer normalCount = dfmsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"2");
            map.put("faultCount",faultCount+faultTempCount);
            map.put("callCount",callCount);
            map.put("normalCount",normalCount);
            lists.add(map);
        }
        return new TableResultResponse(result.getTotal(),lists);
    }

    public CountVo getCountByStatus(Integer buildId, Integer floor, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        return mapper.getCountByStatus(map);
    }

    public Integer getStatusCount(Integer buildId, Integer floor, Integer channelId, String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("status",status);
        return mapper.getStatusCount(map);
    }

    public TableResultResponse selectListByBuildId(Query query, Integer buildId, String status, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map map1 = new HashMap();
        map1.put("buildId",buildId);
        map1.put("status",status);
        map1.put("channelId",channelId);
        List<Map<String, Object>> resultlist = new LinkedList();
        if("0".equals(status)){//故障,离线状态算故障;
            map1.put("status1",4);
            List<DeviceFireMainSensor> list = mapper.selectByBuildingIdAndStatus(map1);
            for(DeviceFireMainSensor deviceFireMainSensor:list){
                Map map = new HashMap();
                Long id = deviceFireMainSensor.getId();
                map.put("id",id);
                map.put("type",deviceFireMainSensor.getSeries());
                map.put("positionDescription",deviceFireMainSensor.getFloor()+"F-"+deviceFireMainSensor.getPositionDescription());
                map.put("alrmDate",deviceFireMainSensor.getStatusTime());
                resultlist.add(map);
            }
        }
        if("1".equals(status)){//报警
            map1.put("status1",null);
            List<DeviceFireMainSensor> list = mapper.selectByBuildingIdAndStatus(map1);
            for(DeviceFireMainSensor deviceFireMainSensor:list){
                Map map = new HashMap();
                map.put("id",deviceFireMainSensor.getId());
                map.put("type",deviceFireMainSensor.getSeries());
                map.put("positionDescription",deviceFireMainSensor.getFloor()+"F-"+deviceFireMainSensor.getPositionDescription());
                map.put("alrmDate",deviceFireMainSensor.getStatusTime());
                resultlist.add(map);
            }
        }
        if("2".equals(status)){//正常
            map1.put("status1",null);
            List<DeviceFireMainSensor> list = mapper.selectByBuildingIdAndStatus(map1);
            for(DeviceFireMainSensor deviceFireMainSensor:list){
                Map map = new HashMap();
                map.put("id",deviceFireMainSensor.getId());
                map.put("type",deviceFireMainSensor.getSeries());
                map.put("positionDescription",deviceFireMainSensor.getFloor()+"F-"+deviceFireMainSensor.getPositionDescription());
                map.put("status","2");
                resultlist.add(map);
            }
        }
        if(!status.equals("2")){
            Collections.sort(resultlist, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    if(((Date)o2.get("alrmDate")).getTime()>((Date)o1.get("alrmDate")).getTime()){
                        return 1;
                    }else {
                        return -1;
                    }
                }
            });
        }
        return new TableResultResponse(result.getTotal(),resultlist);
    }

    public Integer selectNotsignCount(Integer buildId, Integer floor, Integer channelId, String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("status",status);
        return mapper.selectNotsignCount(map);
    }

    public List<String> getSeriesByBuildAndFloor(Integer buildId, Integer floor, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        return mapper.getSeriesByBuildAndFloor(map);
    }

    public TableResultResponse  listFireMainSensorByIds(Integer page,Integer limit,
                                                         String ids,
                                                         String serverIp,
                                                         String port,
                                                         String sensorLoop,
                                                         String address,
                                                         Integer buildingId,
                                                         String exIds,
                                                         String series,
                                                         String code){
        if(code != null && code.length() != 6){
            code = null;
        }
        if(code != null){
            if("00".equals(code.substring(2,4))){
                code = code.substring(0,2) + "____";
            }else if("00".equals(code.substring(4,6))){
                code = code.substring(0,4) + "__";
            }
        }
        Page<Object> result = PageHelper.startPage(page, limit);
        List<FireMainSensorVo> list = this.mapper
                .listFireMainSensorByIds(ids,serverIp, port, sensorLoop, address,buildingId,exIds,series,code);
        return new TableResultResponse(result.getTotal(),list);
    }
}