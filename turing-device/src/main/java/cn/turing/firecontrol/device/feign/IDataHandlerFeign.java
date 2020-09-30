package cn.turing.firecontrol.device.feign;

import cn.turing.firecontrol.auth.client.config.FeignApplyConfiguration;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "turing-datahandler",configuration = FeignApplyConfiguration.class)
public interface IDataHandlerFeign {
    @RequestMapping(value="/noticeRuleUser/listByUserIds",method = RequestMethod.GET)
    ObjectRestResponse listByUserIds(@RequestParam("userIds") String userId,@RequestParam("noticeType") String noticeType);
}
