package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.business.BusinessI;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.AbstractAbnormalHandler;
import cn.turing.firecontrol.datahandler.mapper.DeviceFacilitiesAbnormalMapper;
import cn.turing.firecontrol.datahandler.util.Constants;
import cn.turing.firecontrol.datahandler.util.DateUtil;
import cn.turing.firecontrol.datahandler.vo.AlrmVo;
import cn.turing.firecontrol.datahandler.vo.DeviceAbnormalVo;
import cn.turing.firecontrol.datahandler.vo.ResultVo;
import cn.turing.firecontrol.datahandler.entity.DeviceFacilitiesAbnormal;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceFacilitiesAbnormalBiz extends BusinessBiz<DeviceFacilitiesAbnormalMapper,DeviceFacilitiesAbnormal> {

    @Autowired
    private DeviceFacilitiesAbnormalMapper deviceFacilitiesAbnormalMapper;

    public TableResultResponse selectAlrmByHydrantId(Query query, Integer hydrantId, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String,Object>> resultList = new ArrayList();
        Map map = new HashMap();
        map.put("hydrantId",hydrantId);
        map.put("handleFlag","0");
        map.put("channelId",channelId);
        map.put("tenantId",null);
        List<DeviceFacilitiesAbnormal> list = deviceFacilitiesAbnormalMapper.selectAlrmByHydrantId(map);//报警记录
        if(list.size()==0){
            return new TableResultResponse(result.getTotal(),resultList);
        }
        for(DeviceFacilitiesAbnormal deviceFacilitiesAbnormal:list){
            Map map1 = new HashMap();
            map1.put("alrmType",deviceFacilitiesAbnormal.getAlrmType());
            map1.put("alrmDate",deviceFacilitiesAbnormal.getAlrmDate());
            map1.put("count",result.getTotal());//待处理故障数量
            map1.put("measuringPoint",deviceFacilitiesAbnormal.getMeasuringPoint());//测点
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    public List<ResultVo> selectCountByDate(Date startDate, Date endDate, Integer hydrantId, Integer channelId,String tenantId) {
        Map map = new HashMap();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("hydrantId",hydrantId);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectCountByDate(map);
    }

    public Integer selectCountByHydranNameAndDate(Date startDate, Date endDate,String hydranName,String handleFlag,Integer  channelId,String tenantId) {
        Map map = new HashMap();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("hydranName",hydranName);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectCountByHydranNameAndDate(map);
    }

    public List<ResultVo> selectByYear(Integer year, Integer channelId) {
        Map map = new HashMap();
        map.put("year",year);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.selectByYear(map);
    }

    public TableResultResponse selectAlrm(Query query, String handleFlag, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map map =new HashMap();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",null);
        List<Map<String,Object>> resultList = new ArrayList();
        List<DeviceFacilitiesAbnormal> list = deviceFacilitiesAbnormalMapper.selectAlrm(map);
        for(DeviceFacilitiesAbnormal deviceFacilitiesAbnormal:list){
            Map map1 = new HashMap();
            //map1.put("sensorId",deviceFacilitiesAbnormal.getEquId());//传感器id
            map1.put("alrmId",deviceFacilitiesAbnormal.getId());//异常记录id
            map1.put("buildId",deviceFacilitiesAbnormal.getFireCockId());//消火栓id
            map1.put("name",deviceFacilitiesAbnormal.getHydrantName());
            map1.put("type",deviceFacilitiesAbnormal.getEquipmentType());
            map1.put("positionDescription",deviceFacilitiesAbnormal.getPositionDescription());
            map1.put("alrmType1",deviceFacilitiesAbnormal.getAlrmType());
            map1.put("handleFlag",deviceFacilitiesAbnormal.getHandleFlag());
            map1.put("date", DateUtil.getFriendlytime(deviceFacilitiesAbnormal.getAlrmDate()));
            map1.put("measuringPoint",deviceFacilitiesAbnormal.getMeasuringPoint());
            if(deviceFacilitiesAbnormal.getDataUnit()!=null){
                map1.put("alrmType2",deviceFacilitiesAbnormal.getAlrmData()+deviceFacilitiesAbnormal.getDataUnit());
            }else {
                map1.put("alrmType2",deviceFacilitiesAbnormal.getAlrmData());
            }
            map1.put("alrmCegory","0");//给前端做判断
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    //实时数据历史纪录
    public List<Map<String,Object>>  selectByEquIdResultMP(DeviceFacilitiesAbnormal entity){
        return mapper.selectByEquIdResultMP(entity);
    }

    public Integer getCountByHandleFlag(Date startDate, Date endDate,String handleFlag,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.getCountByHandleFlag(map);
    }

    public Integer getCountByToday(Date startDate, Date endDate,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.getCountByToday(map);
    }

    public TableResultResponse<DeviceFacilitiesAbnormal> selectQuery(Query query, DeviceFacilitiesAbnormal deviceFacilitiesAbnormal, String startDate, String endDate, String tag,
                                                                     String hydrantName, String equipmentType, String sensorNo, String measuringPoint, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        if(tag.equals("0")){//当前报警记录
            deviceFacilitiesAbnormal.setHandleFlag("0");
        }
        if(tag.equals("2")){//故障报警记录
            deviceFacilitiesAbnormal.setHandleFlag("1");
        }
        if(StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)){
            map.put("startDate",null);
            map.put("endDate",null);
        }else {
            map.put("startDate", startDate+" 00:00:00");
            map.put("endDate", endDate+" 23:59:59");
        }
        if("全部".equals(hydrantName)){
            hydrantName =null;
        }
        if("全部".equals(equipmentType)){
            equipmentType =null;
        }
        if("全部".equals(measuringPoint)){
            measuringPoint =null;
        }
        deviceFacilitiesAbnormal.setHydrantName(hydrantName);
        deviceFacilitiesAbnormal.setEquipmentType(equipmentType);
        deviceFacilitiesAbnormal.setSensorNo(sensorNo);
        deviceFacilitiesAbnormal.setMeasuringPoint(measuringPoint);
        deviceFacilitiesAbnormal.setChannelId(channelId);
        deviceFacilitiesAbnormal.setTenantId(null);
        map.put("deviceFacilitiesAbnormal",deviceFacilitiesAbnormal);
        List<DeviceFacilitiesAbnormal> list = deviceFacilitiesAbnormalMapper.selectQuery(map);
        for(DeviceFacilitiesAbnormal abnormal:list){
            if(abnormal.getHandleFlag().equals("0")){
                abnormal.setHandleFlag("false");
            }
            if(abnormal.getHandleFlag().equals("1")){
                abnormal.setHandleFlag("true");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public List<String> selectAlrmType(String handleFlag, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectAlrmType(map);
    }
    public List<DeviceAbnormalVo> selectList(Integer channelId, Long deviceId){
        return mapper.selectList(channelId,deviceId);
    }
    public List<String> selectMeasuringPoint(String handleFlag, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectMeasuringPoint(map);
    }

    public Integer selectCountByType(Date startDate,Date endDate,String alrmType,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alrmType",alrmType);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectCountByType(map);
    }

    public List<Map> selectByHandelFlagAndAlrm(String handleFlag, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectByHandelFlagAndAlrm(map);
    }

    public List<Map> selectByHydrantId(Integer id, Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectByHydrantId(map);
    }

    public List<AlrmVo> selectAllAlrm(String handleFlag) {
        return deviceFacilitiesAbnormalMapper.selectAllAlrm(handleFlag);
    }

    public Integer selectCountByChannelId(String handleFlag,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectCountByChannelId(map);
    }

    public List<Integer> add(List<DeviceFacilitiesAbnormal> list){
        List<Integer> idList = new LinkedList();
        if(list.size()==0){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            for(DeviceFacilitiesAbnormal deviceFacilitiesAbnormal:list){
                mapper.insertSelective(deviceFacilitiesAbnormal);
                idList.add(deviceFacilitiesAbnormal.getId());
            }
        }
        return idList;
    }

    public Integer selectCountByEquId(Integer id) {
        return deviceFacilitiesAbnormalMapper.selectCountByEquId(id);
    }

    public List<DeviceFacilitiesAbnormal> selectAbnormal(String handleFlag, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.selectAbnormal(map);
    }

    public Integer selectCountBySensorNo(String sensorNo,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("sensorNo",sensorNo);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectCountBySensorNo(map);
    }

    public Integer selectCountByDateAndHandle(Date startDate,Date endDate,String handleFlag,Integer channelId,String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectCountByDateAndHandle(map);
    }

    public List<Map<String,Object>> getAbnormalByTheLatestTen(String handleFlag, String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.getAbnormalByTheLatestTen(map);
    }

    public List<DeviceFacilitiesAbnormal> selectByEquId(Long equId, String handleFlag, String codeName, String tenantId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("equId",equId);
        map.put("handleFlag",handleFlag);
        map.put("codeName",codeName);
        map.put("tenantId",tenantId);
        return deviceFacilitiesAbnormalMapper.selectByEquId(map);
    }

    public List<String> getHardwareFacilitiesByDate(String startDate, String endDate) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        return deviceFacilitiesAbnormalMapper.getHardwareFacilitiesByDate(map);
    }

    /**
     * 恢复测点的异常：1、设置恢复时间，2、修改处理状态为已处理
     * @param sensorNo 传感器编号
     * @param alarms 异常
     */
    public void restore(String sensorNo, List<AbstractAbnormalHandler.Alarm> alarms){
        mapper.restoreAbnormal(sensorNo,alarms,new Date());
    }
}