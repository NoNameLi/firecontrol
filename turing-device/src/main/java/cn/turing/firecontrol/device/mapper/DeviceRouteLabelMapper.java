package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceRouteLabel;
import cn.turing.firecontrol.device.vo.LabelCountVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceRouteLabelMapper extends CommonMapper<DeviceRouteLabel> {


    public List<Integer> selectByRouteId(@Param(value = "routeId") Integer routeId,@Param(value = "routeflag")String routeflag);

    public void deleteByLabel(@Param(value = "routeId") Integer routeId,@Param(value = "routeflag")String routeflag,@Param(value = "labelIds") String labelIds);
    LabelCountVo getIndoorCount(Map<String, Object> map);

    LabelCountVo getOutdoorCount(Map<String, Object> map);

    public void deleteByLabel(@Param(value = "routeId") Integer routeId,@Param(value = "labelIds") String labelIds);

    DeviceRouteLabel selectByLabelId(Map<String, Object> map);

    List<Integer> getByRouteId(Map<String, Object> map);

    public Integer selectByLabelIdCount(@Param(value = "labelFlag") String labelFlag,@Param(value = "routeFlag") String routeFlag,@Param(value = "labelId") Integer labelId);

    public Integer selectByLabelIdResultRouteId(@Param(value = "labelFlag") String labelFlag,@Param(value = "routeFlag") String routeFlag,@Param(value = "labelId") Integer labelId);

}