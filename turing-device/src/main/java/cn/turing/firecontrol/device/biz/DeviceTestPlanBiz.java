package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.entity.DeviceTestPlan;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import cn.turing.firecontrol.device.mapper.DeviceTestPlanMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
public class DeviceTestPlanBiz extends BusinessBiz<DeviceTestPlanMapper,DeviceTestPlan> {

}