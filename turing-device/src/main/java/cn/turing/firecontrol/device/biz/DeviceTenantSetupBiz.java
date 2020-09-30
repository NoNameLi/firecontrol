package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.device.entity.DeviceTenantSetup;
import cn.turing.firecontrol.device.mapper.DeviceTenantSetupMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceTenantSetupBiz extends BusinessBiz<DeviceTenantSetupMapper,DeviceTenantSetup>{

    public List<DeviceTenantSetup> getAll(){
        return mapper.getAll();
    }
}
