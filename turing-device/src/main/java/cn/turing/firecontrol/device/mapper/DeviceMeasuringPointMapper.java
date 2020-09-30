package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

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
public interface DeviceMeasuringPointMapper extends CommonMapper<DeviceMeasuringPoint> {

    //根据查询字段计数
    public Integer selectByCount(DeviceMeasuringPoint entity);

    //根据传感器的id获取测点对象
    public List<DeviceMeasuringPoint> selectBySensorSeriesIdResult(Integer sensorSeriesId);

    //根据传感器的id获取测点名称
    public List<String> selectBySensorSeriesIdResultMp(Integer sensorSeriesId);

    //根据传感器的id获取测点id
    public List<Integer> selectBySensorSeriesIdResultId(Integer sensorSeriesId);

    public List<Map> getAll();

    public DeviceMeasuringPoint get(Integer id);

    //分页查询搜索测点
    public List<DeviceMeasuringPoint> selectPageList(DeviceMeasuringPoint entity);

    //分页根据，测点测点id or 测点名称搜索
    public List<Map> selectByIdsOrNamePageList(@Param("ids")String ids, @Param("measuringPoint")String measuringPoint);

    //查询不在ids里面的测点
    public List<DeviceMeasuringPoint> selectByNotIds(String ids);

    //根据测点的代号查询
    public DeviceMeasuringPoint selectByCodeName(String codeName);

}
