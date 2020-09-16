package cn.turing.firecontrol.admin.rest;

import cn.turing.firecontrol.admin.biz.ChannelBiz;
import cn.turing.firecontrol.admin.biz.UserBiz;
import cn.turing.firecontrol.admin.biz.ValicodeBiz;
import cn.turing.firecontrol.admin.entity.Channel;
import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.admin.vo.ChannelDto;
import cn.turing.firecontrol.auth.client.annotation.*;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.BooleanUtil;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("channel")
@CheckClientToken
@CheckUserToken
@Api("栏目管理")
public class ChannelController extends BaseController<ChannelBiz,Channel,Integer> {

    @Autowired
    private UserBiz userBiz;

    /**
     * 按站点查询栏目
     * @param tenantId
     * @return 按栏目类型组成的树状结构数据
     */
    @ApiOperation("按站点查询栏目")
    @GetMapping("tree")
    public ObjectRestResponse<List<Map<String,Object>>> tree(String tenantId){
        List<Map<String,Object>> list = baseBiz.tree(tenantId);
        return new ObjectRestResponse<>().data(list);
    }

    /**
     * 查询站点的所有栏目,如果是超级管理员则查询所有
     * @return 一个栏目数据
     */
    @ApiOperation("查询站点的所有栏目,如果是超级管理员则查询所有")
    @GetMapping("list")
    public ObjectRestResponse<List<Map<String,Object>>> list(){
        String tenantId = null;
        String userid = BaseContextHandler.getUserID();
        User user = userBiz.selectById(userid);
        if(BooleanUtil.BOOLEAN_FALSE.equals(user.getIsSuperAdmin())){
            tenantId = BaseContextHandler.getTenantID();
        }
        List<Map<String,Object>> list = baseBiz.list(tenantId);
        return new ObjectRestResponse<>().data(list);
    }

    @ApiOperation("根据栏目ID查询栏目信息")
    @GetMapping("get")
    @IgnoreUserToken
    public ObjectRestResponse<Channel> get(String channelId){
        if(ValidatorUtils.hasAnyBlank(channelId)){
            throw new ParamErrorException("栏目ID不能为空");
        }
        Channel channel  = baseBiz.selectById(channelId);
        return new ObjectRestResponse<>().data(channel);
    }

    @ApiOperation("根据ids查询")
    @GetMapping("getByIds")
    @IgnoreUserToken
    public ObjectRestResponse<Channel> getByIds(String ids){
        if(ValidatorUtils.hasAnyBlank(ids)){
            throw new ParamErrorException("栏目ID不能为空");
        }
        List<ChannelDto> list = baseBiz.getByIds(ids);
        return new ObjectRestResponse<>().data(list);
    }




}