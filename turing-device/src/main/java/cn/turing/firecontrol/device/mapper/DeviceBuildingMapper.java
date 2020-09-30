package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.vo.DeviceBuildingVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
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
@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceBuildingMapper extends CommonMapper<DeviceBuilding> {

    //查询全部没有被删除的记录
    public List<DeviceBuilding> getAll(Integer buildingId);

    List<DeviceBuilding> selectByOid(Integer id);

    DeviceBuilding selectByBname(String buildingName);

    List<DeviceBuilding> selectQuery(Map map);
    //查看联网单位绑定的建筑id
    public List<Integer> selectByOidResultIds(Integer oid);

    //根据联网单位ids查询
    public List<DeviceBuilding> selectByOids(String ids);

    List<DeviceBuilding> selectAllZxqy();

    List<DeviceBuilding> selectByZxqy(String zxqy);

    //根据6位编码查询建筑
    public List<Integer> selectByZxqzResultIds(String zxqz);

    //查询所有建筑名
    List<String> getBname();

    List<DeviceBuilding> selectByBnameLike(Map map);

    //查看建筑名称是否重复
    public Integer selectByCount(String bName);

    DeviceBuilding getById(Integer id);

    //根据地区编码，所属系统查询
    List<DeviceBuilding> selectByArea(Map map);

    List<DeviceBuilding> selectBySensor(Integer channelId);

    List<DeviceBuilding> getAllAndDelflag();

    //获取建筑物数量和总面积
    Map<String,Object> getTotalCountAndArea();
    List<DeviceBuildingVo> getAlarmBuilding(@Param("channelId")Integer channelId);
    DeviceBuildingVo getLatestBuilding();
    List<DeviceBuildingVo> getAllGis();
}
