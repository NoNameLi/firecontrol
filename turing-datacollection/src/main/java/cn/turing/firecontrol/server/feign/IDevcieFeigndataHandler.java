package cn.turing.firecontrol.server.feign;

import cn.turing.firecontrol.auth.client.config.FeignApplyConfiguration;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "turing-datahandler",configuration = FeignApplyConfiguration.class)
public interface IDevcieFeigndataHandler {
    //恢复异常
    @RequestMapping(value = "/deviceAbnormal/restore",method = RequestMethod.GET)
    public ObjectRestResponse abnormalRestore(@RequestParam("sensorNo") String sensorNo);

    //消防主机恢复异常
    @RequestMapping(value = "/deviceFireMainAbnormal/restore",method = RequestMethod.GET)
    public ObjectRestResponse fireMainAbnormalRestore(@RequestParam("serverIp") String ip, @RequestParam("port") String port, @RequestParam("sensorLoop") String sensorLoop, @RequestParam("address") String localtionNo);

}
