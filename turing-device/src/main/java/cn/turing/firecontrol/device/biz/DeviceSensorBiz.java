package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.EntityUtils;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.dto.SensorTypeStatus;
import cn.turing.firecontrol.device.dto.TypeNumDto;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.enums.SensorStatusEnum;
import cn.turing.firecontrol.device.feign.AdminFeign;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.mapper.*;
import cn.turing.firecontrol.device.util.Constant;
import cn.turing.firecontrol.device.vo.*;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceSensorBiz extends BusinessBiz<DeviceSensorMapper,DeviceSensor> {

    @Autowired
    private DeviceSensorMapper deviceSensorMapper;
    @Autowired
    private DeviceBuildingMapper deviceBuildingMapper;
    @Autowired
    private DeviceNetworkingUnitMapper dnuMapper;
    @Autowired
    private IUserFeign iUserFeign;
    @Autowired
    private DeviceMeasuringPointMapper dmpMapper;
    @Autowired
    private DeviceAlarmThresholdBiz datBiz;
    @Autowired
    private DeviceAlarmLevelBiz dalBiz;
    @Autowired
    private DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceNetworkingUnitBiz dnuBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;
    @Autowired
    private DeviceSensorTypeBiz dstBiz;
    @Autowired
    private DeviceHardwareFacilitiesBiz dhfBiz;
    @Autowired
    private TransportClient client;
    @Autowired
    private DeviceSensorSeriesBiz dssBiz;
    @Autowired
    private DeviceSensorMpRelationBiz dsmrBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmpBiz;
    @Autowired
    private DeviceFireMainSensorBiz dfmsBiz;

    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;
    @Autowired
    private AdminFeign adminFeign;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void insertSelective(DeviceSensor entity) {
        DeviceSensor sensor  = selectBySensorNo(entity.getSensorNo());
        if(sensor != null){
            throw new RuntimeException("设备已存在");
        }
        super.insertSelective(entity);
    }

    //根据建筑id查询
    public List<DeviceSensor> selectByBuildingId(Integer id,Integer channelId) {
        return deviceSensorMapper.selectByBuildingId(id,channelId);
    }
    //根据传感器类型id查询
    public  List<DeviceSensor> selectBySensorTypeId(Integer id) {
        return deviceSensorMapper.selectBySensorTypeId(id);
    }

    public TableResultResponse<DeviceSensor> selectQuery(Query query, String ids,DeviceSensor deviceSensor) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        //当ids为空或者-1是查询全部
        if(!("-1".equals(ids)||ids == null)){
            String[] idstr =ids.split(",");
            List idlist = Arrays.asList(idstr);
            map.put("ids",idlist);
        }
        map.put("deviceSensor",deviceSensor);
        List<Map> list = deviceSensorMapper.selectQuery(map);
        for(int i=0;i<list.size();i++){
            Map temp = list.get(i);
            Integer chid = (Integer)temp.get("channelId");
            if(chid==null){
                new RuntimeException("所属系统找不到！");
            }
            JSONObject a= iUserFeign.selectById(chid);
            JSONObject data = a.getJSONObject("data");
            if(data==null){
                temp.put("channelName", "无");
            }else {
                temp.put("channelName", data.get("channelName"));
            }
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

    //室外传感器列表
    public TableResultResponse<DeviceSensor> selectOutdoorQuery(Query query, String ids,String facilityType,String hydrantName,DeviceSensor deviceSensor) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        //当ids为-1或者空时查询全部
        if(!(ids == null || "-1".equals(ids))){
            String[] idstr =ids.split(",");
            List idlist = Arrays.asList(idstr);
            map.put("ids",idlist);
        }
        map.put("facilityType",facilityType);
        map.put("hydrantName",hydrantName);
        map.put("deviceSensor",deviceSensor);
        List<Map> list = deviceSensorMapper.selectOutdoorQuery(map);
        for(int i=0;i<list.size();i++){
            Map temp = list.get(i);
            Integer chid = (Integer)temp.get("channelId");
            if(chid==null){
                new RuntimeException("所属系统找不到！");
            }
            JSONObject a= iUserFeign.selectById(chid);
            JSONObject data = a.getJSONObject("data");
            if(data==null){
                temp.put("channelName", "无");
            }else {
                temp.put("channelName", data.get("channelName"));
            }
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
            String facilityTypeStr = (String) temp.get("facilityType");
            if("0".equals(facilityTypeStr)){
                temp.put("facilityType","室外消火栓");
            }else {
                temp.put("facilityType","");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }
    public Integer selectCountByType(Integer id,Integer buildId,Integer floor,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("floor",floor);
        map.put("buildId",buildId);
        map.put("channelId",channelId);
        return deviceSensorMapper.selectCountByType(map);
    }

    public Integer selectStatusCount(Integer buildId, Integer floor,Integer channelId,String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("status",status);
        return deviceSensorMapper.selectStatusCount(map);
    }

    public TableResultResponse<DeviceSensor> selectAbnormal(Query query, Integer buildId, Integer floor,Integer channelId){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        List<AbnormalVo> list = deviceSensorMapper.selectAbnormal(map);
        for(AbnormalVo abnormalVo:list){
            DeviceMeasuringPoint deviceMeasuringPoint = dmpMapper.selectByCodeName(abnormalVo.getUnit());
            if(deviceMeasuringPoint!=null){
                abnormalVo.setUnit(deviceMeasuringPoint.getMeasuringPoint());
                //当报警时获取报警等级
                if("1".equals(abnormalVo.getAlrmCategory())){
                    DeviceAlarmThreshold deviceAlarmThreshold = datBiz.selectByAlrmData(deviceMeasuringPoint.getId(),abnormalVo.getAlrmData(),abnormalVo.getTenantId());
                    if(deviceAlarmThreshold!=null){
                        DeviceAlarmLevel deviceAlarmLevel =dalBiz.selectById(deviceAlarmThreshold.getAlId());
                        abnormalVo.setLevel(deviceAlarmLevel.getLevel());
                    }
                }
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public DeviceSensor selectBySensorNo(String sensorNo) {
        return deviceSensorMapper.selectBySensorNo(sensorNo);
    }

    //查看代号是否重复
    public Integer selectByCount(String sensorNo){
        return deviceSensorMapper.selectByCount(sensorNo);
    }

    //获取所有的厂商
    public List<String> getManufacturer(){
        return deviceSensorMapper.getManufacturer();
    }

    //获取所有的类型
    public List<String> getEquipmentType(){
        return deviceSensorMapper.getEquipmentType();
    }

    public TableResultResponse<DeviceSensor> selectByArea(Query query,String zxqy, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("zxqy",zxqy);
        map.put("channelId",channelId);
        //查询条件对应建筑物
        List<DeviceBuilding> list = deviceBuildingMapper.selectByArea(map);
        List<Map<String ,Object>> resultlist = new ArrayList();
        for(DeviceBuilding deviceBuilding:list){
            Map resultmap = new HashMap();
            resultmap.put("BuildId",deviceBuilding.getId());//建筑id
            resultmap.put("name",deviceBuilding.getBName());
            resultmap.put("address",deviceBuilding.getBAddress());
            //根据单位id查询单位
            DeviceNetworkingUnit deviceNetworkingUnit = dnuBiz.selectById(deviceBuilding.getOid());
            //resultmap.put("networkingUnitId",deviceBuilding.getId());//联网单位id
            resultmap.put("principal",deviceNetworkingUnit.getSafeDutyName());
            resultmap.put("tel",deviceNetworkingUnit.getSafeDutyPhone());
            CountVo countVo = dsBiz.getCountByStatus(deviceBuilding.getId(),null,channelId);
            //消防主机
            CountVo countVo1 = dfmsBiz.getCountByStatus(deviceBuilding.getId(),null,channelId);
//            Integer faultCount = dsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"0");//故障
//            Integer tempCount = dsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"4");//离线算故障
//            Integer callCount = dsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"1");//报警
//            Integer count = dsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"2");//正常
            Integer faultCount = countVo.getFaultCount()+countVo.getOffCount()+countVo1.getFaultCount()+countVo1.getOffCount();
            Integer callCount = countVo.getCallCount()+countVo1.getCallCount();
            Integer totalCount = faultCount+callCount+countVo.getNormalCount()+countVo1.getNormalCount();
            resultmap.put("faultCount",faultCount);
            resultmap.put("callCount",callCount);
            resultmap.put("totalCount",totalCount);
            resultlist.add(resultmap);
        }
        return new TableResultResponse(result.getTotal(),resultlist);
    }

    public  List<DeviceSensor> getSensorStatusByBuildAndFloor(Integer buildId, Integer floor,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        return deviceSensorMapper.getSensorStatusByBuildAndFloor(map);
    }

    public List<DeviceSensor> selectByChannelId(Integer channelId) {
        return deviceSensorMapper.selectByChannelId(channelId);
    }

    //获取所有的代号，存在Set里面
    public Set<String> getAllIgnoreTenantSensorNo(){
        return deviceSensorMapper.getAllIgnoreTenantSensorNo();
    }

//    public List<DeviceSensor> selectByBuildingIdAndStatus(Integer buildId, String status) {
//        return deviceSensorMapper.selectByBuildingIdAndStatus(buildId,status);
//    }

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
            DeviceSensor temp = new DeviceSensor();
            temp.setChannelId(channelId);
            temp.setBuildingId(deviceBuilding.getId());
            temp.setStatus("0");
            //将0，1，2状态为  不存在的填0
            Integer faultCount = deviceSensorMapper.selectByChannelIdAndStatusAndBuilding(temp);
            temp.setStatus("4");
            //离线算故障
            Integer faultTempCount = deviceSensorMapper.selectByChannelIdAndStatusAndBuilding(temp);
            temp.setStatus("1");
            Integer callCount = deviceSensorMapper.selectByChannelIdAndStatusAndBuilding(temp);
            temp.setStatus("2");
            Integer normalCount = deviceSensorMapper.selectByChannelIdAndStatusAndBuilding(temp);
/*            for(DeviceSensor deviceSensor:sensors){
                if(deviceSensor.getStatus().equals("0")){//故障
                    faultCount++;
                }
                if(deviceSensor.getStatus().equals("1")){//报警
                    callCount++;
                }
                if(deviceSensor.getStatus().equals("2")){//正常
                    normalCount++;
                }
            }*/
            map.put("faultCount",faultCount+faultTempCount);
            map.put("callCount",callCount);
            map.put("normalCount",normalCount);
            lists.add(map);
        }
        return new TableResultResponse(result.getTotal(),lists);
    }

    public TableResultResponse<DeviceSensor> selectByFloorGetSensor(Query query,Integer channelId,Integer buildingId,Integer floor,String statusName) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceSensor deviceSensor = new DeviceSensor();
        deviceSensor.setBuildingId(buildingId);
        deviceSensor.setChannelId(channelId);
        deviceSensor.setFloor(floor);
        deviceSensor.setStatus(statusName);
        List<Map> list = deviceSensorMapper.selectByFloorGetSensor(deviceSensor);
        for(int i=0;i<list.size();i++){
            Map temp = list.get(i);
            Integer chid = (Integer)temp.get("channelId");
            if(chid==null){
                new RuntimeException("所属系统找不到！");
            }
            JSONObject a= iUserFeign.selectById(chid);
            JSONObject data = a.getJSONObject("data");
            if(data==null){
                temp.put("channelName", "无");
            }else {
                temp.put("channelName", data.get("channelName"));
            }
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
            }else if("4".equals(status)){
                temp.put("status","离线");
                temp.put("color","#F5A623");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }
    public TableResultResponse selectListByBuildId(Query query, Integer buildId, String status,Integer channelId){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map map1 = new HashMap();
        if(buildId == null){
            map1.put("buildId",null);
        }else{
            map1.put("buildId",buildId);
        }
        map1.put("status",status);
        map1.put("channelId",channelId);
        List<Map<String, Object>> resultlist = new LinkedList();
        if("0".equals(status)){//故障,离线状态算故障;
            map1.put("status1",4);
            List<DeviceSensor> list = deviceSensorMapper.selectByBuildingIdAndStatus(map1);
            for(DeviceSensor deviceSensor:list){
                Map map = new HashMap();
                Long id = deviceSensor.getId();
                DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
//                Integer faultCount = daBiz.selectCountBySensorNo(deviceSensor.getSensorNo(),"0");//故障次数
//                Integer alrmCount = daBiz.selectCountBySensorNo(deviceSensor.getSensorNo(),"1");//报警次数
                map.put("id",id);
                map.put("sensorNo",deviceSensor.getSensorNo());
                VideoDeviceVo vo = this.deviceVideoExtBiz.getVideoDeviceDetail(deviceSensor.getId());
                if(vo != null){
                    map.put("deviceName",vo.getDeviceName());
                    map.put("groupName",vo.getGroupName());
                }
                if(deviceSensorType!=null){
                    map.put("type",deviceSensorType.getEquipmentType());
                }
                if(deviceSensor.getHydrantId()!=null && deviceSensor.getBuildingId()==null){
                    DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectById(deviceSensor.getHydrantId());
                    map.put("positionDescription",deviceHardwareFacilities.getPositionDescription());
                }
                if(deviceSensor.getBuildingId()!=null && deviceSensor.getHydrantId()==null){
                    map.put("positionDescription",deviceSensor.getFloor()+"-"+deviceSensor.getPositionDescription());
                }

//                map.put("faultCount",faultCount);
//                map.put("alrmCount",alrmCount);
                map.put("alrmDate",deviceSensor.getStatusTime());
                resultlist.add(map);
            }
        }
        if("1".equals(status)){//报警
            map1.put("status1",null);
            List<DeviceSensor> list = deviceSensorMapper.selectByBuildingIdAndStatus(map1);
            for(DeviceSensor deviceSensor:list){
                Map map = new HashMap();
                //List<DeviceAbnormal> abnormalList= daBiz.selectByEquIdAndBuildId(deviceSensor.getId(),buildId);
                DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
                map.put("id",deviceSensor.getId());
                map.put("sensorNo",deviceSensor.getSensorNo());
                VideoDeviceVo vo = this.deviceVideoExtBiz.getVideoDeviceDetail(deviceSensor.getId());
                if(vo != null){
                    map.put("deviceName",vo.getDeviceName());
                    map.put("groupName",vo.getGroupName());
                }
                if(deviceSensorType!=null){
                    map.put("type",deviceSensorType.getEquipmentType());
                }
                if(deviceSensor.getHydrantId()!=null && deviceSensor.getBuildingId()==null){
                    DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectById(deviceSensor.getHydrantId());
                    map.put("positionDescription",deviceHardwareFacilities.getPositionDescription());
                }
                if(deviceSensor.getBuildingId()!=null && deviceSensor.getHydrantId()==null){
                    map.put("positionDescription",deviceSensor.getFloor()+"-"+deviceSensor.getPositionDescription());
                }
                map.put("alrmDate",deviceSensor.getStatusTime());
//                Integer faultCount = daBiz.selectCountBySensorNo(deviceSensor.getSensorNo(),"0");//故障次数
//                Integer alrmCount = daBiz.selectCountBySensorNo(deviceSensor.getSensorNo(),"1");//报警次数
//                map.put("faultCount",faultCount);
//                map.put("alrmCount",alrmCount);
                resultlist.add(map);
                }
        }
        if("2".equals(status)){//正常
            map1.put("status1",null);
            List<DeviceSensor> list = deviceSensorMapper.selectByBuildingIdAndStatus(map1);
            for(DeviceSensor deviceSensor:list){
                Map map = new HashMap();
                DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
                map.put("id",deviceSensor.getId());
                map.put("sensorNo",deviceSensor.getSensorNo());
                VideoDeviceVo vo = this.deviceVideoExtBiz.getVideoDeviceDetail(deviceSensor.getId());
                if(vo != null){
                    map.put("deviceName",vo.getDeviceName());
                    map.put("groupName",vo.getGroupName());
                }
                if(deviceSensorType!=null){
                    map.put("type",deviceSensorType.getEquipmentType());
                }
                if(deviceSensor.getHydrantId()!=null && deviceSensor.getBuildingId()==null){
                    DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectById(deviceSensor.getHydrantId());
                    map.put("positionDescription",deviceHardwareFacilities.getPositionDescription());
                }
                if(deviceSensor.getBuildingId()!=null && deviceSensor.getHydrantId()==null){
                    map.put("positionDescription",deviceSensor.getFloor()+"-"+deviceSensor.getPositionDescription());
                }
                map.put("status","2");
//                Integer faultCount = daBiz.selectCountBySensorNo(deviceSensor.getSensorNo(),"0");//故障次数
//                Integer alrmCount = daBiz.selectCountBySensorNo(deviceSensor.getSensorNo(),"1");//报警次数
//                map.put("faultCount",faultCount);
//                map.put("alrmCount",alrmCount);
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

    //根据传感器的栏目id，跟状态查询对应的个数
    public Integer selectByChannelIdAndStatusAndBuilding(Integer channel,String status,Integer buildingId,String tenantId){
        DeviceSensor deviceSensor = new DeviceSensor();
        deviceSensor.setChannelId(channel);
        deviceSensor.setStatus(status);
        deviceSensor.setBuildingId(buildingId);
        deviceSensor.setTenantId(tenantId);
        return deviceSensorMapper.selectByChannelIdAndStatusAndBuilding(deviceSensor);
    }

    //批量假删除
    public void updateBatch(String id){
        deviceSensorMapper.updateBatch(id);
    }

    public Integer selectNotsignCount(Integer buildId, Integer floor, Integer channelId, String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("status",status);
        return deviceSensorMapper.selectNotsignCount(map);
    }

    public Integer selectSignCount(Integer buildId, Integer floor, Integer channelId,String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("status",status);
        return deviceSensorMapper.selectSignCount(map);
    }

    public Integer selectFaultCount(Integer hydrantId,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("hydrantId",hydrantId);
        map.put("channelId",channelId);
        return deviceSensorMapper.selectFaultCount(map);
    }

    public List<DeviceSensor> selectByHydrantId(Integer hydrantId,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("hydrantId",hydrantId);
        map.put("channelId",channelId);
        return deviceSensorMapper.selectByHydrantId(map);
    }
    //删除硬件设施  根据硬件设施的id查询
    public Integer deleteQueryByHydrantId(Integer hydrantId){
        return deviceSensorMapper.deleteQueryByHydrantId(hydrantId);
    }

    public List<DeviceSensor> getByHydrantId(Integer hydrantId, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("hydrantId",hydrantId);
        map.put("channelId",channelId);
        return deviceSensorMapper.getByHydrantId(map);
    }

    public Integer selectByChannelIdAndStatusAndHydrant(Integer channelId, String status, Object hydrantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("hydrantId",hydrantId);
        map.put("status",status);
        map.put("channelId",channelId);
        return deviceSensorMapper.selectByChannelIdAndStatusAndHydrant(map);
    }

    public TableResultResponse getAllHardwareFacilitiesList(Query query, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<DeviceSensor> list = deviceSensorMapper.selectHardwareFacilitiesByChannelId(channelId);
        List resultlist = new ArrayList();
        for(DeviceSensor deviceSensor:list){
            Map<String ,Object> map = new HashMap<>();
            //查询消火栓
            DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectById(deviceSensor.getHydrantId());
            //传感器类型
            DeviceSensorType deviceSensorType =dstBiz.selectById(deviceSensor.getSensorTypeId());
            //查询传感器系列
            DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
            //查询测点ids
            List<Integer> ids = dsmrBiz.selectBySensorSeriesId(deviceSensorSeries.getId());
            map.put("hydrantName",deviceHardwareFacilities.getHydrantName());
            map.put("adress",deviceHardwareFacilities.getPositionDescription());
            //0=单口式/1=双口式/2=三出水口式
            if("0".equals(deviceHardwareFacilities.getOutlet())){
                map.put("outlet","单口式");
            }
            if("1".equals(deviceHardwareFacilities.getOutlet())){
                map.put("outlet","双口式");
            }
            if("2".equals(deviceHardwareFacilities.getOutlet())){
                map.put("outlet","三出水口式");
            }
            //ES查询
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            JSONObject jsonObject =null;
            //mac地址
            if (org.apache.commons.lang.StringUtils.isNotBlank(deviceSensor.getSensorNo())) {
                queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceSensor.getSensorNo()));
                SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.DESC);
                SearchResponse searchResponse = client.prepareSearch(Constant.ESConstant.ES_INDEX_SENSOR).setQuery(queryBuilder).setSize(1).addSort(sortBuilder).execute().actionGet();
                SearchHits searchHits = searchResponse.getHits();
                if(searchHits.getTotalHits()>0){
                    jsonObject = JSONObject.parseObject(searchHits.getAt(0).getSourceAsString());
                }
            }
            for(Integer id:ids){
                DeviceMeasuringPoint deviceMeasuringPoint =dmpBiz.selectById(id);
                if(deviceMeasuringPoint==null){
                    throw new RuntimeException("测点不存在!");
                }
                if(jsonObject.containsKey(deviceMeasuringPoint.getCodeName())){
                    JSONObject jasonObject1 = JSONObject.parseObject(jsonObject.get(deviceMeasuringPoint.getCodeName()).toString());
                    map.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
                    if(jasonObject1.get("alarmValue")!=null){
                        map.put("data",jasonObject1.get("alarmValue"));
                        map.put("type",jasonObject1.get("alarmType"));
                    }
                }
            }
            resultlist.add(map);
        }
        return new TableResultResponse(result.getTotal(),resultlist);
    }

    public List<String> getOutdoorEquipmentType() {
        return deviceSensorMapper.getOutdoorEquipmentType();
    }

    public List<String> getOutdoorManufacturer() {
        return deviceSensorMapper.getOutdoorManufacturer();
    }

    //根据建筑名称查询建筑对应的状态
    public String getBuildingStatus(Integer buildId,Integer channelId){
//        DeviceBuilding deviceBuilding = dbBiz.selectById(buildingId);
//        Integer callCount = dsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"1");//报警个数
//        Integer faultCount = dsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"0");//故障个数
//        Integer offlineCount = dsBiz.selectStatusCount(deviceBuilding.getId(),null,channelId,"4");//离线个数
        CountVo countVo = dsBiz.getCountByStatus(buildId,null,channelId);
        Integer faultCount = countVo.getFaultCount() + countVo.getOffCount();
        Integer callCount = countVo.getCallCount();
        if(faultCount == 0 && callCount == 0){
            return "2";//正常
        }
        if(faultCount !=0 && callCount==0){
            return "0";//故障
        }
        if(callCount !=0){
            return "1";//报警
        }
        return null;
    }

    //根据建筑名称查询建筑对应的状态
    public Integer[] getBuildingStatusAndCount(Integer buildId,Integer channelId){
        CountVo countVo = dsBiz.getCountByStatus(buildId,null,channelId);
        Integer faultCount = countVo.getFaultCount() + countVo.getOffCount();
        Integer callCount = countVo.getCallCount();
        return new Integer[]{callCount,faultCount};
    }

    //获得全部建筑对应的状态
    public String getTotalBuildingStatus(Integer buildId,Integer channelId){
//        DeviceBuilding deviceBuilding = dbBiz.selectByBname(buildingName);
        CountVo countVo = dsBiz.getCountByStatus(buildId,null,channelId);
        //消防主机
        CountVo countVo1 = dfmsBiz.getCountByStatus(buildId,null,channelId);
        Integer faultCount = countVo.getFaultCount() + countVo.getOffCount()+countVo1.getFaultCount()+countVo1.getOffCount();
        Integer callCount = countVo.getCallCount()+countVo1.getCallCount();
        if(faultCount == 0 && callCount == 0){
            return "2";//正常
        }
        if(faultCount !=0 && callCount==0){
            return "0";//故障
        }
        if(callCount !=0){
            return "1";//报警
        }
        return null;
    }

    public DeviceSensor getById(Long id) {
        return deviceSensorMapper.getById(id);
    }

    public String HardwareFacilities(String hydrantName) {
        DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectByHydrantName(hydrantName);
        Integer faultCount = dsBiz.selectHardwareFacilitiesStatusCount(deviceHardwareFacilities.getId(),"0");//故障个数
        Integer offlineCount = dsBiz.selectHardwareFacilitiesStatusCount(deviceHardwareFacilities.getId(),"4");//离线个数
        if((faultCount + offlineCount) == 0){
            return "2";//正常
        }
        if((faultCount + offlineCount) !=0 ){
            return "0";//故障
        }
        return null;
    }

    public Integer selectHardwareFacilitiesStatusCount(Integer hydrantId, String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("hydrantId",hydrantId);
        map.put("status",status);
        return deviceSensorMapper.selectHardwareFacilitiesStatusCount(map);
    }

    public TableResultResponse getNotEnabledSensorList(Query query, Integer channelId, Integer buildingId,Integer floor) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("channelId",channelId);
        map.put("buildingId",buildingId);
        map.put("floor",floor);
        map.put("status","3");
        List<Map<String,String>> list = deviceSensorMapper.getNotEnabledSensorList(map);
        JSONObject jsonObject = iUserFeign.selectById(channelId);
        for(Map<String,String> sensorMap:list){
            sensorMap.put("channelName",jsonObject.getJSONObject("data").getString("channelName"));
            if(StringUtils.isNotBlank(sensorMap.get("positionSign"))){
                sensorMap.put("positionSign","true");
            }else{
                sensorMap.put("positionSign","false");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public TableResultResponse getUnlabeledSensorList(Query query, Integer channelId, Integer buildingId, Integer floor) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("channelId",channelId);
        map.put("buildingId",buildingId);
        map.put("floor",floor);
        List<Map<String,String>> list = deviceSensorMapper.getUnlabeledSensorList(map);
        return new TableResultResponse(result.getTotal(),list);
    }

    //传感器系列删除前查询1.5
    public List<String> deleteSensorTypeQuery(Integer id){
        return mapper.deleteSensorTypeQuery(id);
    }

    //测点删除前查询1.5
    public List<String> deleteMPQuery(Integer id){
        return mapper.deleteMPQuery(id);
    }


    public List<String> getManufacturerChannelId(Integer channelId){
        return mapper.getManufacturerChannelId(channelId);
    }

    public List<String> getEquipmentTypeChannelId(Integer channelId){
        return   mapper.getEquipmentTypeChannelId(channelId);
    }

    //根据id判断传感器是否已经被删除
    public Integer queryIdIsDel(Long id){
        return mapper.queryIdIsDel(id);
    }

    public TableResultResponse selectByHydrantIdGetSensor(Query query, Integer channelId, Integer hydrantId, String statusName) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceSensor deviceSensor = new DeviceSensor();
        deviceSensor.setHydrantId(hydrantId);
        deviceSensor.setChannelId(channelId);
        deviceSensor.setStatus(statusName);
        List<Map> list = deviceSensorMapper.selectByHydrantIdGetSensor(deviceSensor);
        for(int i=0;i<list.size();i++){
            Map temp = list.get(i);
//            Integer chid = (Integer)temp.get("channelId");
//            if(chid==null){
//                new RuntimeException("所属系统找不到！");
//            }
//            JSONObject a= iUserFeign.selectById(chid);
//            JSONObject data = a.getJSONObject("data");
//            if(data==null){
//                temp.put("channelName", "无");
//            }else {
//                temp.put("channelName", data.get("channelName"));
//            }
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
            }else if("4".equals(status)){
                temp.put("status","离线");
                temp.put("color","#F5A623");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public TableResultResponse getNotEnabledSensorListByHydrantId(Query query, Integer channelId, Integer hydrantId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("channelId",channelId);
        map.put("hydrantId",hydrantId);
        map.put("status","3");
        List<Map<String,String>> list = deviceSensorMapper.getNotEnabledSensorListByHydrantId(map);
//        JSONObject jsonObject = iUserFeign.selectById(channelId);
//        for(Map<String,String> sensorMap:list){
//            sensorMap.put("channelName",jsonObject.getJSONObject("data").getString("channelName"));
//        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public CountVo getCountByStatus(Integer buildId, Integer floor, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        return deviceSensorMapper.getCountByStatus(map);
    }

    public Integer getStatusCount(Integer buildId, Integer floor, Integer channelId, String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("status",status);
        return deviceSensorMapper.getStatusCount(map);
    }


    /**
     * 通过ID批量查询设备信息
     * @param ids
     * @return
     */
    public List<Map<String,Object>> queryByIds(String ids, String manufacturer, String equipmentType,String model,String sensorNo,Long buildingId,
                                               String excludeIds,Integer channelId,String code){

        return deviceSensorMapper.queryByIds(parseStringToLongArry(ids), manufacturer, equipmentType,model,sensorNo,buildingId,
                parseStringToLongArry(excludeIds),channelId,code);
    }

    /**
     * 查询室外防火栓对应的传感器
     * @param ids
     * @param manufacturer
     * @param equipmentType
     * @param model
     * @param sensorNo
     * @param excludeIds
     * @param channelId
     * @param hids
     * @return
     */
    public List<Map<String,Object>> queryOutdoorSensorByIds(String ids, String manufacturer, String equipmentType,String model,String sensorNo,
                                               String excludeIds,Integer channelId,List<Integer> hids){
        return deviceSensorMapper.queryOutdoorSensorByIds(parseStringToLongArry(ids), manufacturer, equipmentType,model,sensorNo,
                parseStringToLongArry(excludeIds),channelId,hids);
    }

    @Transactional
    public void saveSensorWithKeyReturn(DeviceSensor deviceSensor){
        EntityUtils.setCreatAndUpdatInfo(deviceSensor);
       deviceSensorMapper.saveSensorWithKeyReturn(deviceSensor);
    }

    public List<Map<String,Object>> queryNestedSensorByIds(String ids, String manufacturer, String equipmentType,String model,String sensorNo,
                                                            String excludeIds,Integer channelId,Long groupId){
        return deviceSensorMapper.queryNestedSensorByIds(parseStringToLongArry(ids), manufacturer, equipmentType,model,sensorNo,
                parseStringToLongArry(excludeIds),channelId,groupId);
    }

    private Long[] parseStringToLongArry(String ids){
        Long[] lids = null;
        if(StringUtils.isNotBlank(ids)){
            String[] arr = ids.split(",");
            lids = new Long[arr.length];
            for(int i = 0; i < arr.length; i++){
                lids[i] = Long.valueOf(arr[i]);
            }
        }
        return lids;
    }


    public List<DeviceSensor> queryByBuildings(List<Integer> buildingIds){
        return mapper.queryByBuildings(buildingIds);
    }

    /**
     * 更新设备离线状态
     * @param sensorNos 设备编号
     * @param isOffline true: 将设备状态修改为离线；false: 将离线状态的设备修改为正常状态
     * @return
     */
    public int updateOfflineStatus(List<String> sensorNos,Boolean isOffline){
        return mapper.updateOfflineStatus(sensorNos,isOffline,new Date());
    }

    /**
     * 获取所有设备的状态信息
     * @return
     */
    public List<Map<String,Object>> getAllStatus(Integer channelId){
        return mapper.selectAllStatus(channelId);
    }

    public List<DeviceMeasuringPoint> selectMeasuringPointById(Long id){
        return mapper.selectMeasuringPointById(id);
    }
    public List<SensorStatusVO> getSensorTypeStatus(Integer channelId){
        List<SensorStatusVO> rs=new ArrayList<>();
        List<SensorTypeStatus> list = mapper.getSensorTypeStatus(channelId);
        Map<String, List<SensorTypeStatus>> collect = list.stream().collect(Collectors.groupingBy(SensorTypeStatus::getEquipmentType));
        collect.forEach((k,v)->{
            SensorStatusVO s=new SensorStatusVO();
            s.setEquipmentType(k);
            for (SensorTypeStatus sensor:v) {
                //'状态[0=故障/1=报警/2=正常/3=未启用/4=离线
                if(sensor.getStatus().equals("0")){
                    s.setFaultNum(sensor.getNum());
                }else if(sensor.getStatus().equals("1")){
                    s.setWarningNum(sensor.getNum());
                }else if(sensor.getStatus().equals("2")){
                    s.setNormalNum(sensor.getNum());
                }else if(sensor.getStatus().equals("3")){
                    s.setNotUsedNum(sensor.getNum());
                }else if(sensor.getStatus().equals("4")){
                    s.setOffLineNum(sensor.getNum());
                }
            }
            rs.add(s);
        });
        return rs;
    }
   public List<SensorTypeStatus> getSensorStatusNum(){
       List<SensorTypeStatus> list = mapper.getSensorStatusNum();
       if(CollectionUtils.isNotEmpty(list)){
           int sum = list.stream().mapToInt(SensorTypeStatus::getNum).sum();
           if(sum>0){
               for (SensorTypeStatus sensor: list) {
                   sensor.setPercent(new BigDecimal(1.0 * sensor.getNum()/sum).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
                   sensor.setStatusName(SensorStatusEnum.getMessageByType(Integer.parseInt(sensor.getStatus())));
               }
           }else {
               for (SensorTypeStatus sensor: list) {
                  sensor.setPercent(0.0);
                   sensor.setStatusName(SensorStatusEnum.getMessageByType(Integer.parseInt(sensor.getStatus())));
               }
           }
       }
       return list;
   }

    public List<SensorStatusVO> getChannelSensorStatusNum(){
        List<SensorStatusVO> rs=new ArrayList<>();
        List<SensorTypeStatus> list = mapper.getChannelSensorStatusNum();
        Set<Integer> sets = list.stream().map(x -> x.getChannelId()).collect(Collectors.toSet());
        List<Map> channelDtos = getList(sets);
        Map<Integer, List<SensorTypeStatus>> collect = list.stream().collect(Collectors.groupingBy(SensorTypeStatus::getChannelId));
        collect.forEach((k,v)->{
            SensorStatusVO s=new SensorStatusVO();
            s.setChannelId(k);
            for (Map m :channelDtos) {
                if(m.get("id").equals(k)){
                    s.setChannelName(m.get("channelName").toString());
                    break;
                }
            }
            for (SensorTypeStatus sensor:v) {
                //'状态[0=故障/1=报警/2=正常/3=未启用/4=离线
                if(sensor.getStatus().equals("0")){
                    s.setFaultNum(sensor.getNum());
                }else if(sensor.getStatus().equals("1")){
                    s.setWarningNum(sensor.getNum());
                }else if(sensor.getStatus().equals("2")){
                    s.setNormalNum(sensor.getNum());
                }else if(sensor.getStatus().equals("3")){
                    s.setNotUsedNum(sensor.getNum());
                } else if(sensor.getStatus().equals("4")){
                    s.setOffLineNum(sensor.getNum());
                }
            }
            rs.add(s);
        });
        return rs;
    }
    public List<Map> getList(Set<Integer> list){
        String ids = StringUtils.join(list.toArray(), ",");
        ObjectRestResponse response = adminFeign.getByIds(ids);
        List<Map> data = (List<Map>) response.getData();
        return data;
    }

}