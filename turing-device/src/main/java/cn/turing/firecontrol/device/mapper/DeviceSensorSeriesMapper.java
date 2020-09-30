package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceSensorSeries;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.CacheNamespace;
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
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceSensorSeriesMapper extends CommonMapper<DeviceSensorSeries> {

    //根据查询字段计数
    public int selectByCount(DeviceSensorSeries entity);

    //分页搜索查询 1.4
    public List<DeviceSensorSeries> selectPageList(DeviceSensorSeries entity);

    //分页搜索查询 1.5
    public List<Map> selectPageList(DeviceSensorType entity);

    //传感器类型的下拉框，选择传感器
    public List<String> selectedType();

    //根据传感器列表的id返回传感器系列的id
    public Integer selectBySensorId(Integer id);

    //根据传感器类型查询传感器系列
    DeviceSensorSeries selectBySensorType(String equipmentType);

    //根据传感器类型id查看传感器系列id
    public DeviceSensorSeries selectBySensorTypeId(Integer id);
}
