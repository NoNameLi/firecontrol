package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.enums.AlarmStatusEnum;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.AbstractAbnormalHandler;
import cn.turing.firecontrol.datahandler.mapper.DeviceAbnormalMapper;
import cn.turing.firecontrol.datahandler.mapper.DeviceFireMainAbnormalMapper;
import cn.turing.firecontrol.datahandler.util.Constants;
import cn.turing.firecontrol.datahandler.util.DateUtil;
import cn.turing.firecontrol.datahandler.vo.*;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
@Slf4j
public class DeviceAbnormalBiz extends BusinessBiz<DeviceAbnormalMapper,DeviceAbnormal> {

    @Autowired
    private DeviceAbnormalMapper deviceAbnormalMapper;
    @Autowired
    private IDeviceFeign deviceFeign;
    @Autowired
    private DeviceFireMainAbnormalMapper deviceFireMainAbnormalMapper;
    public TableResultResponse<DeviceAbnormal> selectQuery(Query query, DeviceAbnormal deviceAbnormal, String startDate, String endDate, String tag, String bName, String floorId, String sensorNo, String positionDescription, String equipmentType,Integer channelId){
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
        if("全部".equals(bName)){
            bName =null;
        }
        if("全部".equals(equipmentType)){
            equipmentType =null;
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
        deviceAbnormal.setTenantId(null);
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

    public List<String> selectAlrmType(Integer channelId,String alrmCategory,String handleFlag,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("channelId",channelId);
        map.put("handleFlag",handleFlag);
        map.put("alrmCategory",alrmCategory);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.selectAlrmType(map);
    }

    public Integer selectCountByFlag(String flag) {
        return deviceAbnormalMapper.selectCountByFlag(flag);
    }

    public List<DeviceAbnormal> selectCountByBuildId(Date startDate, Date endDate, Integer id, String alrmCategory) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("id",id);
        map.put("alrmCategory",alrmCategory);
        return deviceAbnormalMapper.selectCountByBuildId(map);
    }

    public Integer selectCountByType(Date startDate, Date endDate,String alrmType,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alrmType",alrmType);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.selectCountByType(map);
    }

    public Integer selectCountByMonth(String date,String alrmcategory,Integer channelId){
        Map<String ,Object> map = new HashMap<>();
        map.put("date",date);
        map.put("alrmcategory",alrmcategory);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectCountByMonth(map);
    }

    public List<DeviceAbnormal> selectByEquId(long equId, String handleFlag, String codeName,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("equId",equId);
        map.put("handleFlag",handleFlag);
        map.put("codeName",codeName);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.selectByEquId(map);
    }

    public Integer getCountByToday(Date startDate, Date endDate, String alrmCategory,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.getCountByToday(map);
    }

    public List<DeviceAbnormal> selectAlrm(String handleFlag, Date startDate, Date endDate, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
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

    public Integer selectAlrmCountByDate(Date startDate,Date endDate, Integer buildId, String alrmcategory,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("buildId",buildId);
        map.put("alrmcategory",alrmcategory);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.selectAlrmCountByDate(map);
    }

    // 获取所有的未处理的记录
    public List<Map> selectByHandelFlagAndAlrm(String alrmcategory,String handleFlag,Integer channelId,String tenantID){
//        return deviceAbnormalMapper.selectByHandelFlagAndAlrm(alrmcategory,handleFlag,channelId);
        Map map =new HashMap();
        DeviceAbnormal deviceAbnormal = new DeviceAbnormal();
        deviceAbnormal.setAlrmCategory(alrmcategory);
        deviceAbnormal.setHandleFlag(handleFlag);
        deviceAbnormal.setTenantId(tenantID);
        map.put("deviceAbnormal",deviceAbnormal);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectByHandelFlagAndAlrm(map);
    }

    @Override
    public void insertSelective(DeviceAbnormal entity) {
        mapper.insertSelective(entity);
    }

    public TableResultResponse selectAlrm(Query query, String handleFlag, String alrmCategory, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map map =new HashMap();
        map.put("handleFlag",handleFlag);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        map.put("tenantId",null);
        List<Map<String,Object>> resultList = new ArrayList();
        List<DeviceAbnormal> list = deviceAbnormalMapper.selectAlrm(map);
        for(DeviceAbnormal deviceAbnormal:list){
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
            map1.put("measuringPoint",deviceAbnormal.getMeasuringPoint());
            if(deviceAbnormal.getUnit()!=null){
                map1.put("alrmType2",deviceAbnormal.getAlrmData()+deviceAbnormal.getDataUnit());
            }else{
                map1.put("alrmType2",deviceAbnormal.getAlrmData());
            }
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    public Integer selectCountByAlrmCategoryAndFireFlag(String fireFlag, String alrmCategory, String handleFlag,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("fireFlag",fireFlag);
        map.put("alrmCategory",alrmCategory);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
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

    public Integer selectCountByBuildIdAndDate(Date startDate, Date endDate, String bName, String alrmCategory,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("bName",bName);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.selectCountByBuildIdAndDate(map);
    }
    public List<Map> selectTopCountByBuildIdAndDate(Date startDate, Date endDate, String alrmCategory,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.selectTopCountByBuildIdAndDate(map);
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

    public Integer getCountByHandleFlag(Date startDate, Date endDate,String handleFlag,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.getCountByHandleFlag(map);
    }

    public List<Integer> add(List<DeviceAbnormal> list){
        List<Integer> idList = new LinkedList();
        if(list.size()==0){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            for(DeviceAbnormal deviceAbnormal:list){
                mapper.insertSelective(deviceAbnormal);
                idList.add(deviceAbnormal.getId());
            }
        }
        return idList;
    }

    public TableResultResponse selectAbnormal(Query query, Integer buildId, Integer floor, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("tenantId",BaseContextHandler.getTenantID());
        List<AbnormalVo> list = deviceAbnormalMapper.selectAbnormal(map);
        return new TableResultResponse(result.getTotal(),list);
    }

    public Integer selectCountBySensorNo(String sensorNo, String alrmCategory,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("sensorNo",sensorNo);
        map.put("alrmCategory",alrmCategory);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.selectCountBySensorNo(map);
    }

    public List<Map<String,Object>> getAbnormalByTheLatestTen(String handleFlag, String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("tenantId",tenantId);
        return deviceAbnormalMapper.getAbnormalByTheLatestTen(map);
    }

    public Integer selectCountByDateAndHandle(Date startDate, Date endDate, String handleFlag, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceAbnormalMapper.selectCountByDateAndHandle(map);
    }

    public List<String> getBNameByDate(String startDate, String endDate) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        return deviceAbnormalMapper.getBNameByDate(map);
    }

    public Map<String,Object> selectCountByDeviceSeriesAndBuilding(Date startTime,Date endTime){
        Map<String,Object> result = new HashMap<>();
        //获取建筑物列表
        List<DeviceBuilding> buildingList =  deviceFeign.getBuildings().getData();
        if(buildingList == null||buildingList.isEmpty()){
            return result;
        }
        String[] buildings = new String[buildingList.size()];
        DeviceBuilding building = null;
        for(int i = 0;i < buildingList.size();i++){
            building = buildingList.get(i);
            buildings[i] = building.getBName();
        }
        result.put("buildings",buildings);
        //获取设备系列列表
        List<DeviceSensorType> types = deviceFeign.queryTypeByTenant().getData();
        String[] devcieSeries = new String[types.size()];
        for(int i = 0;i<types.size();i++){
            devcieSeries[i] = types.get(i).getEquipmentType();
        }
        result.put("devcieSeries",devcieSeries);
        //获取数量
        long[][] counts = new long[devcieSeries.length][buildings.length];
        List<Map<String,Object>> list = mapper.selectCountByDeviceSeriesAndBuilding(startTime,endTime);
        String bName = null,equipmentType = null;
        long count = 0;
        for(Map<String,Object> m : list){
            bName = m.get("bName").toString();
            equipmentType = m.get("equipmentType").toString();
            count = Long.valueOf(m.getOrDefault("counts",0).toString());
            for(int i=0;i<devcieSeries.length;i++){
                if(!devcieSeries[i].equals(equipmentType)){
                    continue;
                }
                for(int j=0; j < buildings.length ; j++){
                    if(buildings[j].equals(bName)){
                        counts[i][j] = count;
                    }
                }
            }
        }
        result.put("counts",counts);
        return result;
    }

    public Map<String,Long> selectUnhandleCounts(Date startTime,Date endTime){
        Map<String,Long> result = new HashMap<>();
        result.put("malfunction",0L);
        result.put("alarm",0L);
        List<Map<String,Object>> list = mapper.selectUnhandleCounts(startTime,endTime);
        String alarmCategory = null;
        Long counts = 0L;
        for(Map<String,Object> m : list){
            alarmCategory = m.get("alarmCategory").toString();
            Object count = m.get("counts");
            if(count != null && StringUtils.isNotBlank(count.toString())){
                counts = Long.valueOf(count.toString());
            }
            if("0".equals(alarmCategory)){
                result.put("malfunction",counts);
            }else if("1".equals(alarmCategory)){
                result.put("alarm",counts);
            }
        }
        return result;
    }

    public List<Map<String,Object>> selectCountBySensor(Date startTime,Date endTime,Integer limit){
        List<Map<String,Object>> list = mapper.selectCountBySensor(startTime,endTime,limit);
        return list;
    }

    public Map<String,Map<String,Long>> selectCountByDay(Date startTime,Date endTime){
        Map<String,Map<String,Long>> result = new HashMap<>();
        List<Map<String,Object>> list = mapper.selectCountByDay(startTime,endTime);
        //将list转换成map
        Map<String,Long> gCountMap = new HashMap<>();
        Map<String,Long> bCountMap = new HashMap<>();
        List<Map<String,Object>> datas = null;
        for(Map<String,Object> m : list){
            String alarmCategory = m.get("alarmCategory").toString();
            datas = (ArrayList)m.get("data");
            if("0".equals(alarmCategory)){
                for(Map<String,Object> n : datas){
                    gCountMap.put(n.get("alarmDate").toString(),Long.valueOf(n.get("count").toString()));
                }
            }else if("1".equals(alarmCategory)){
                for(Map<String,Object> n : datas){
                    bCountMap.put(n.get("alarmDate").toString(),Long.valueOf(n.get("count").toString()));
                }
            }
        }
        result.put("0",gCountMap);
        result.put("1",bCountMap);
        return  result;
    }

    //根据异常类型（故障、报警）查询异常列表
    public List<DeviceAbnormal> selectByCategory(Date startTime,Date endTime,String alarmCategory){
        return mapper.selectByCategory(startTime,endTime,alarmCategory);
    }

    //根据异常类型（故障、报警）查询各报警类型的数量
    public List<Map<String,Object>> selectTypeCountByCategory(Date startTime,Date endTime,String alarmCategory){
        return mapper.selectTypeCountByCategory(startTime,endTime,alarmCategory);
    }

    /**
     * 恢复测点的异常：1、设置恢复时间，2、修改处理状态为已处理
     * @param sensorNo 传感器编号
     * @param alarms 测点代号
     */
    public void restore(String sensorNo,List<AbstractAbnormalHandler.Alarm> alarms){
        mapper.restoreAbnormal(sensorNo,alarms,new Date());
    }


    /**
     * 查询最近几个月的个状态异常数（报警，误报，故障）（不包含未确认的报警信息）
     * @param count
     * @return
     */
    public Map<String,Object[]> selectCountNearlyMonth(int count,Integer channelId){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,(count-1) * -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String startMonth = dateFormat.format(calendar.getTime());
        dateFormat = new SimpleDateFormat("yyyyMM");
        String[] months = new String[count];
        for(int i=0; i<count ;i++){
            if(i != 0){
                calendar.add(Calendar.MONTH,1);
            }
            months[i] = dateFormat.format(calendar.getTime());
        }

        List<Map<String,Object>> list = deviceAbnormalMapper.selectCountNearlyMonth(startMonth,channelId);
        Long[] faultCount = new Long[count];
        Arrays.fill(faultCount,0L);
        Long[] alarmCount = new Long[count];
        Arrays.fill(alarmCount,0L);
        Long[] notAlarmCount = new Long[count];
        Arrays.fill(notAlarmCount,0L);
        list.parallelStream().forEach(map -> {
            int index = Arrays.binarySearch(months,map.get("month"));
            if(index < 0){
                return;
            }
            long counts = (long)map.get("counts");
            if("0".equals(map.get("alarmCategory"))){
                faultCount[index] += counts;
            }else{
                if("1".equals(map.get("fireFlag"))){
                    alarmCount[index] += counts;
                }else if("0".equals(map.get("fireFlag"))){
                    notAlarmCount[index] += counts;
                }
            }
        });
        Map<String,Object[]> map = new HashMap<>();
        map.put("months",months);
        map.put("faultCount",faultCount);
        map.put("alarmCount",alarmCount);
        map.put("notAlarmCount",notAlarmCount);
        return map;
    }

 public List<TypeNumVO> selectFireFlagCount(Integer channelId){
        //2=火警测试/1=火警/0=误报
     List<TypeNumVO> typeNumVOS=new ArrayList<>();
      typeNumVOS = mapper.selectFireFlagCount(channelId);
     if(CollectionUtils.isNotEmpty(typeNumVOS)){
         long sum = typeNumVOS.stream().mapToLong(TypeNumVO::getNum).sum();
         if(sum>0){
             for (TypeNumVO t:typeNumVOS) {
                 t.setTypeName(AlarmStatusEnum.getMessageByType(Integer.parseInt(t.getType())));
                 t.setPercent(new BigDecimal(1.0 * t.getNum()/sum).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
             }
         }
     }else {
         typeNumVOS.add(new TypeNumVO("0","误报",0.0)) ;
         typeNumVOS.add(new TypeNumVO("1","火警",0.0)) ;
         typeNumVOS.add(new TypeNumVO("2","测试",0.0)) ;
     }
     DeviceAbnormal p2=new DeviceAbnormal();
     p2.setDelFlag("0");
     p2.setChannelId(channelId);
     Long total = selectCount(p2);
     log.info("报警总数;{}",total);
     if(total>0){
         DeviceAbnormal p1=new DeviceAbnormal();
         p1.setHandleFlag("1");
         p1.setDelFlag("0");
         p1.setChannelId(channelId);
         Long aLong = selectCount(p1);
         log.info("处理总数;{}",aLong);
         typeNumVOS.add(new TypeNumVO("3","报警处理率",new BigDecimal(1.0 * aLong/total).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()));
     }else {
         typeNumVOS.add(new TypeNumVO("3","报警处理率",0.0)) ;
     }
     return typeNumVOS;
 }




    public Map<String,Object[]> selectCountNearlyDate(int count,Integer channelId,Integer buildId){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,(count-1) * -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = dateFormat.format(calendar.getTime());
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String[] dates = new String[count];
        for(int i=0; i<count ;i++){
            if(i != 0){
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }
            dates[i] = dateFormat.format(calendar.getTime());
        }
        List<Map<String,Object>> list=null;
 /*       if(channelId==null){
            list=deviceAbnormalMapper.selectAllCountNearlyDate(startDate);
           
        }else*/ if(channelId!=null && channelId==11){
            list= deviceFireMainAbnormalMapper.selectCountNearlyDate(startDate, channelId, buildId);
        }else {
            list = deviceAbnormalMapper.selectCountNearlyDate(startDate,channelId,buildId);
        }
        
        Long[] faultCount = new Long[count];
        Arrays.fill(faultCount,0L);
        Long[] alarmCount = new Long[count];
        Arrays.fill(alarmCount,0L);
        Long[] notAlarmCount = new Long[count];
        Arrays.fill(notAlarmCount,0L);
        SimpleDateFormat finalDateFormat = dateFormat;
        list.parallelStream().forEach(map -> {
            int index = Arrays.binarySearch(dates, finalDateFormat.format(map.get("alrmDate")));
            if(index < 0){
                return;
            }
            long counts = (long)map.get("counts");
            if("0".equals(map.get("alarmCategory"))){
                faultCount[index] += counts;
            }else{
                if("1".equals(map.get("fireFlag"))){
                    alarmCount[index] += counts;
                }else if("0".equals(map.get("fireFlag"))){
                    notAlarmCount[index] += counts;
                }
            }
        });
        Map<String,Object[]> map = new HashMap<>();
        map.put("dates",dates);
        map.put("faultCount",faultCount);
        map.put("alarmCount",alarmCount);
        map.put("notAlarmCount",notAlarmCount);
        return map;
    }

    /**
     * 查询设备的最后一条异常记录
     * @param deviceId
     * @return
     */
    public DeviceAbnormal selectLast(Long deviceId){
        return mapper.selectLast(deviceId);
    }


    public List<DeviceAbnormalVo> selectList(Integer channelId,Long deviceId,Integer buildId,Integer floor){
        return mapper.selectList(channelId,deviceId,buildId,floor);
    }

    public Integer getCountByCond(String handleFlag,Integer channelId){
        return mapper.getCountByCond(handleFlag,channelId);
    }


    /**
     * 查询最近N天分周别时段的报警数据
     * @param count
     * @return
     */
    public List<Map<String,Object>> selectCountByWeekAndHour(Integer count){
        Date startTime = DateUtil.getStartBefore(count-1);
        int startWeekDay = DateUtil.getWeekDay(startTime);
        String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};
        Map<String,Integer> countMap = new HashMap<>();
        List<Map<String,Integer>> counts = mapper.selectCountByWeekAndHour(startTime);
        counts.parallelStream().forEach(m -> {
            int weekday = Integer.valueOf(m.get("myweek").toString());
            countMap.put(weeks[(weekday + 1)%7] + m.get("myhour"),m.get("count"));
        });
        List<Map<String,Object>> res = new ArrayList<>();
        Map<String,Object> map = null;
        for(int i=0; i<count; i++){
            for(int j=0; j<24; j++){
                String week = weeks[(startWeekDay -1 + i)%7];
                map = new HashMap<>();
                map.put("week",week);
                map.put("hour",j);
                if(countMap.containsKey(week + j)){
                    map.put("count",countMap.get(week + j));
                }else{
                    map.put("count",null);
                }
                res.add(map);
            }
        }
        return res;
    }
   public List<DeviceAbnormalVo> getLatestList(Integer channelId){
        Map<String,Object> map=new HashMap<>();
       DateTime now = DateTime.now();
        map.put("channelId",channelId);
        map.put("start",now.minusSeconds(30).toDate());
        map.put("end",now.toDate());
        map.put("tenantId",BaseContextHandler.getTenantID());
        return mapper.getLatestList(map);
    }
   public List<TypeNumVO> getAlrmNumByDate(String startDate,String endDate){
       List<TypeNumVO> rs =new ArrayList<>();
        //0：故障，1：火警',
       Map<String,Object> map=new HashMap<>();
       map.put("startDate",startDate);
       map.put("endDate",endDate);
       map.put("category",0);
       Integer alrmNum = mapper.getAlrmNumByDate(map);
       map.put("category",1);
       Integer falutNum = mapper.getAlrmNumByDate(map);

       Integer handleNum = mapper.getHandleNumByDate(map);
       rs.add(new TypeNumVO("alrmNum",alrmNum)) ;
       rs.add(new TypeNumVO("falutNum",falutNum)) ;
       rs.add(new TypeNumVO("handleNum",handleNum));
       return rs;
   }
   public List<TypeNumVO> countNumByCategoryAndHandle(Integer buildId,Integer channelId){
        //0：故障，1：火警'
       List<TypeNumVO> rs =new ArrayList<>();
       Map<String,Object> map=new HashMap<>();
       map.put("buildId",buildId);
       map.put("channelId",channelId);
       List<AbnormalDTO> abnormalDTOS = mapper.countNumByCategoryAndHandle(map);
       int fault1=0;
       int fault0=0;
       int fire1=0;
       int fire0=0;
       if(CollectionUtils.isNotEmpty(abnormalDTOS)){
           for (AbnormalDTO x : abnormalDTOS) {
               if(x.getCategory().equals("0") && x.getHandleFlag().equals("1")){
                   fault1=x.getNum();
               } if(x.getCategory().equals("0") && x.getHandleFlag().equals("0")){
                   fault0=x.getNum();
               } if(x.getCategory().equals("1") && x.getHandleFlag().equals("1")){
                   fire1=x.getNum();
               } if(x.getCategory().equals("1") && x.getHandleFlag().equals("0")){
                   fire0=x.getNum();
               }
           }
           rs.add(new TypeNumVO("faultNum", fault0));
           rs.add(new TypeNumVO("alrmNum",fault0+fire0));
           if(fault1+fault0>0){
               rs.add(new TypeNumVO("faultPercent",new BigDecimal(1.0 * fault1/(fault1+fault0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()));
           }else {
               rs.add(new TypeNumVO("faultPercent",0.0));
           }
           if(fault1+fault0+fire1+fire0>0){
               rs.add(new TypeNumVO("alrmPercent",new BigDecimal(1.0 * (fault1+fire1)/(fault1+fault0+fire1+fire0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()));
           }else {
               rs.add(new TypeNumVO("alrmPercent",0.0));
           }
       }else {
           rs.add(new TypeNumVO("faultNum",0));
           rs.add(new TypeNumVO("alrmNum",0));
           rs.add(new TypeNumVO("faultPercent",0.0));
           rs.add(new TypeNumVO("alrmPercent",0.0));
       }

       return rs;
   }

   public List<AbnormalNumVO> countNumMonth(Integer equId,Integer buildId){
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       DateTime now = DateTime.now();
       Date end = now.plusDays(1).toDate();
       Date start = now.minusDays(30).toDate();
       Map<String,Object> map=new HashMap<>();
       map.put("equId",equId);
       map.put("buildId",buildId);
       map.put("startDate",sdf.format(start));
       map.put("endDate",sdf.format(end));
       List<AbnormalDTO> list = mapper.countNumMonth(map);
       List<AbnormalNumVO> monthDateList = getMonthDateList();
       for (AbnormalNumVO v : monthDateList) {
           if(CollectionUtils.isNotEmpty(list)){
               for (AbnormalDTO dto : list) {
                   if(v.getAlrmDate().equals(dto.getAlrmDate())){
                       if(dto.getCategory().equals("0")){
                           v.setFaultNum(dto.getNum());
                       }else if(dto.getCategory().equals("1")){
                           v.setFireNum(dto.getNum());
                       }
                   }
               }
           }
       }
       return monthDateList;
   }

   private List<AbnormalNumVO> getMonthDateList(){
       DateTime now = DateTime.now();
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       List<AbnormalNumVO> rs=new ArrayList<>();
       for (int i = 0; i < 30; i++) {
           AbnormalNumVO d=new AbnormalNumVO();
           d.setAlrmDate(sdf.format(now.minusDays(i).toDate()));
           rs.add(d);

       }
       return rs;
   }
   public List<Integer> getEquId(){
        return mapper.getEquId();
   }

}