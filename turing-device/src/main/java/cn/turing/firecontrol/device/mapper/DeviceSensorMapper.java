package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.dto.SensorTypeStatus;
import cn.turing.firecontrol.device.dto.TypeNumDto;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.vo.AbnormalVo;
import cn.turing.firecontrol.device.vo.CountVo;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceSensorMapper extends CommonMapper<DeviceSensor> {

    List<DeviceSensor> selectByBuildingId(Integer id,Integer channelId);

    List<DeviceSensor> selectBySensorTypeId(Integer id);

    List<Map> selectQuery(Map map);

    Integer selectCountByType(Map map);

    Integer selectStatusCount(Map map);

    List<AbnormalVo> selectAbnormal(Map map);

    DeviceSensor selectBySensorNo(String sensorNo);

    //获取所有的代号，存在Set里面
    public Set<String> getAllIgnoreTenantSensorNo();

    //查看代号是否重复
    public Integer selectByCount(String sensorNo);

    //获取所有的厂商
    public List<String> getManufacturer();

    //获取所有的类型
    public List<String> getEquipmentType();
    //根据建筑id，楼层查询
    List<DeviceSensor> getSensorStatusByBuildAndFloor(Map map);
    //根据所属系统id查询
    List<DeviceSensor> selectByChannelId(Integer channelId);

    List<DeviceSensor> selectByBuildingIdAndStatus(Map map);

    //根据传感器的状态分组统计传感器的数
    public List<DeviceSensor> selectByStatusGroup(DeviceSensor deviceSensor );

    //设备列表，分页查询
    public List<Map> selectByFloorGetSensor(DeviceSensor deviceSensor);

    //根据传感器的栏目id，跟状态查询对应的个数
    public Integer selectByChannelIdAndStatusAndBuilding(DeviceSensor deviceSensor);

    //批量假删除
    public void updateBatch(String id);

    Integer selectNotsignCount(Map<String, Object> map);

    Integer selectSignCount(Map<String, Object> map);

    Integer selectFaultCount(Map<String, Object> map);

    List<DeviceSensor> selectByHydrantId(Map<String, Object> map);

    //删除硬件设施  根据硬件设施的id查询
    public Integer deleteQueryByHydrantId(Integer hydrantId);

    //室外传感器列表
    List<Map> selectOutdoorQuery(Map map);

    List<DeviceSensor> getByHydrantId(Map<String, Object> map);

    Integer selectByChannelIdAndStatusAndHydrant(Map<String, Object> map);

    List<DeviceSensor> selectHardwareFacilitiesByChannelId(Integer channelId);

    List<String> getOutdoorEquipmentType();

    List<String> getOutdoorManufacturer();

    DeviceSensor getById(Long id);

    Integer selectHardwareFacilitiesStatusCount(Map<String, Object> map);

    List<Map<String,String>> getNotEnabledSensorList(Map<String, Object> map);

    List<Map<String, String>> getUnlabeledSensorList(Map<String, Object> map);

    //传感器系列删除前查询1.5
    public List<String> deleteSensorTypeQuery(Integer id);

    //测点删除前查询1.5
    public List<String> deleteMPQuery(Integer id);


    public List<String> getManufacturerChannelId(Integer channelId);

    public List<String> getEquipmentTypeChannelId(Integer channelId);

    //根据id判断传感器是否已经被删除
    public Integer queryIdIsDel(Long id);

    List<Map> selectByHydrantIdGetSensor(DeviceSensor deviceSensor);

    List<Map<String, String>> getNotEnabledSensorListByHydrantId(Map<String, Object> map);

    CountVo getCountByStatus(Map<String, Object> map);

    Integer getStatusCount(Map<String, Object> map);

    //批量查询指定ID的传感器信息
    List<Map<String,Object>> queryByIds(@Param("ids") Long[] ids, @Param("manufacturer") String manufacturer, @Param("equipmentType")String equipmentType, @Param("model")String model, @Param("sensorNo")String sensorNo);

    //保存传感器并返回主键值
    void saveSensorWithKeyReturn(DeviceSensor deviceSensor);

    List<Map<String,Object>> queryByIds(@Param("ids") Long[] ids, @Param("manufacturer") String manufacturer,
                                        @Param("equipmentType")String equipmentType, @Param("model")String model,
                                        @Param("sensorNo")String sensorNo,@Param("buildingId") Long buildingId,
                                        @Param("excludeIds") Long[] excludeIds,
                                        @Param("channelId") Integer channelId,
                                        @Param("code") String code);

    List<Map<String,Object>> queryOutdoorSensorByIds(@Param("ids") Long[] ids, @Param("manufacturer") String manufacturer,
                                                     @Param("equipmentType")String equipmentType, @Param("model")String model,
                                                     @Param("sensorNo")String sensorNo,
                                                     @Param("excludeIds") Long[] excludeIds,
                                                     @Param("channelId") Integer channelId,
                                                     @Param("hids") List<Integer> hids);

    List<Map<String,Object>> queryNestedSensorByIds(@Param("ids") Long[] ids, @Param("manufacturer") String manufacturer,
                                                     @Param("equipmentType")String equipmentType, @Param("model")String model,
                                                     @Param("sensorNo")String sensorNo,
                                                     @Param("excludeIds") Long[] excludeIds,
                                                     @Param("channelId") Integer channelId,
                                                     @Param("groupId") Long groupId);

    /**
     * 获取建筑物下面的第一个设备
     * @param buildingIds
     * @return
     */
    List<DeviceSensor> queryByBuildings(@Param("buildingIds") List<Integer> buildingIds);


    /**
     * 更新设备离线状态
     * @param sensorNos 设备编号
     * @param isOffline true: 将设备状态修改为离线；false: 将离线状态的设备修改为正常状态
     * @return
     */
    int updateOfflineStatus(@Param("sensorNos") List<String> sensorNos,@Param("isOffline") Boolean isOffline, @Param("time") Date time);


    List<Map<String,Object>> selectAllStatus(@Param("channelId")Integer channelId);


    //查询设备的测点
    List<DeviceMeasuringPoint> selectMeasuringPointById(@Param("id")Long id);


    List<SensorTypeStatus> getSensorTypeStatus(@Param("channelId")Integer channelId);
    List<SensorTypeStatus> getSensorStatusNum();
    List<SensorTypeStatus>getChannelSensorStatusNum();
    List<String> getAllStatusById(Map<String,Object> map);



}
