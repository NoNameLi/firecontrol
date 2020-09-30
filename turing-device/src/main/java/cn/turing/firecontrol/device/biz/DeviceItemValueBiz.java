package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.device.entity.DeviceItemValue;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.mapper.DeviceItemValueMapper;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

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
public class DeviceItemValueBiz extends BusinessBiz<DeviceItemValueMapper,DeviceItemValue> {

    public List<Map> selectByResultId(Integer resultsId,String inspectionFlag,String itemFlag){
        return mapper.selectByResultId(resultsId,inspectionFlag,itemFlag);
    }
}