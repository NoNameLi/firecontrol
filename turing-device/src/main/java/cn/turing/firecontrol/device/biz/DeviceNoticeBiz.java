package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.device.entity.DeviceSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

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
public class DeviceNoticeBiz extends BusinessBiz<DeviceNoticeMapper,DeviceNotice> {

    @Autowired
    private DeviceNoticeMapper dnMapper;

    //查看报警等级绑定的通知方式
    public List<DeviceNotice> selectByAlarmLevelIdResult(String ids){
        return dnMapper.selectByAlarmLevelIdResult(ids);
    }

    //查看报警，或者故障的通知方式
    public List<DeviceNotice> selectByNoticeTypeResult(String ids){
        return dnMapper.selectByNoticeTypeResult(ids);
    }

    //查询所有的通知方式
    public List<Map> getAll(){
        return dnMapper.getAll();
    }

    //批量获取通知方式
    public List<String> getByIds( String ids){
        return dnMapper.getByIds(ids);
    }
}