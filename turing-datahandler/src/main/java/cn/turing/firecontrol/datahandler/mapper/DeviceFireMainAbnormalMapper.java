package cn.turing.firecontrol.datahandler.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.entity.DeviceFireMainAbnormal;
import cn.turing.firecontrol.datahandler.vo.DeviceAbnormalVo;
import cn.turing.firecontrol.datahandler.vo.TypeNumVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
public interface DeviceFireMainAbnormalMapper extends CommonMapper<DeviceFireMainAbnormal> {

    List<DeviceFireMainAbnormal> selectQuery(Map<String, Object> map);

    List<String> selectAlrmType(Map<String, Object> map);

    Integer selectCountByAlrmCategoryAndFireFlag(Map<String, Object> map);

    Integer getCountByHandleFlag(Map<String, Object> map);

    Integer getCountByToday(Map<String, Object> map);

    Integer getCount(Map<String, Object> map);

    Integer selectCountByType(Map<String, Object> map);

    Integer selectCountByBuildIdAndDate(Map<String, Object> map);

    List<Map> selectByHandelFlagAndAlrm(Map map);

    List<DeviceFireMainAbnormal> selectAbnormal(Map<String, Object> map);

    List<Map<String, Object>> selectByEquIdResultMP(DeviceFireMainAbnormal deviceFireMainAbnormal);

    List<Map<String,Object>> getAbnormalByTheLatestTen(Map<String, Object> map);

    Integer selectCountByDateAndHandle(Map<String, Object> map);

    Integer selectCountBySensorNo(Map<String, Object> map);

    List<DeviceFireMainAbnormal> selectAlrm(Map map);

    List<DeviceFireMainAbnormal> selectByEquId(Map<String, Object> map);

    Integer selectAlrmCountByDate(Map<String, Object> map);

    List<String> getBNameByDate(Map<String, Object> map);

    void handleAbnormal(@Param("fireMainId") Integer fireMainId,
                        @Param("sensorId") Long sensorId,
                        @Param("sensorLoop") String sensorLoop,
                        @Param("address") String address,
                        @Param("series") String series);
    void restoreAbnormal(@Param("sensorId") Long sensorId);

    //查询最近N个月各状态（火警，故障，误报）的数量
    List<Map<String,Object>> selectCountNearlyMonth(@Param("month") String month);

    //倒序查询传感器的异常记录
    List<DeviceAbnormalVo> selectList(@Param("channelId") Integer channelId, @Param("deviceId") Long deviceId, @Param("buildId")Integer buildId, @Param("floor")Integer floor);
    List<Map<String,Object>> selectCountNearlyDate(@Param("date") String date,@Param("channelId")Integer channelId,@Param("buildId")Integer buildId);
    List<TypeNumVO> selectFireFlagCount(@Param("channelId") Integer channelId);

}