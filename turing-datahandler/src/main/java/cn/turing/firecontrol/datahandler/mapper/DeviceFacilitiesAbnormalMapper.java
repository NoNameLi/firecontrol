package cn.turing.firecontrol.datahandler.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.datahandler.business.BusinessI;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.AbstractAbnormalHandler;
import cn.turing.firecontrol.datahandler.vo.AlrmVo;
import cn.turing.firecontrol.datahandler.vo.DeviceAbnormalVo;
import cn.turing.firecontrol.datahandler.vo.ResultVo;
import cn.turing.firecontrol.datahandler.entity.DeviceFacilitiesAbnormal;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Tenant
@Repository
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

    Integer selectCountByEquId(Integer id);

    List<DeviceFacilitiesAbnormal> selectAbnormal(Map<String, Object> map);

    Integer selectCountBySensorNo(Map<String ,Object> map);

    Integer selectCountByDateAndHandle(Map<String, Object> map);

    List<Map<String,Object>> getAbnormalByTheLatestTen(Map<String, Object> map);

    List<DeviceFacilitiesAbnormal> selectByEquId(Map<String, Object> map);

    List<String> getHardwareFacilitiesByDate(Map<String, Object> map);

    /**
     * 恢复异常
     * @param sensorNo
     * @param alarms 如果为空，则是所有测点
     * @return
     */
    int restoreAbnormal(@Param("sensorNo") String sensorNo, @Param("alarms") List<AbstractAbnormalHandler.Alarm> alarms, @Param("time") Date time);

    //倒序查询传感器的异常记录
    List<DeviceAbnormalVo> selectList(@Param("channelId") Integer channelId, @Param("deviceId") Long deviceId);
}