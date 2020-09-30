package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.datahandler.biz.NoticeLogBiz;
import cn.turing.firecontrol.datahandler.entity.NoticeLog;
import cn.turing.firecontrol.datahandler.entity.NoticeRule;
import com.github.pagehelper.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2019/03/18 10:26
 *
 * @Description TODO
 * @Version V1.0
 */
@RestController
@RequestMapping("noticeLog")
@CheckClientToken
@CheckUserToken
@Api(tags = "通知推送日志")
public class NoticeLogController extends BaseController<NoticeLogBiz, NoticeLog,Long> {

    @RequestMapping(value = "/listNoticeLog",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "mobilePhone",value = "用户手机号",paramType = "query"),
            @ApiImplicitParam(name = "noticeType",value = "通知方式",paramType = "query")
    })
    public TableResultResponse<NoticeLog> listNoticeLog(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "15") Integer limit,
                                                                  String mobilePhone, String noticeType){

        return this.baseBiz.listNoticeLog(page,limit,mobilePhone,noticeType);
    }

    @RequestMapping(value = "/queryLastLog",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询设备最新的日志")
    public ObjectRestResponse queryLastLog(Long sensorId,Integer channelId,String userId){
        ObjectRestResponse response = new ObjectRestResponse();
        return response.data(this.baseBiz.queryLastLog(sensorId,channelId,userId));
    }
}