package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceFireMainSensor;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.vo.CountVo;
import cn.turing.firecontrol.device.vo.FireMainSensorVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceFireMainSensorMapper extends CommonMapper<DeviceFireMainSensor> {

    List<Map> selectQuery(Map map);

    //根据消防主机id查看绑定的传感器的个数
    public Integer selectByFireMainIdCount(Integer fireMainId);

    //根据消防主机id查看绑定的传感器信息
    public List<DeviceFireMainSensor> selectByFireMainId(Integer fireMainId);

    //根据建筑id查询绑定的传感器信息
    public List<DeviceFireMainSensor> selectByBuildingIdQuery(Integer buildingId);

    //根据ip+端口+回路+地址查询传感器的信息
    public List<DeviceFireMainSensor> selectIgnoreTenantIpAndPortAndSensor(@Param(value = "serverIp") String serverIp, @Param(value = "port") String port, @Param(value = "sensorLoop") String sensorLoop, @Param(value = "address") String address);

    //修改传感器的状态
    public void updateSensorStatus(@Param(value = "id")Long id,@Param(value = "status")String status);

    List<DeviceFireMainSensor> selectByBuildingId(Map<String, Object> map);

    Integer selectStatusCount(Map<String, Object> map);

    Integer selectByChannelIdAndStatusAndBuilding(Map<String, Object> map);

    List<DeviceFireMainSensor> getSensorStatusByBuildAndFloor(Map<String, Object> map);

    Integer selectCountByType(Map<String, Object> map);

    //当主机一样时回路+地址判重
    public Integer selectByCount(DeviceFireMainSensor deviceFireMainSensor);

    List<Map<String, String>> getNotEnabledSensorList(Map<String, Object> map);

    List<Map> selectByFloorGetSensor(DeviceFireMainSensor deviceFireMainSensor);

    // 假删除 根据消防主机的id  批量假删除
    public void deleteByFireMain(Integer fireMainId);

    CountVo getCountByStatus(Map<String, Object> map);

    Integer getStatusCount(Map<String, Object> map);

    List<DeviceFireMainSensor> selectByBuildingIdAndStatus(Map map1);

    Integer selectNotsignCount(Map<String, Object> map);

    List<String> getSeriesByBuildAndFloor(Map<String, Object> map);

    List<FireMainSensorVo> listFireMainSensorByIds(@Param("ids") String ids,
                                                   @Param("serverIp") String serverIp,
                                                   @Param("port") String port,
                                                   @Param("sensorLoop") String sensorLoop,
                                                   @Param("address") String address,
                                                   @Param("buildingId") Integer buildingId,
                                                   @Param("exIds") String exIds,
                                                   @Param("series") String series,
                                                   @Param("code") String code);
}