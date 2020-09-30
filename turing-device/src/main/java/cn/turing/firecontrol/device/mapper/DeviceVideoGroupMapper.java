package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceVideoGroup;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
@Tenant
public interface DeviceVideoGroupMapper extends CommonMapper<DeviceVideoGroup> {

    //通过设备组名称查询
    List<DeviceVideoGroup> selectByGroupName(String groupName);

    //查询所有设备组及其下面的设备，组成树形数据
    List<Map<String,Object>> queryDeviceTree(@Param("deviceNoOrName") String deviceNoOrName,@Param("groupId") String groupId,@Param("hasSolution") Boolean hasSolution,
                                           @Param("sensorNo") String sensorNo,@Param("deviceName") String deviceName);

    //查询设备ID对应的设备组
    List<Map<String,Object>> queryBySensorNos(@Param("sensorNos") List<String> sensorNos);

    //删除设备组关联的传感器的位置标记
    Integer removePositionSignByGroup(@Param("groupId") Integer groupId);

}