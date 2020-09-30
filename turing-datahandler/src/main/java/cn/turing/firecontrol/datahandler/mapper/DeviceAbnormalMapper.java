package cn.turing.firecontrol.datahandler.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.datahandler.business.BusinessI;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.AbstractAbnormalHandler;
import cn.turing.firecontrol.datahandler.vo.*;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import io.swagger.models.auth.In;
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

    List<AbnormalVo> selectAbnormal(Map<String, Object> map);

    Integer selectCountBySensorNo(Map<String, Object> map);

    List<Map<String,Object>> getAbnormalByTheLatestTen(Map<String, Object> map);

    Integer selectCountByDateAndHandle(Map<String, Object> map);

    List<String> getBNameByDate(Map<String, Object> map);

    List<Map<String,Object>> selectCountByDeviceSeriesAndBuilding(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    List<Map<String,Object>> selectUnhandleCounts(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    List<Map<String,Object>> selectCountBySensor(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("limit")Integer limit);

    List<Map<String,Object>> selectCountByDay(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<DeviceAbnormal> selectByCategory(@Param("startTime") Date startTime, @Param("endTime") Date endTime,@Param("alarmCategory") String alarmCategory);

    List<Map<String,Object>> selectTypeCountByCategory(@Param("startTime") Date startTime, @Param("endTime") Date endTime,@Param("alarmCategory") String alarmCategory);

    /**
     * 恢复异常
     * @param sensorNo
     * @param alarms 如果为空，则是所有测点
     * @return
     */
    int restoreAbnormal(@Param("sensorNo") String sensorNo, @Param("alarms") List<AbstractAbnormalHandler.Alarm> alarms, @Param("time") Date time);


    //查询最近N个月各状态（火警，故障，误报）的数量
    List<Map<String,Object>> selectCountNearlyMonth(@Param("month") String month,@Param("channelId")Integer channelId);

    List<TypeNumVO> selectFireFlagCount(@Param("channelId") Integer channelId);


    //查询最近N个月各状态（火警，故障，误报）的数量
    List<Map<String,Object>> selectCountNearlyDate(@Param("date") String date,@Param("channelId")Integer channelId,@Param("buildId")Integer buildId);
    List<Map<String,Object>> selectAllCountNearlyDate(@Param("date") String date);
    //查询设备的最后一条异常记录
    DeviceAbnormal selectLast(@Param("deviceId") Long deviceId);

    //倒序查询传感器的异常记录
    List<DeviceAbnormalVo> selectList(@Param("channelId") Integer channelId,@Param("deviceId") Long deviceId,@Param("buildId")Integer buildId,@Param("floor")Integer floor);

    //按周别和小时段统计数量
    List<Map<String,Integer>> selectCountByWeekAndHour(@Param("date")Date date);

    //查询近七天报警数量,待处理量
    Integer getCountByCond(@Param("handleFlag") String handleFlag,@Param("channelId") Integer channelId);

    //查询最近30s内新增的异常
    List<DeviceAbnormalVo> getLatestList(Map<String,Object> map);
    //今日报警数，故障数
    Integer getAlrmNumByDate(Map<String,Object> map);

    Integer getHandleNumByDate(Map<String,Object> map);
    List<AbnormalDTO> countNumByCategoryAndHandle(Map<String,Object> map);
    //近一个月报警曲线
    List<AbnormalDTO> countNumMonth(Map<String,Object> map);

    List<Integer> getEquId();

    List<Map> selectTopCountByBuildIdAndDate(Map<String, Object> map);



}
