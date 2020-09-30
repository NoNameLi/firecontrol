package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.dto.FireDoorSensorDto;
import cn.turing.firecontrol.device.entity.DeviceSensorFdExt;
import cn.turing.firecontrol.device.vo.FdSensorVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Tenant
@Repository
public interface DeviceSensorFdExtMapper extends CommonMapper<DeviceSensorFdExt> {
    void deleteFireDoorSensorExt(@Param("doorId") Long doorId);
    List<FdSensorVo> listSensor(FireDoorSensorDto dto);
    List<DeviceSensorFdExt> selectSensorExtByDoorId(@Param("doorId") Long doorId);
    DeviceSensorFdExt getById(@Param("id") Long id);
    void updateDoorStatus(@Param("id") Long id,@Param("status") String status);
    List<DeviceSensorFdExt> listSensorExtByDoorId(@Param("doorId") Long doorId);
}