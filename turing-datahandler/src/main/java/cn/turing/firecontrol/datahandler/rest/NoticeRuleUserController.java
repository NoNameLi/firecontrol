package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.biz.NoticeRuleUserBiz;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleUser;
import cn.turing.firecontrol.datahandler.util.Constants;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * Created on 2019/03/18 10:32
 *
 * @Description TODO
 * @Version V1.0
 */
@RestController
@RequestMapping("noticeRuleUser")
@CheckClientToken
@CheckUserToken
@Api(tags = "通知推送规则与用户关联关系")
public class NoticeRuleUserController extends BaseController<NoticeRuleUserBiz, NoticeRuleUser,Long> {

    @Autowired
    private NoticeRuleUserBiz  noticeRuleUserBiz;

    @RequestMapping(value = "/configNoticeRuleForUser",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("配置用户与通知规则关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeRuleId",value = "推送规则ID",paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "用户ID",paramType = "query"),
            @ApiImplicitParam(name = "status",value = "启停状态",paramType = "query"),
            @ApiImplicitParam(name = "noticeType",value = "消息类型",paramType = "query")
    })
    public ObjectRestResponse configNoticeRuleForUser(@RequestBody String json) {
        ObjectRestResponse response = new ObjectRestResponse();
        JSONObject obj = JSONObject.parseObject(json);
        String status = obj.getString("status");
        String noticeType = obj.getString("noticeType");
        if(!"1".equals(status)){
            status = "0";
        }
        if(!"1".equals(noticeType) && !"2".equals(noticeType)){
            noticeType = "1";
        }
        noticeRuleUserBiz.configNoticeRuleForUser(obj.getLong("noticeRuleId"),obj.getString("userId"),status,noticeType);
        return response;
    }


    @RequestMapping(value = "/listByUserIds",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("批量查询用户是否有推送规则与之关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds",value = "用户ID",paramType = "query"),
    })
    public ObjectRestResponse listByUserIds(@RequestParam String userIds,@RequestParam(defaultValue = "1") String noticeType) {
        ObjectRestResponse response = new ObjectRestResponse();
        response.data(noticeRuleUserBiz.listByUserIds(userIds,noticeType));
        return response;
    }


}
