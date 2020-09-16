package cn.turing.firecontrol.admin.rest;

import cn.turing.firecontrol.admin.biz.LoginLogBiz;
import cn.turing.firecontrol.admin.entity.LoginLog;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("loginLog")
@Api("登陆日志")
public class LoginLogController extends BaseController<LoginLogBiz,LoginLog,Integer> {

    @ApiOperation("分页查询登录日志（按时间倒序）")
    @GetMapping("pageList")
    @CheckUserToken
    @CheckClientToken
    public TableResultResponse<LoginLog> pageList(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return baseBiz.list(query.getPage(),query.getLimit());
    }





}