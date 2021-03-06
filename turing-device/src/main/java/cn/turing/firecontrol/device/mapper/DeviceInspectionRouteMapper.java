package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceInspectionRoute;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceInspectionRouteMapper extends CommonMapper<DeviceInspectionRoute> {

    public List<Map> selectPageList(@Param(value = "routeName") String routeName,@Param(value = "routeFlag") String routeFlag);

    public Integer selectByCount(@Param(value = "routeName") String routeName,@Param(value = "routeFlag") String routeFlag);

    public List<Map> getNotIds(@Param(value = "ids")String ids,@Param(value = "id")Integer id);

    DeviceInspectionRoute getById(Integer routeId);
}