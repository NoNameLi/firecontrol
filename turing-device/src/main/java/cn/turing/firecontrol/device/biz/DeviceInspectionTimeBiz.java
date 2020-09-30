package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.device.entity.DeviceInspectionTime;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.mapper.DeviceInspectionTimeMapper;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceInspectionTimeBiz extends BusinessBiz<DeviceInspectionTimeMapper,DeviceInspectionTime> {

    public List<DeviceInspectionTime> selectBySchemeId(Integer id,String tenantId) {
        Map map = new HashMap();
        map.put("id",id);
        map.put("tenantId",tenantId);
        return mapper.selectBySchemeId(map);
    }
}