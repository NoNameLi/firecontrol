package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceTestPlanBiz;
import cn.turing.firecontrol.device.entity.DeviceTestPlan;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("deviceTestPlan")
@CheckClientToken
@CheckUserToken
public class DeviceTestPlanController extends BaseController<DeviceTestPlanBiz,DeviceTestPlan,Integer> {

}