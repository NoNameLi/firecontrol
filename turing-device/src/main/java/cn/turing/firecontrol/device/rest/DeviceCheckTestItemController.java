package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceCheckTestItemBiz;
import cn.turing.firecontrol.device.biz.DeviceFacilitiesTypeBiz;
import cn.turing.firecontrol.device.entity.DeviceCheckTestItem;
import cn.turing.firecontrol.device.entity.DeviceFacilitiesType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("deviceCheckTestItem")
@CheckClientToken
@CheckUserToken
public class DeviceCheckTestItemController extends BaseController<DeviceCheckTestItemBiz,DeviceCheckTestItem,Integer> {



}