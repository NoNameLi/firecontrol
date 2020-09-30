package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceFacilitiesAbnormal;
import cn.turing.firecontrol.device.vo.AlrmVo;
import cn.turing.firecontrol.device.vo.ResultVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceFacilitiesAbnormalMapper extends CommonMapper<DeviceFacilitiesAbnormal> {

    List<DeviceFacilitiesAbnormal> selectAlrmByHydrantId(Map map);

    List<ResultVo> selectCountByDate(Map map);

    Integer selectCountByHydranNameAndDate(Map map);

    List<ResultVo> selectByYear(Map map);

    List<DeviceFacilitiesAbnormal> selectAlrm(Map map);

    Integer getCountByHandleFlag(Map<String, Object> map);

    Integer getCountByToday(Map<String, Object> map);

    List<DeviceFacilitiesAbnormal> selectQuery(Map<String, Object> map);

    //实时数据历史纪录
    public List<Map<String,Object>>  selectByEquIdResultMP(DeviceFacilitiesAbnormal deviceAbnormal);

    List<String> selectAlrmType(Map<String, Object> map);

    List<String> selectMeasuringPoint(Map<String, Object> map);

    Integer selectCountByType(Map<String, Object> map);

    List<Map> selectByHandelFlagAndAlrm(Map<String, Object> map);

    List<Map> selectByHydrantId(Map<String, Object> map);

    List<AlrmVo> selectAllAlrm(String handleFlag);

    Integer selectCountByChannelId(Map<String, Object> map);
}