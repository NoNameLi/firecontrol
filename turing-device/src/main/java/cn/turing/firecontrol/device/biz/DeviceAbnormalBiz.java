package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.DateUtil;
import cn.turing.firecontrol.device.vo.ResultVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.turing.firecontrol.device.mapper.DeviceAbnormalMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import java.util.*;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceAbnormalBiz extends BusinessBiz<DeviceAbnormalMapper,DeviceAbnormal> {

    @Autowired
    private DeviceAbnormalMapper deviceAbnormalMapper;

    @Autowired
    private DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;
    @Autowired
    private DeviceSensorTypeBiz dstBiz;
    @Autowired
    private DeviceAlarmThresholdBiz datBiz;
    @Autowired
    private DeviceAlarmLevelBiz dalBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmp;

    public TableResultResponse<DeviceAbnormal> selectQuery(Query query, DeviceAbnormal deviceAbnormal, String startDate, String endDate,String tag,String bName,String floorId,String sensorNo,String positionDescription,String equipmentType,Integer channelId){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        if(tag.equals("0")){//当前报警记录
            deviceAbnormal.setAlrmCategory(null);
            deviceAbnormal.setHandleFlag("0");
            //map.put("deviceAbnormal",deviceAbnormal);
        }
        if(tag.equals("1")){//火警报警记录
            deviceAbnormal.setAlrmCategory("1");
            deviceAbnormal.setHandleFlag("1");
            //map.put("deviceAbnormal",deviceAbnormal);
        }
        if(tag.equals("2")){//故障报警记录
            deviceAbnormal.setAlrmCategory("0");
            deviceAbnormal.setHandleFlag("1");
            //map.put("deviceAbnormal",deviceAbnormal);
        }
        if(StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)){
            map.put("startDate",null);
            map.put("endDate",null);
        }else {
            map.put("startDate", startDate+" 00:00:00");
            map.put("endDate", endDate+" 23:59:59");
        }
        deviceAbnormal.setbName(bName);
        if(deviceAbnormal.getFloor()==null&&StringUtils.isNotBlank(floorId)){
            try {
                deviceAbnormal.setFloor(Integer.parseInt(floorId));
            }catch (Exception e){
                return new TableResultResponse<DeviceAbnormal>();
            }
        }
        deviceAbnormal.setSensorNo(sensorNo);
        deviceAbnormal.setPositionDescription(positionDescription);
        deviceAbnormal.setEquipmentType(equipmentType);
        deviceAbnormal.setChannelId(channelId);
        map.put("deviceAbnormal",deviceAbnormal);
        List<DeviceAbnormal> list = deviceAbnormalMapper.selectQuery(map);
        for(DeviceAbnormal abnormal:list){
            if(abnormal.getHandleFlag().equals("0")){
                abnormal.setHandleFlag("false");
            }
            if(abnormal.getHandleFlag().equals("1")){
                abnormal.setHandleFlag("true");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public List<String> selectAlrmType(Integer channelId,String alrmCategory,String handleFlag) {
        Map<String ,Object> map = new HashMap<>();
        map.put("channelId",channelId);
        map.put("handleFlag",handleFlag);
        map.put("alrmCategory",alrmCategory);
        return deviceAbnormalMapper.selectAlrmType(map);
    }

    public Integer selectCountByFlag(String flag) {
        return deviceAbnormalMapper.selectCountByFlag(flag);
    }

    public List<DeviceAbnormal> selectCountByBuildId(Date startDate, Date endDate, Integer id,String alrmCategory) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("id",id);
        map.put("alrmCategory",alrmCategory);
        return deviceAbnormalMapper.selectCountByBuildId(map);
    }

    public Integer selectCountByType(Date startDate, Date endDate,String alrmType) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alrmType",alrmType);
        return deviceAbnormalMapper.selectCountByType(map);
    }

    public Integer selectCountByMonth(String date,String alrmcategory,Integer channelId){
        Map<String ,Object> map = new HashMap<>();
        map.put("date",date);
        map.put("alrmcategory",alrmcategory);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectCountByMonth(map);
    }

    public List<DeviceAbnormal> selectByEquId(long equId,String handleFlag,String codeName) {
        Map<String ,Object> map = new HashMap<>();
        map.put("equId",equId);
        map.put("handleFlag",handleFlag);
        map.put("codeName",codeName);
        return deviceAbnormalMapper.selectByEquId(map);
    }

    public Integer getCountByToday(Date startDate, Date endDate, String alemCategory,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alemCategory",alemCategory);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.getCountByToday(map);
    }

    public List<DeviceAbnormal> selectAlrm(String handleFlag,Date startDate, Date endDate,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectAlrm(map);
    }

    public List<DeviceAbnormal> selectByBuildId(Integer id) {
        return deviceAbnormalMapper.selectByBuildId(id);
    }

    public int selectCountByAlrmCategoryAndBuildId(String alrmCategory, Integer buildId) {
        return deviceAbnormalMapper.selectCountByAlrmCategoryAndBuildId(alrmCategory,buildId);
    }

    public List selectByEquIdAndBuildId(Long equId, Integer buildId) {
        return deviceAbnormalMapper.selectByEquIdAndBuildId(equId,buildId);
    }

    public List<ResultVo> selectByYear(Integer year, String alemCategory) {
        return deviceAbnormalMapper.selectByYear(year,alemCategory);
    }

    public List<DeviceAbnormal> selectByBuildingId(Integer buildId) {
        return  deviceAbnormalMapper.selectByBuildingId(buildId);
    }

    public List<String> selectAlrmBySensorId(List list,String alrmCategory,String handleFlag) {
        Map<String ,Object> map = new HashMap<>();
        map.put("list",list);
        map.put("alrmCategory",alrmCategory);
        map.put("handleFlag",handleFlag);
        return deviceAbnormalMapper.selectAlrmBySensorId(map);
    }

    public Integer selectAlrmCountByDate(String date, Integer buildId, String alrmcategory,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("date",date);
        map.put("buildId",buildId);
        map.put("alrmcategory",alrmcategory);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectAlrmCountByDate(map);
    }

    // 获取所有的未处理的记录
    public List<Map> selectByHandelFlagAndAlrm(String alrmcategory,String handleFlag,Integer channelId,String tenantId){
//        return deviceAbnormalMapper.selectByHandelFlagAndAlrm(alrmcategory,handleFlag,channelId);
        Map map =new HashMap();
        DeviceAbnormal deviceAbnormal = new DeviceAbnormal();
        deviceAbnormal.setAlrmCategory(alrmcategory);
        deviceAbnormal.setHandleFlag(handleFlag);
        deviceAbnormal.setTenantId(tenantId);
        map.put("deviceAbnormal",deviceAbnormal);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectByHandelFlagAndAlrm(map);
    }

    @Override
    public void insertSelective(DeviceAbnormal entity) {
        mapper.insertSelective(entity);
    }

    public TableResultResponse selectAlrm(Query query, String handleFlag,String alrmCategory,Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map map =new HashMap();
        map.put("handleFlag",handleFlag);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        List<Map<String,Object>> resultList = new ArrayList();
        List<DeviceAbnormal> list = deviceAbnormalMapper.selectAlrm(map);
        for(DeviceAbnormal deviceAbnormal:list){
            DeviceMeasuringPoint deviceMeasuringPoint = dmp.selectByCodeName(deviceAbnormal.getUnit());
            Map map1 = new HashMap();
            map1.put("sensorId",deviceAbnormal.getEquId());//传感器id
            map1.put("alrmId",deviceAbnormal.getId());//异常记录id
            map1.put("buildId",deviceAbnormal.getBuildId());//建筑id
            map1.put("name",deviceAbnormal.getbName());
            map1.put("type",deviceAbnormal.getEquipmentType());
            map1.put("positionDescription",deviceAbnormal.getFloor()+"F-"+deviceAbnormal.getPositionDescription());
            map1.put("alrmType1",deviceAbnormal.getAlrmType());
            map1.put("fireFlag",deviceAbnormal.getFireFlag());
            map1.put("handleFlag",deviceAbnormal.getHandleFlag());
            map1.put("alrmCegory",deviceAbnormal.getAlrmCategory());
            map1.put("date", DateUtil.getFriendlytime(deviceAbnormal.getAlrmDate()));
            if(deviceMeasuringPoint!=null){
                map1.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
                map1.put("alrmType2",deviceAbnormal.getAlrmData()+deviceMeasuringPoint.getDataUnit());
            }else{
                break;
            }
            if( deviceAbnormal.getAlrmCategory().equals("1")){
                map1.put("alrmType3",deviceAbnormal.getLevel());
            }
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    public Integer selectCountByAlrmCategoryAndFireFlag(String fireFlag, String alrmCategory, String handleFlag,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("fireFlag",fireFlag);
        map.put("alrmCategory",alrmCategory);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectCountByAlrmCategoryAndFireFlag(map);
    }

    public List<ResultVo> selectCountByDate(Date startDate,Date endDate,Integer channelId,String alrmCategory) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("channelId",channelId);
        map.put("alrmCategory",alrmCategory);
        return deviceAbnormalMapper.selectCountByDate(map);
    }

    public Integer selectCountByBuildIdAndDate(Date startDate, Date endDate, String bName, String alrmCategory,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("bName",bName);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectCountByBuildIdAndDate(map);
    }

    //实时数据历史纪录
    public List<Map<String,Object>>  selectByEquIdResultMP(DeviceAbnormal deviceAbnormal){
        return mapper.selectByEquIdResultMP(deviceAbnormal);
    }

    public Integer selectCountByEquId(Long id, String alrmCategory) {
        Map<String ,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("alrmCategory",alrmCategory);
        return deviceAbnormalMapper.selectCountByEquId(map);
    }

    public Integer getCountByHandleFlag(Date startDate, Date endDate,String handleFlag,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.getCountByHandleFlag(map);
    }

    public Integer selectCountBySensorNo(String sensorNo, String alrmCategory) {
        Map<String ,Object> map = new HashMap<>();
        map.put("sensorNo",sensorNo);
        map.put("alrmCategory",alrmCategory);
        return deviceAbnormalMapper.selectCountBySensorNo(map);
    }


}