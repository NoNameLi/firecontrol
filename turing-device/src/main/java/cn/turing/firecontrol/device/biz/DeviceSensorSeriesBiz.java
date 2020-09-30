package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.mapper.DeviceMeasuringPointMapper;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceSensorSeries;
import cn.turing.firecontrol.device.mapper.DeviceSensorSeriesMapper;
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
public class DeviceSensorSeriesBiz extends BusinessBiz<DeviceSensorSeriesMapper,DeviceSensorSeries> {

    @Autowired
    private DeviceSensorSeriesMapper dssMapper;
    @Autowired
    private DeviceMeasuringPointMapper dmpMapper;
    @Autowired
    private IUserFeign iUserFeign;

    //根据查询字段计数
    public int selectByCount(String sensorType) {
        DeviceSensorSeries entity=new DeviceSensorSeries();
        entity.setSensorType(sensorType);
        int count =  dssMapper.selectByCount(entity);
        return count;
    }

    //分页查看传感器系列与测点  1.4
    public TableResultResponse<Map<String,Object>> selectPageList(Query query,String typeName) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceSensorSeries entity = new DeviceSensorSeries();
        entity.setSensorType(typeName);
        List<DeviceSensorSeries> list = dssMapper.selectPageList(entity);
        List<Map<String,Object>> maps =new ArrayList<>();
        for(int i=0;i<list.size();i++){
            List<String> deviceMeasuringPoints = dmpMapper.selectBySensorSeriesIdResultMp( list.get(i).getId());
            Map<String,Object> map = new HashMap<>();
            DeviceSensorSeries temp = list.get(i);
            Map<String,Object> sensorMap = new HashMap<>();
            sensorMap.put("id",temp.getId());
            sensorMap.put("sensorType",temp.getSensorType());
            sensorMap.put("color",temp.getColor());
            if("0".equals(temp.getType())){
                sensorMap.put("type","室内");
            }else {
                sensorMap.put("type","室外");
            }
            map.put("sensorType",sensorMap);
            map.put("measuringPoint",deviceMeasuringPoints);
            maps.add(map);
        }
        return new TableResultResponse<Map<String,Object>>(result.getTotal(),maps);
    }

    //分页查看传感器系列与测点  1.5
    public TableResultResponse<Map<String,Object>> selectPageList(Query query,Integer channelId,String manufacturer,String equipmentType,String model) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceSensorType entity = new DeviceSensorType();
        entity.setChannelId(channelId);
        entity.setManufacturer(manufacturer);
        entity.setEquipmentType(equipmentType);
        entity.setModel(model);
        List<Map> list = dssMapper.selectPageList(entity);
        List<Map<String,Object>> maps =new ArrayList<>();
        for(int i=0;i<list.size();i++){
            List<String> deviceMeasuringPoints = dmpMapper.selectBySensorSeriesIdResultMp( (Integer) list.get(i).get("seriesId"));
            String channelName = "";
            Map temp = list.get(i);
            Integer chid = (Integer)temp.get("channelId");
            if(chid!=null){
                JSONObject a= iUserFeign.selectById(chid);
                JSONObject data = a.getJSONObject("data");
                if(data!=null){
                    channelName = (String) data.get("channelName");
                }
            }
            list.get(i).put("channelName",channelName);
            Map<String,Object> map = new HashMap<>();
            map.put("sensorType",list.get(i));
            map.put("measuringPoint",deviceMeasuringPoints);
            maps.add(map);
        }
        return new TableResultResponse<Map<String,Object>>(result.getTotal(),maps);
    }


    //传感器类型的下拉框，选择传感器
    public List<String> selectedType(){
        return dssMapper.selectedType();
    }

    //根据传感器列表的id返回传感器系列的id
    public Integer selectBySensorId(Integer id){
        return dssMapper.selectBySensorId(id);
    }

    public DeviceSensorSeries selectBySensorType(String equipmentType) {
        return dssMapper.selectBySensorType(equipmentType);
    }

    //根据传感器类型id查看传感器系列id
    public DeviceSensorSeries selectBySensorTypeId(Integer id){
        return mapper.selectBySensorTypeId(id);
    }


}