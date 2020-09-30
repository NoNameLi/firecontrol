package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.ognl.ObjectElementsAccessor;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceSensorTypeMapper extends CommonMapper<DeviceSensorType> {

    //根据传感器系列设备的的id查询出有多少站点使用了改传感器，返回子站点的id
    public List<String> selectBySensorSeriesIdResultTenantId(Integer sensorSeriesId);

    //根据测点，绑定的传感器系列，查询多少人使用了改传感器，返回子站点的id
    public List<String> selectByMeasuringPointIdResultTenantID(Integer measuringPointId);

    //根据传感器厂商绑定的传感器，查询多少人使用了改厂商的传感器，返回子站点的id
    public List<String> selectBySensorManufacturerIdResultTenantID(Integer manufacturerId);



    public List<Map> selectPageList(DeviceSensorType entity);

    public List<DeviceSensorType> selectByType(Map map);

    //获取所有的厂商
    public List<String> getManufacturer();

    //获取所有的系列
    public List<String> getEquipmentType();


    //根据厂商获取所有的系列
    public List<String> getEquipmentTypeByManufacturer(String manufacturer);

    //根据厂商系列获取所有的类型
    public List<String> getModelByManufacturerAndType(DeviceSensorType entity );

    DeviceSensorType getById(Integer id);

    //查询全部没有删除的传感器类型
    public List<DeviceSensorType> getAll();

    //已添加的厂商
    public List<Map> getAddManufacturer();
    
    public void updateSensor(DeviceSensorType entity);


    //获取当前栏目所有的厂商
    public List<String> getManufacturerChannelId(Integer channelId);

    //根据当前栏目厂商获取所有的系列
    public List<String> getEquipmentTypeByManufacturerChannelId(DeviceSensorType eintiy);

    //根据当前栏目厂商系列获取所有的类型
    public List<String> getModelByManufacturerAndTypeChannelId(DeviceSensorType entity);

    //获取已经添加的栏目的id
    public List<Integer> getAddChannelId();

    //获取当前栏目所有的id
    public List<Integer> selectByChannelId(Integer channelId);

    //判断所属系统
    public Integer selectResultChannel(DeviceSensorType entity);

    /**
     * 获取租户设备系列
     * @param tenantId
     * @return
     */
    List<DeviceSensorType> selectByTenant(String tenantId);

    /**
     * 根据channelId查询设备类型
     * @param channelId
     * @param notInChannnelId
     * @return
     */
    List<Map<String, Object>> selectTreeByChannelId(@Param("channelId") Integer[] channelId, @Param("notInChannelId")Integer[] notInChannelId);
}
