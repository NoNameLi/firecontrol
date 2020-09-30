package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.entity.DeviceFireMainAbnormal;
import cn.turing.firecontrol.datahandler.mapper.DeviceFireMainAbnormalMapper;
import cn.turing.firecontrol.datahandler.util.Constants;
import cn.turing.firecontrol.datahandler.util.DateUtil;
import cn.turing.firecontrol.datahandler.vo.DeviceAbnormalVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceFireMainAbnormalBiz extends BusinessBiz<DeviceFireMainAbnormalMapper, DeviceFireMainAbnormal> {

    @Autowired
    private DeviceFireMainAbnormalMapper deviceFireMainAbnormalMapper;

    public TableResultResponse<DeviceFireMainAbnormal> selectQuery(Query query, String startDate, String endDate, String tag, String bName, String fireMainId, String sensorLoop,
                                                                   String address, String series,String alrmType, String floorId, String positionDescription, String handlePerson, String handleStartDate, String handleEndDate, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        DeviceFireMainAbnormal deviceFireMainAbnormal = new DeviceFireMainAbnormal();
        if(tag.equals("0")){//当前报警记录
            deviceFireMainAbnormal.setAlrmCategory(null);
            deviceFireMainAbnormal.setHandleFlag("0");
        }
        if(tag.equals("1")){//火警报警记录
            deviceFireMainAbnormal.setAlrmCategory("1");
            deviceFireMainAbnormal.setHandleFlag("1");
        }
        if(tag.equals("2")){//故障报警记录
            deviceFireMainAbnormal.setAlrmCategory("0");
            deviceFireMainAbnormal.setHandleFlag("1");
        }
        if(StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)){
            map.put("startDate",null);
            map.put("endDate",null);
        }else {
            map.put("startDate", startDate+" 00:00:00");
            map.put("endDate", endDate+" 23:59:59");
        }
        if(StringUtils.isBlank(handleStartDate) || StringUtils.isBlank(handleEndDate)){
            map.put("handleStartDate",null);
            map.put("handleEndDate",null);
        }else {
            map.put("handleStartDate", handleStartDate+" 00:00:00");
            map.put("handleEndDate", handleEndDate+" 23:59:59");
        }
        if("全部".equals(bName)){
            bName =null;
        }
        deviceFireMainAbnormal.setbName(bName);
        if(StringUtils.isNotBlank(floorId)){
            try {
                deviceFireMainAbnormal.setFloor(Integer.parseInt(floorId));
            }catch (Exception e){
                return new TableResultResponse<DeviceFireMainAbnormal>();
            }
        }
        if(StringUtils.isNotBlank(fireMainId)){
            try {
                deviceFireMainAbnormal.setFireMainId(Integer.parseInt(fireMainId));
            }catch (Exception e){
                return new TableResultResponse<DeviceFireMainAbnormal>();
            }
        }
        deviceFireMainAbnormal.setSensorLoop(sensorLoop);
        deviceFireMainAbnormal.setSeries(series);
        deviceFireMainAbnormal.setAlrmType(alrmType);
        deviceFireMainAbnormal.setAddress(address);
        deviceFireMainAbnormal.setPositionDescription(positionDescription);
        deviceFireMainAbnormal.setHandlePerson(handlePerson);
        deviceFireMainAbnormal.setChannelId(channelId);
        deviceFireMainAbnormal.setTenantId(null);
        map.put("deviceFireMainAbnormal",deviceFireMainAbnormal);
        List<DeviceFireMainAbnormal> list = deviceFireMainAbnormalMapper.selectQuery(map);
        for(DeviceFireMainAbnormal abnormal:list){
            if(abnormal.getHandleFlag().equals("0")){
                abnormal.setHandleFlag("false");
            }
            if(abnormal.getHandleFlag().equals("1")){
                abnormal.setHandleFlag("true");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }
    public List<DeviceAbnormalVo> selectList(Integer channelId, Long deviceId, Integer buildId, Integer floor){
        return mapper.selectList(channelId,deviceId,buildId,floor);
    }

    public List<String> selectAlrmType(Integer channelId, String alrmCategory, String handleFlag,String tenantId) {
        Map<String,Object> map = new HashMap<>();
        map.put("channelId",channelId);
        map.put("alrmCategory",alrmCategory);
        map.put("handleFlag",handleFlag);
        map.put("tenantId",tenantId);
        return deviceFireMainAbnormalMapper.selectAlrmType(map);
    }

    public Integer selectCountByAlrmCategoryAndFireFlag(String fireFlag, String alrmCategory, String handleFlag, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("fireFlag",fireFlag);
        map.put("alrmCategory",alrmCategory);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFireMainAbnormalMapper.selectCountByAlrmCategoryAndFireFlag(map);
    }

    public Integer getCountByHandleFlag(Date startDate, Date endDate, String handleFlag, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFireMainAbnormalMapper.getCountByHandleFlag(map);
    }

    public Integer getCountByToday(Date startDate, Date endDate, String alrmCategory, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFireMainAbnormalMapper.getCountByToday(map);
    }

    public Integer getCount(Date startDate, Date endDate, String alemCategory, String handleFlag, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alemCategory",alemCategory);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFireMainAbnormalMapper.getCount(map);
    }

    public Integer selectCountByType(Date startDate, Date endDate, String alrmType, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alrmType",alrmType);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFireMainAbnormalMapper.selectCountByType(map);
    }

    public Integer selectCountByBuildIdAndDate(Date startDate, Date endDate, String bName, String alrmCategory, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("bName",bName);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFireMainAbnormalMapper.selectCountByBuildIdAndDate(map);
    }

    public List<Map> selectByHandelFlagAndAlrm(String alrmCategory, String handleFlag, Integer channelId,String tenantId) {
        Map map =new HashMap();
        map.put("alrmCategory",alrmCategory);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFireMainAbnormalMapper.selectByHandelFlagAndAlrm(map);
    }

    public TableResultResponse selectAbnormal(Query query, Integer buildId, Integer floor, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("buildId",buildId);
        map.put("floor",floor);
        map.put("channelId",channelId);
        map.put("tenantId",null);
        List<DeviceFireMainAbnormal> list = deviceFireMainAbnormalMapper.selectAbnormal(map);
        return new TableResultResponse(result.getTotal(),list);
    }

    public List<Integer> add(List<DeviceFireMainAbnormal> list) {
        List<Integer> idList = new LinkedList();
        if(list.size()==0){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            for(DeviceFireMainAbnormal deviceFireMainAbnormal:list){
                mapper.insertSelective(deviceFireMainAbnormal);
                idList.add(deviceFireMainAbnormal.getId());
            }
        }
        return idList;
    }

    public List<Map<String, Object>> selectByEquIdResultMP(DeviceFireMainAbnormal deviceFireMainAbnormal) {
        return mapper.selectByEquIdResultMP(deviceFireMainAbnormal);
    }

    public List<Map<String,Object>> getAbnormalByTheLatestTen(String handleFlag, String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("tenantId",tenantId);
        return mapper.getAbnormalByTheLatestTen(map);
    }

    public Integer selectCountByDateAndHandle(Date startDate, Date endDate, String handleFlag, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return mapper.selectCountByDateAndHandle(map);
    }

    public Integer selectCountBySensorId(Long id, String alrmCategory, String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("alrmCategory",alrmCategory);
        map.put("tenantId",tenantId);
        return mapper.selectCountBySensorNo(map);
    }

    public TableResultResponse selectAlrm(Query query, String handleFlag, String alrmCategory, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map map =new HashMap();
        map.put("handleFlag",handleFlag);
        map.put("alrmCategory",alrmCategory);
        map.put("channelId",channelId);
        map.put("tenantId",null);
        List<Map<String,Object>> resultList = new ArrayList();
        List<DeviceFireMainAbnormal> list = mapper.selectAlrm(map);
        for(DeviceFireMainAbnormal deviceFireMainAbnormal:list){
            Map map1 = new HashMap();
            map1.put("sensorId",deviceFireMainAbnormal.getEquId());//传感器id
            map1.put("alrmId",deviceFireMainAbnormal.getId());//异常记录id
            map1.put("buildId",deviceFireMainAbnormal.getBuildId());//建筑id
            map1.put("name",deviceFireMainAbnormal.getbName());
            map1.put("type",deviceFireMainAbnormal.getSeries());
            map1.put("positionDescription",deviceFireMainAbnormal.getFloor()+"F-"+deviceFireMainAbnormal.getPositionDescription());
            map1.put("alrmType1",deviceFireMainAbnormal.getAlrmType());
            map1.put("fireFlag",deviceFireMainAbnormal.getFireFlag());
            map1.put("handleFlag",deviceFireMainAbnormal.getHandleFlag());
            map1.put("alrmCegory",deviceFireMainAbnormal.getAlrmCategory());
            map1.put("date", DateUtil.getFriendlytime(deviceFireMainAbnormal.getAlrmDate()));
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    public List<DeviceFireMainAbnormal> selectByEquId(Long equId, String handleFlag, String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("equId",equId);
        map.put("handleFlag",handleFlag);
        map.put("tenantId",tenantId);
        return mapper.selectByEquId(map);
    }

    public Integer selectAlrmCountByDate(Date startDate,Date endDate, Integer buildId, String alrmcategory, Integer channelId, String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("buildId",buildId);
        map.put("alrmcategory",alrmcategory);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return mapper.selectAlrmCountByDate(map);
    }

    public List<String> getBNameByDate(String startDate, String endDate) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        return mapper.getBNameByDate(map);
    }

    public void handleAbnormal(Integer fireMainId, Long sensorId, String sensorLoop,String address,String series){
        this.mapper.handleAbnormal(fireMainId,sensorId,sensorLoop,address,series);
    }
    public void restoreAbnormal(Long sensorId){
        mapper.restoreAbnormal(sensorId);
    }


    /**
     * 查询最近几个月的个状态异常数（报警，误报，故障）（不包含未确认的报警信息）
     * @param count
     * @return
     */
    public Map<String,Object[]> selectCountNearlyMonth(int count){
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

        List<Map<String,Object>> list = mapper.selectCountNearlyMonth(startMonth);
        Long[] faultCount = new Long[count];
        Arrays.fill(faultCount,0L);
        Long[] alarmCount = new Long[count];
        Arrays.fill(alarmCount,0L);
        Long[] notAlarmCount = new Long[count];
        Arrays.fill(notAlarmCount,0L);
        list.parallelStream().forEach(map -> {
            int index = Arrays.binarySearch(months,map.get("month"));
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

}