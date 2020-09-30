package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceItemValueBiz;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.entity.DeviceItemValue;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("deviceItemValue")
@CheckClientToken
@CheckUserToken
public class DeviceItemValueController extends BaseController<DeviceItemValueBiz,DeviceItemValue,Integer> {

}