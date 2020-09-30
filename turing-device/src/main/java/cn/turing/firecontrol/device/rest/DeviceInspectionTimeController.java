package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceInspectionTimeBiz;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.entity.DeviceInspectionTime;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("deviceInspectionTime")
@CheckClientToken
@CheckUserToken
public class DeviceInspectionTimeController extends BaseController<DeviceInspectionTimeBiz,DeviceInspectionTime,Integer> {

}