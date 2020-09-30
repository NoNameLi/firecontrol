package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.biz.DeviceRouteLabelBiz;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.entity.DeviceRouteLabel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("deviceRouteLabel")
@CheckClientToken
@CheckUserToken
public class DeviceRouteLabelController extends BaseController<DeviceRouteLabelBiz,DeviceRouteLabel,Integer> {

}