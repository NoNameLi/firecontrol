package cn.turing.firecontrol.server.controller;


import cn.turing.firecontrol.server.handler.device.DeviceEventHandlerComposite;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 以防万一，kafka ，或者rabbitmq服务挂掉，暴露此接口
 */
@Controller
@RequestMapping("/dataanalysis")
@Slf4j
public class ReceiveDeviceController {

    @Autowired
    DeviceEventHandlerComposite deviceEventHandlerComposite;

    @Autowired
    ExecutorService executorService;

    @ResponseBody
    @RequestMapping("/device")
    public String receiveMessageByDevice(@RequestBody String data) {
        //{"deviceType":"TZJ_SS","iccid":"111111","data":"5050090201600559011C0348E211","id":"004a770124042749","time":"20200523142439","version":"1.0.0"}
        log.info("接收推送的 message:{}", data);
        executorService.execute(() -> {
            DeviceSensorMessage deviceSensorMessage = this.dataAnalusis(data);
            deviceEventHandlerComposite.prcess(deviceSensorMessage);

        });

        return "200";
    }

    private DeviceSensorMessage dataAnalusis(String data) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotBlank(data)) {
//            String[] arrSplit = data.split("[&]");
//            for (String strSplit : arrSplit) {
//                String[] arrSplitEqual = null;
//                arrSplitEqual = strSplit.split("[=]");
//                if (arrSplitEqual.length > 1) {
//                    jsonObject.put(arrSplitEqual[0], arrSplitEqual[1]);
//                } else {
//                    if (StringUtils.isNotBlank(arrSplitEqual[0])) {
//                        jsonObject.put(arrSplitEqual[0], "");
//                    }
//                }
//            }
            jsonObject = JSON.parseObject(data);
            return JSONObject.toJavaObject(jsonObject, DeviceSensorMessage.class);
        }
        return null;
    }
}