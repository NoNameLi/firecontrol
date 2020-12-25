package cn.turing.firecontrol.device.feign;

import cn.turing.firecontrol.auth.client.config.FeignApplyConfiguration;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "turing-admin",configuration = FeignApplyConfiguration.class)
public interface AdminFeign {
    @RequestMapping(value="channel/getByIds",method = RequestMethod.GET)
    ObjectRestResponse getByIds(@RequestParam("ids")String ids);

    @RequestMapping(value = "tenant/getByTenantNo",method = RequestMethod.GET)
    JSONObject getByTenantNo(@RequestParam("tenantNo") String tenantNo);
}
