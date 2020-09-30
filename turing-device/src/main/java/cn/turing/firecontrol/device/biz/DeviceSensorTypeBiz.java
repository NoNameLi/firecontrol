package cn.turing.firecontrol.device.biz;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceSensorType;
import cn.turing.firecontrol.device.mapper.DeviceSensorTypeMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.*;

import java.util.List;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Service
public class DeviceSensorTypeBiz extends BusinessBiz<DeviceSensorTypeMapper,DeviceSensorType> {

    @Autowired
    private DeviceSensorTypeMapper deviceSensorTypeMapper;

    //根据传感器系列设备的的id查询出有多少站点使用了改传感器，返回子站点的id
    public List<String> selectBySensorSeriesIdResultTenantId(Integer sensorSeriesId){
        return deviceSensorTypeMapper.selectBySensorSeriesIdResultTenantId(sensorSeriesId);
    }

    //根据测点，绑定的传感器系列，查询多少人使用了改传感器，返回子站点的id
    public List<String> selectByMeasuringPointIdResultTenantID(Integer measuringPointId){
        return deviceSensorTypeMapper.selectByMeasuringPointIdResultTenantID(measuringPointId);
    }
    //根据传感器厂商绑定的传感器，查询多少人使用了改厂商的传感器，返回子站点的id
    public List<String> selectBySensorManufacturerIdResultTenantID(Integer manufacturerId){
        return deviceSensorTypeMapper.selectBySensorManufacturerIdResultTenantID(manufacturerId);
    }




    //分页查询，搜索厂商、系列、型号
    public TableResultResponse<Map<String, Object>> selectPageList(Query query, String manufacturer, String equipmentType, String model) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceSensorType entity = new DeviceSensorType();
        entity.setManufacturer(manufacturer);
        entity.setEquipmentType(equipmentType);
        entity.setModel(model);
        List<Map> list = deviceSensorTypeMapper.selectPageList(entity);
        return new TableResultResponse(result.getTotal(),list);
    }

    public List selectByType(String manufacturer,String model,String equipmentType) {
        Map map = new HashMap();
        map.put("manufacturer",manufacturer);
        map.put("model",model);
        map.put("equipmentType",equipmentType);
        return deviceSensorTypeMapper.selectByType(map);
    }

    //获取所有的厂商
    public List<String> getManufacturer(){
        return deviceSensorTypeMapper.getManufacturer();
    }

    //获取所有的系列
    public List<String> getEquipmentType(){
        return deviceSensorTypeMapper.getEquipmentType();
    }

    //根据厂商获取所有的系列
    public List<String> getEquipmentTypeByManufacturer(String manufacturer){
        return mapper.getEquipmentTypeByManufacturer(manufacturer);
    }

    //根据厂商系列获取所有的类型
    public List<String> getModelByManufacturerAndType(String manufacturer,String equipmentType ){
        DeviceSensorType entity =new DeviceSensorType();
        entity.setManufacturer(manufacturer);
        entity.setEquipmentType(equipmentType);
        return mapper.getModelByManufacturerAndType(entity);
    }

    public DeviceSensorType getById(Integer id) {
        return mapper.getById(id);
    }

    //查询全部没有删除的传感器类型
    public List<DeviceSensorType> getAll(){
        return mapper.getAll();
    }

    //已添加的厂商
    public List<Map> getAddManufacturer(){
        return mapper.getAddManufacturer();
    }

    public void updateSensor(DeviceSensorType entity){
        mapper.updateSensor(entity);
    }

    //获取当前栏目所有的厂商
    public List<String> getManufacturerChannelId(Integer channelId){
        return mapper.getManufacturerChannelId(channelId);
    }

    //根据当前栏目厂商获取所有的系列
    public List<String> getEquipmentTypeByManufacturerChannelId(String manufacturer,Integer channelId){
        DeviceSensorType entity = new DeviceSensorType();
        entity.setChannelId(channelId);
        entity.setManufacturer(manufacturer);
        return mapper.getEquipmentTypeByManufacturerChannelId(entity);
    }

    //根据当前栏目厂商系列获取所有的类型
    public List<String> getModelByManufacturerAndTypeChannelId(String manufacturer,String equipmentType,Integer channelId){
        DeviceSensorType entity = new DeviceSensorType();
        entity.setChannelId(channelId);
        entity.setManufacturer(manufacturer);
        entity.setEquipmentType(equipmentType);
        return mapper.getModelByManufacturerAndTypeChannelId(entity);
    }

    //获取已经添加的栏目的id
    public List<Integer> getAddChannelId(){
        return mapper.getAddChannelId();
    }

    //获取当前栏目所有的id
    public List<Integer> selectByChannelId(Integer channelId){
        return mapper.selectByChannelId(channelId);
    }

    //判断所属系统
    public Integer selectResultChannel(String manufacturer,String equipmentType,String model){
        DeviceSensorType entity = new DeviceSensorType();
        entity.setManufacturer(manufacturer);
        entity.setEquipmentType(equipmentType);
        entity.setModel(model);
        return mapper.selectResultChannel(entity);
    }

    /**
     * 查询租户所属设备的所有类型
     * @param tenantId
     * @return
     */
    public List<DeviceSensorType> selectByTenant(String tenantId){
        return mapper.selectByTenant(tenantId);
    }


    /**
     * 根据channelId查询设备类型
     * @return
     */
    public List<Map<String, Object>> selectTreeByChannelId(Integer[] channelId,Integer[] notInChannelId){
        return mapper.selectTreeByChannelId(channelId,notInChannelId);
    }
}