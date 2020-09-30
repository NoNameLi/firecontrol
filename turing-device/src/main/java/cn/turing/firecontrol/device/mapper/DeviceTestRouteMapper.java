package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceTestRoute;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceTestRouteMapper extends CommonMapper<DeviceTestRoute> {
    public List<Map> selectPageList(@Param(value = "routeName") String routeName, @Param(value = "routeFlag") String routeFlag);

    public Integer selectByCount(@Param(value = "routeName") String routeName,@Param(value = "routeFlag") String routeFlag);

    public List<Map> getNotIds(@Param(value = "ids")String ids,@Param(value = "id")Integer id);
}