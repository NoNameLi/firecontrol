package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceDataDictionaryBiz;
import cn.turing.firecontrol.device.entity.DeviceDataDictionary;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

@RestController
@RequestMapping("deviceDataDictionary")
@CheckClientToken
@CheckUserToken
public class DeviceDataDictionaryController extends BaseController<DeviceDataDictionaryBiz,DeviceDataDictionary,Integer> {

}