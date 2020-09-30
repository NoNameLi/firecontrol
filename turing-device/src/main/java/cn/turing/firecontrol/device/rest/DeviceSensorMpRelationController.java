package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceSensorMpRelationBiz;
import cn.turing.firecontrol.device.entity.DeviceSensorMpRelation;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

@RestController
@RequestMapping("deviceSensorMpRelation")
@CheckClientToken
@CheckUserToken
public class DeviceSensorMpRelationController extends BaseController<DeviceSensorMpRelationBiz,DeviceSensorMpRelation,Integer> {

}