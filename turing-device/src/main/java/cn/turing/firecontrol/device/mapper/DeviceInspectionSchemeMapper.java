package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceInspectionScheme;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

//@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceInspectionSchemeMapper extends CommonMapper<DeviceInspectionScheme> {

    //根据巡检路线判断绑定的巡检计划-
    public List<DeviceInspectionScheme> selectByInspectionRouteId(Map<String,Object> map);

    public List<Map> selectPageList(@Param(value = "routeName")String routeName,@Param(value = "patrolCycle")Integer patrolCycle,@Param(value = "startTimeFirst")String startTimeFirst,@Param(value = "startTimeLast")String startTimeLast,
                                    @Param(value = "endTimeFirst")String endTimeFirst,@Param(value = "endTimeLast")String endTimeLast,@Param(value = "tenantId")String tenantId);

    public List<Integer> getAllInspectionTouteId(Map<String,Object> map);

    List<DeviceInspectionScheme> getListAll();
}