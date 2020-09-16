package cn.turing.firecontrol.auth.feign;

import cn.turing.firecontrol.auth.configuration.FeignConfiguration;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(value = "turing-admin",configuration = FeignConfiguration.class)
public interface ILoginLogService {

    @RequestMapping(value = "/loginLog",method = RequestMethod.POST)
    public ObjectRestResponse<Map<String,String>> add(Map<String,String> entity);




}
