package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceVideoAnalysisSolutionBiz;
import cn.turing.firecontrol.device.entity.DeviceVideoAnalysisSolution;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created on 2019/02/20 11:38
 *
 * @Description
 * @Version V1.0
 */
@Api(tags = "视频分析方案")
@RestController
@RequestMapping("videoAnalysisSolution")
@CheckUserToken
@CheckClientToken
public class DeviceVideoAnalysisSolutionController  extends BaseController<DeviceVideoAnalysisSolutionBiz, DeviceVideoAnalysisSolution,Integer> {

    @GetMapping("solutionStatus")
    @ApiOperation("查询分析方案服务概况")
    public ObjectRestResponse getSolutionStatus(){
        try {
            List<Map<String,Object>> list = baseBiz.querySolutionStatus(BaseContextHandler.getTenantID());
            return new ObjectRestResponse().data(list);
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
    }

    @GetMapping("solutions")
    @ApiOperation("查询分析方案列表")
    public ObjectRestResponse getSolutions(){
        try {
            List<Map<String,Object>> list = baseBiz.querySolutions(BaseContextHandler.getTenantID());
            return new ObjectRestResponse().data(list);
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
    }

}
