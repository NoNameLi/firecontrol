package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.device.entity.DeviceCheckTestItem;
import cn.turing.firecontrol.device.entity.DeviceFacilitiesType;
import cn.turing.firecontrol.device.mapper.DeviceCheckTestItemMapper;
import cn.turing.firecontrol.device.mapper.DeviceFacilitiesTypeMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceCheckTestItemBiz extends BusinessBiz<DeviceCheckTestItemMapper,DeviceCheckTestItem> {

    public List<DeviceCheckTestItem> selectByQuery( Integer facilitiesTypeId, String itemFlag,String flag){
        return mapper.selectByQuery(facilitiesTypeId,itemFlag,flag);
    }

    public List<DeviceCheckTestItem> selectByFacilitiesNo(Integer facilitiesTypeId, String itemFlag, String flag) {
        return mapper.selectByFacilitiesNo(facilitiesTypeId,itemFlag,flag);
    }
}