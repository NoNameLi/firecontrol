package cn.turing.firecontrol.device.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceFloorLayout;
import cn.turing.firecontrol.device.mapper.DeviceFloorLayoutMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:57
 */
@Service
public class DeviceFloorLayoutBiz extends BusinessBiz<DeviceFloorLayoutMapper,DeviceFloorLayout> {

    @Autowired
    private DeviceFloorLayoutMapper deviceFloorLayoutMapper;

    public List<DeviceFloorLayout> selectFloorLayout(Integer buildingId, Integer floor) {
        Map<String ,Object> map = new HashMap<>();
        map.put("buildingId",buildingId);
        map.put("floor",floor);
        return deviceFloorLayoutMapper.selectFloorLayout(map);
    }
}