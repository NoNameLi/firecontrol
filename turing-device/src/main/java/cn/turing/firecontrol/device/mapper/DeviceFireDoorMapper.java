package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceFireDoor;
import cn.turing.firecontrol.device.vo.FireDoorVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Tenant
@Repository
public interface DeviceFireDoorMapper extends CommonMapper<DeviceFireDoor> {
    List<FireDoorVo> listFireDoor(DeviceFireDoor door);
    String getFireDoorNormalStatus(@Param("sensorNo") String sensorNo);
    void increaseSensorNum(@Param("id") Long id);
    void decreaseSensorNum(@Param("id") Long id);
    void updateDoorStatus(@Param("id") Long id,@Param("status") String status);
}