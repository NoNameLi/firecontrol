package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.mapper.DeviceFacilitiesAbnormalMapper;
import cn.turing.firecontrol.device.mapper.DeviceHardwareFacilitiesMapper;
import cn.turing.firecontrol.device.util.DateUtil;
import cn.turing.firecontrol.device.vo.AlrmVo;
import cn.turing.firecontrol.device.vo.ResultVo;
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

    public TableResultResponse selectAlrmByHydrantId(Query query,Integer hydrantId, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String,Object>> resultList = new ArrayList();
        //List<DeviceSensor> list = dsBiz.selectByHydrantId(hydrantId,channelId);//查询传感器状态在报警的
        Map map = new HashMap();
        map.put("hydrantId",hydrantId);
        map.put("handleFlag","0");
        map.put("channelId",channelId);
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
//            DeviceMeasuringPoint deviceMeasuringPoint = dmpBiz.selectByCodeName(deviceFacilitiesAbnormal.getUnit());//查询测点
//            if(deviceMeasuringPoint!=null){
//                map1.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
//            }else{
//                resultList.add(map1);
//                continue;
//            }
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    public List<ResultVo> selectCountByDate(Date startDate, Date endDate,Integer hydrantId,Integer channelId) {
        Map map = new HashMap();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("hydrantId",hydrantId);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.selectCountByDate(map);
    }

    public Integer selectCountByHydranNameAndDate(Date startDate, Date endDate,String hydranName, Integer channelId) {
        Map map = new HashMap();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("hydranName",hydranName);
        map.put("channelId",channelId);
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
            map1.put("alrmType2",deviceFacilitiesAbnormal.getAlrmData()+deviceFacilitiesAbnormal.getDataUnit());
            map1.put("alrmCegory","0");//给前端做判断
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    //实时数据历史纪录
    public List<Map<String,Object>>  selectByEquIdResultMP(DeviceFacilitiesAbnormal entity){
        return mapper.selectByEquIdResultMP(entity);
    }

    public Integer getCountByHandleFlag(Date startDate, Date endDate,String handleFlag,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.getCountByHandleFlag(map);
    }

    public Integer getCountByToday(Date startDate, Date endDate,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.getCountByToday(map);
    }

    public TableResultResponse<DeviceFacilitiesAbnormal> selectQuery(Query query, DeviceFacilitiesAbnormal deviceFacilitiesAbnormal, String startDate, String endDate, String tag,
                                                                     String hydrantName,String equipmentType,String sensorNo,String measuringPoint,Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        if(tag.equals("0")){//当前报警记录
            deviceFacilitiesAbnormal.setHandleFlag("0");
        }
        if(tag.equals("1")){//故障报警记录
            deviceFacilitiesAbnormal.setHandleFlag("1");
        }
        if(StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)){
            map.put("startDate",null);
            map.put("endDate",null);
        }else {
            map.put("startDate", startDate+" 00:00:00");
            map.put("endDate", endDate+" 23:59:59");
        }
        deviceFacilitiesAbnormal.setHydrantName(hydrantName);
        deviceFacilitiesAbnormal.setEquipmentType(equipmentType);
        deviceFacilitiesAbnormal.setSensorNo(sensorNo);
        deviceFacilitiesAbnormal.setMeasuringPoint(measuringPoint);
        deviceFacilitiesAbnormal.setChannelId(channelId);
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

    public List<String> selectAlrmType(String handleFlag, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.selectAlrmType(map);
    }

    public List<String> selectMeasuringPoint(String handleFlag, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.selectMeasuringPoint(map);
    }

    public Integer selectCountByType(Date startDate,Date endDate,String alrmType) {
        Map<String ,Object> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("alrmType",alrmType);
        return deviceFacilitiesAbnormalMapper.selectCountByType(map);
    }

    public List<Map> selectByHandelFlagAndAlrm(String handleFlag, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.selectByHandelFlagAndAlrm(map);
    }

    public List<Map> selectByHydrantId(Integer id, Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.selectByHydrantId(map);
    }

    public List<AlrmVo> selectAllAlrm(String handleFlag) {
        return deviceFacilitiesAbnormalMapper.selectAllAlrm(handleFlag);
    }

    public Integer selectCountByChannelId(String handleFlag,Integer channelId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("handleFlag",handleFlag);
        map.put("channelId",channelId);
        return deviceFacilitiesAbnormalMapper.selectCountByChannelId(map);
    }
}