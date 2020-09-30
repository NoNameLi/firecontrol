package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.datahandler.biz.NoticeRuleBiz;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.entity.NoticeRule;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.FiredoorAbnormalHandler;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.VideoAbnormalHandler;
import cn.turing.firecontrol.datahandler.vo.NoticeRuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created on 2019/03/18 10:28
 *
 * @Description TODO
 * @Version V1.0
 */
@RestController
@RequestMapping("noticeRule")
@CheckClientToken
@CheckUserToken
@Api(tags = "通知推送规则")
public class NoticeRuleController extends BaseController<NoticeRuleBiz, NoticeRule,Long> {

    @RequestMapping(value = "/listNoticeRule",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统ID",paramType = "query"),
            @ApiImplicitParam(name = "ruleName",value = "规则名称",paramType = "query")
    })
    public TableResultResponse<NoticeRule> listNoticeRule(@RequestParam(defaultValue = "1") Integer page,
                                                                   @RequestParam(defaultValue = "15") Integer limit,
                                                                    Integer channelId){
        return this.baseBiz.listNoticeRule(page,limit,channelId);
    }

    @RequestMapping(value = "/listNoticeRuleForUser",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统ID",paramType = "query"),
            @ApiImplicitParam(name = "ruleName",value = "规则名称",paramType = "query"),
            @ApiImplicitParam(name = "noticeType",value = "消息类型",paramType = "query")
    })
    public TableResultResponse<NoticeRuleVo> listNoticeRuleForUser(@RequestParam(defaultValue = "1") Integer page,
                                                                   @RequestParam(defaultValue = "15") Integer limit,
                                                                   @RequestParam  String userId, Integer channelId, String ruleName,
                                                                   @RequestParam(defaultValue = "1") String noticeType){
        return this.baseBiz.listNoticeRuleForUser(page,limit,userId,channelId,ruleName,noticeType);
    }


    @RequestMapping(value = "/del",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除推送规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "推送规则ID",paramType = "query")
    })
    public ObjectRestResponse deleteById(@RequestParam  Long id){
        ObjectRestResponse response = new ObjectRestResponse();
        return response.data(this.baseBiz.deleteNoticeRuleById(id));
    }

    @RequestMapping(value = "/countBindUser",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除推送规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "推送规则ID",paramType = "query")
    })
    public ObjectRestResponse countBindUser(@RequestParam  Long id){
        ObjectRestResponse response = new ObjectRestResponse();
        return response.data(this.baseBiz.countBindUser(id));
    }

//    @RequestMapping(value = "/test",method = RequestMethod.GET)
//    @ResponseBody
//    public ObjectRestResponse test(){
//        ObjectRestResponse response = new ObjectRestResponse();
//        //this.baseBiz.test();
//        //String json = "{\"deviceSerial\":\"视频识别\",\"analysisDataIds\":[\"d007a2e18aa6475a93009e0be8eeeaa3\"],\"alarmType\":\"火警\",\"alarmTime\":\"2019-02-25 18:27:39\",\"tenantId\":\"a164JAy9\",\"deviceNo\":\"202480708\",\"deviceName\":\"测试摄像头\",\"pictures\":[\"http://file.tmc.turing.ac.cn/video_202480707_1551090459015\"]}";
//        //{"deviceSerial":"视频识别","analysisDataIds":["d007a2e18aa6475a93009e0be8eeeaa3"],"alarmType":"火警","alarmTime":"2019-02-25 18:27:39","tenantId":"a164JAy9","deviceNo":"202480707","deviceName":"测试摄像头","pictures":["http://file.tmc.turing.ac.cn/video_202480707_1551090459015"]}
////        videoAbnormalHandler.handleAbnormal(json);
//        String buidingName = "未来之光2栋";
//        String deviceSeries = "防火门001";
//        String abnormalType = "报警";
//        Map<String,String> vmsParams = new HashMap<>();
//        vmsParams.put("building",buidingName);
//        vmsParams.put("deviceSeries",deviceSeries);
//        vmsParams.put("alrmType",abnormalType);
//        String[] smsParams = new String[]{buidingName,deviceSeries,abnormalType};
//        abnormalHandler.sendAlarmNotice("a164JAy9","门打开","门打开", smsParams, vmsParams,"8888888888", new Date());
//        return response;
//    }

}
