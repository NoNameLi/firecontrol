package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceHardwareFacilities;
import cn.turing.firecontrol.device.vo.DeviceBuildingVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import java.util.Map;
import java.util.List;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceHardwareFacilitiesMapper extends CommonMapper<DeviceHardwareFacilities> {

    List<DeviceHardwareFacilities> selectAllZxqy(String facilityType);


    //根据6位编码查询室外消防栓
    public List<Integer> selectByZxqzResultIds(DeviceHardwareFacilities entity);

    List<DeviceHardwareFacilities> selectQuery(Map map);


    //查看硬件设施名称是否重复
    public Integer selectByCount(DeviceHardwareFacilities entity);

    List<DeviceHardwareFacilities> getHardwareFacilities(Map<String, Object> map);

    DeviceHardwareFacilities getById(Integer id);

    List<DeviceHardwareFacilities> getAll(String facilityType);

    List<DeviceHardwareFacilities> selectByHydrantNameLike(Map map);

    //根据硬件设施的名称查询硬件设施,和类型查询
    public List<DeviceHardwareFacilities> selectByNameAndType(DeviceHardwareFacilities eneity);

    //根据硬件设施的名称查询硬件设施,和类型和代号查询
    public List<DeviceHardwareFacilities> selectByNameAndTypeAndCode(DeviceHardwareFacilities eneity);

    List<String> getHydrantName();

    DeviceHardwareFacilities selectByHydrantName(String hydrantName);

    List<DeviceHardwareFacilities> getAllAndDelflag(String facilityType);


    List<DeviceBuildingVo> getAllGis();
}