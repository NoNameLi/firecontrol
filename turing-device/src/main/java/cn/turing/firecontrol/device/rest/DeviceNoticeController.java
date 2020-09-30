package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.HuaweiVmsUtil;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceNotice")
@CheckClientToken
@CheckUserToken
public class DeviceNoticeController extends BaseController<DeviceNoticeBiz,DeviceNotice,Integer> {

    @Autowired
    private DeviceNoticeBiz dnBiz;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HuaweiVmsUtil huaweiVmsUtil;

    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取通知方式")
    public List<Map> getAll(){
        return dnBiz.getAll();
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取通知方式")
    public String test(){
        String[] phones = new String[]{"18186141485","13260501658"};
        HuaweiVmsUtil.AlarmParam alarmParam = huaweiVmsUtil.new AlarmParam("未来之光7栋鑫豪斯三","鑫豪斯三相和鑫豪斯好的","报警");
//        huaweiVmsUtil.sendAlarm(phones,alarmParam);
        return "";
    }






}