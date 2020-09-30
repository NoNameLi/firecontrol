package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.vo.DeviceSensorVo;
import cn.turing.firecontrol.device.vo.VideoDeviceVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface DeviceVideoExtMapper extends CommonMapper<DeviceVideoExt> {

    /**
     *  查询所有需要进行某项分析的视频设备
     * @param analysisCode
     * @return
     */
    List<DeviceVideoExt> getAllToAnalysisDevices(@Param("analysisCode")String analysisCode,@Param("tenantId")String tenantId);


    /**
     * 设备组下面的所有设备
     * @param groupId
     * @return
     */
    List<Map<String,Object>> queryByPage(@Param("groupId") Integer groupId, @Param("sensorNo") String sensorNo, @Param("status") String status,
                                         @Param("deviceName")  String deviceName, @Param("manufacturer") String manufacturer,
                                         @Param("equipmentType") String equipmentType, @Param("model") String model,
                                         @Param("isMark") String isMark, @Param("tenantId") String tenantId);


    /**
     * 根据某（几）个属性查询设备
     * @param ext
     * @return
     */
    List<DeviceVideoExt> queryOnlyExt(DeviceVideoExt ext);

    /**
     * 查询需要显示的设备信息
     * @return
     */
    List<Map<String,Object>> queryShowDevice();

    /**
     * 根据是否显示，是否已配置分析方案查询设备，不区分设备组
     * @param showFlag
     * @param hasSolution
     * @return
     */
    List<Map<String,Object>> queryDevice(@Param("showFlag") String showFlag,@Param("hasSolution") Boolean hasSolution,
                                         @Param("groupId")Integer groupId,@Param("tenantId") String tenantId);

    /**
     * 修改设备显示状态
     * @param ids
     * @param status
     */
    void updateDeviceShowStatus(@Param("ids") Long[] ids, @Param("status")String status, @Param("tenantId")String tenantId);

    /**
     * 更新设备的分析方案
     * @param solutionId
     * @param deviceId
     */
    void updateSolution(@Param("solutionId") Integer solutionId, @Param("deviceId")Long deviceId);

    /**
     * 根据ID
     * @param sensorId
     * @return
     */
    DeviceSensorVo queryById(@Param("sensorId") Long sensorId,@Param("tenantId") String tenantId);

    VideoDeviceVo getVideoDeviceDetail(@Param("sensorId") Long sensorId,@Param("tenantId") String tenantId);

    Map countVideoSensor();

    /**
     * 查询同一个真实设备序列号的设备列表
     * @param realSensorNo
     * @return
     */
    List<DeviceVideoExt> querySameRealSensorNos(@Param("realSensorNo") String realSensorNo);

    /**
     * 根据
     * @param entity
     */
    int updateOnlyExtSelectiveBySensorNo(DeviceVideoExt entity);

}