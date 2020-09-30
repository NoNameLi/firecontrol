package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceEarlyWarningBiz;
import cn.turing.firecontrol.device.entity.DeviceEarlyWarning;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.SplitUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceEarlyWarning")
@CheckClientToken
@CheckUserToken
public class DeviceEarlyWarningController extends BaseController<DeviceEarlyWarningBiz,DeviceEarlyWarning,Integer> {

    @ApiOperation("分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "handleFlag",value = "是否读取[1=是/0=否]",paramType = "query")
    })
    @ResponseBody
    public TableResultResponse<DeviceEarlyWarning> list(@RequestParam String page, @RequestParam String limit,@RequestParam String handleFlag){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,handleFlag);
    }


    @RequestMapping(value = "/updateBatch",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("批量读取预警通知消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "id拼成字符串，用逗号隔开",paramType = "query")
    })
    public ObjectRestResponse updateBatch(@RequestParam String ids) {
        if(StringUtils.isNotBlank(ids)){
            Integer[] lists = SplitUtil.splitInt(ids);
            DeviceEarlyWarning entity = null;
            String handleName ="{"+BaseContextHandler.getUsername()+"} ({"+BaseContextHandler.getName()+"})";
            for(int i=0;i<lists.length;i++){
                entity = new DeviceEarlyWarning();
                entity.setId(lists[i]);
                entity.setHandleFlag("1");
                entity.setHandlePerson(handleName);
                baseBiz.updateSelectiveById(entity);
            }
        }
        return new ObjectRestResponse();
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/batchInsert",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("批量添加预警记录")
    public ObjectRestResponse batchInsert(@RequestBody List<DeviceEarlyWarning> list){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(list.size()==0){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            for(DeviceEarlyWarning deviceEarlyWarning:list){
                baseBiz.insertSelective(deviceEarlyWarning);
            }
        }
        return responseResult;
    }

}