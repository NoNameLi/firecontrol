package cn.turing.firecontrol.admin.feign;/**
 * Created by hanyong on 2018/09/10 19:01
 */

import cn.turing.firecontrol.auth.client.config.FeignApplyConfiguration;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by hanyong on 2018/09/10 19:01
 *
 * @Description 调用设备管理服务
 * @Version V1.0
 */
@FeignClient(value = "turing-device",configuration = FeignApplyConfiguration.class)
public interface DeviceFeign {

    /**
     * 删除Device服务中与本租户相关的数据
     * @param tenantId
     * @return
     */
    @RequestMapping(value = "/turingBusiness/deleteTenant",method = RequestMethod.GET)
    public ObjectRestResponse deleteTenant(@RequestParam(value = "tenantId") String tenantId);



}
