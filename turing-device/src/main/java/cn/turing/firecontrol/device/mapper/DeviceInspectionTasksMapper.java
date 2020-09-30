package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceInspectionTasks;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

//@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceInspectionTasksMapper extends CommonMapper<DeviceInspectionTasks> {

    List<DeviceInspectionTasks> selectMyTasksList(Map<String,Object> map);

    List<DeviceInspectionTasks> selectTasksList(Map<String,Object> map);

    DeviceInspectionTasks selectByRouteId(Map map);

    void update(Integer id);
    
    //不租户隔离
    List<DeviceInspectionTasks> getListAll();

    DeviceInspectionTasks getById(Integer taskId);

    List<DeviceInspectionTasks> getByRouteId(Map<String,Object> map);

    List<DeviceInspectionTasks> getAllList(Map<String, Object> map);
    Long selectTaskCount();
}