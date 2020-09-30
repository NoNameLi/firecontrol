package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceAlarmThreshold;
import cn.turing.firecontrol.device.entity.DeviceSensorSeries;
import cn.turing.firecontrol.device.mapper.DeviceAlarmThresholdMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.device.mapper.DeviceMeasuringPointMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Service
public class DeviceMeasuringPointBiz extends BusinessBiz<DeviceMeasuringPointMapper,DeviceMeasuringPoint> {

    @Autowired
    DeviceMeasuringPointMapper dmpMapper;
    @Autowired
    DeviceAlarmThresholdMapper datMapper;

    //根据查询字段计数
    public int selectByCount(String measuringPoint ,String codeName) {
        DeviceMeasuringPoint entity=new DeviceMeasuringPoint();
        entity.setMeasuringPoint(measuringPoint);
        entity.setCodeName(codeName);
       int count =  dmpMapper.selectByCount(entity);
      return count;
    }

    //根据传感器系列的id获取测点对象
    public List<DeviceMeasuringPoint> selectBySensorSeriesIdResult(Integer sensorSeriesId){
        return dmpMapper.selectBySensorSeriesIdResult(sensorSeriesId);
    }

    //根据传感器的id获取测点名称
    public List<String> selectBySensorSeriesIdResultMp(Integer sensorSeriesId){
        return dmpMapper.selectBySensorSeriesIdResultMp(sensorSeriesId);
    }

    //根据传感器的id获取测点id
    public List<Integer> selectBySensorSeriesIdResultId(Integer sensorSeriesId){
        return dmpMapper.selectBySensorSeriesIdResultId(sensorSeriesId);
    }

    public List<Map> getAll(){
        return dmpMapper.getAll();
    }

    public DeviceMeasuringPoint get(Integer id){
        return dmpMapper.get(id);
    }

    //分页查询搜索测点
    public TableResultResponse<Map> selectPageList(Query query, String measuringPoint, String measuringPointType){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceMeasuringPoint entity = new DeviceMeasuringPoint();
        entity.setMeasuringPoint(measuringPoint);
        entity.setMeasuringPointType(measuringPointType);
        List<DeviceMeasuringPoint> lists = dmpMapper.selectPageList(entity);
        List<Map<String,Object>> maps = new ArrayList<>();
        for(int i=0;i<lists.size();i++){
            DeviceMeasuringPoint deviceMeasuringPoint = lists.get(i);
            Map<String,Object> map = new HashMap<>();
            map.put("id",deviceMeasuringPoint.getId());
            map.put("codeName",deviceMeasuringPoint.getCodeName());
            map.put("dataUnit",deviceMeasuringPoint.getDataUnit());
            map.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
            map.put("measuringPointType",deviceMeasuringPoint.getMeasuringPointType());
            map.put("normalValueMax",deviceMeasuringPoint.getNormalValueMax());
            map.put("normalSymbolMax",deviceMeasuringPoint.getNormalSymbolMax());
            List<Map> deviceAlarmThresholds = datMapper.selectByMeasuringPointId( deviceMeasuringPoint.getId());
            map.put("deviceAlarmThresholds",deviceAlarmThresholds);
            maps.add(map);
        }
        return new TableResultResponse(result.getTotal(),maps);
    }


    //查询不在ids里面的测点
    public List<DeviceMeasuringPoint> selectByNotIds(String ids){
        return dmpMapper.selectByNotIds(ids);
    }

    //根据测点的代号查询
    public DeviceMeasuringPoint selectByCodeName(String codeName){
        return dmpMapper.selectByCodeName(codeName);
    }
}