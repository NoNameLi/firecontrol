package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.config.MybatisRedisCache;
import cn.turing.firecontrol.device.entity.DeviceAbnormal;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.vo.ResultVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceAbnormalMapper extends CommonMapper<DeviceAbnormal> {

    List<DeviceAbnormal> selectQuery(Map map);

    List<String> selectAlrmType(Map map);

    Integer selectCountByFlag(String flag);

    List<DeviceAbnormal> selectCountByBuildId(Map<String, Object> map);

    Integer selectCountByType(Map map);

    Integer selectCountByMonth(Map map);

    List<DeviceAbnormal> selectByEquId(Map map);

    Integer getCountByToday(Map<String, Object> map);

    List<DeviceAbnormal> selectAlrm(Map map);

    List<DeviceAbnormal> selectByBuildId(Integer id);

    int selectCountByAlrmCategoryAndBuildId(String alrmCategory, Integer buildId);

    List<DeviceAbnormal> selectByEquIdAndBuildId(Long equId, Integer buildId);

    List<ResultVo> selectByYear(Integer year, String alemCategory);

    List<DeviceAbnormal> selectByBuildingId(Integer buildId);

    List<String> selectAlrmBySensorId(Map map);

    Integer selectAlrmCountByDate(Map<String, Object> map);

    // 获取所有的未处理的记录
//    public List<DeviceAbnormal> selectByHandelFlagAndAlrm(@Param(value = "alrmcategory")String alrmcategory,@Param(value = "handleFlag") String handleFlag,@Param(value = "channelId")Integer channelId);
    public List<Map> selectByHandelFlagAndAlrm(Map map);

    Integer selectCountByAlrmCategoryAndFireFlag(Map<String, Object> map);

    List<ResultVo> selectCountByDate(Map map);


    //实时数据历史纪录
    public List<Map<String,Object>>  selectByEquIdResultMP(DeviceAbnormal deviceAbnormal);

    Integer selectCountByBuildIdAndDate(Map<String, Object> map);

    Integer selectCountByEquId(Map<String, Object> map);

    Integer getCountByHandleFlag(Map<String, Object> map);

    Integer selectCountBySensorNo(Map<String, Object> map);
}
