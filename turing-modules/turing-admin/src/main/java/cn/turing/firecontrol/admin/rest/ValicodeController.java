package cn.turing.firecontrol.admin.rest;

import cn.turing.firecontrol.admin.biz.UserBiz;
import cn.turing.firecontrol.admin.biz.ValicodeBiz;
import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.util.SmsUtil;
import cn.turing.firecontrol.common.util.StringHelper;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import com.aliyuncs.exceptions.ClientException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("valicode")
@Controller
//@CheckClientToken
//@CheckUserToken
@Api("验证码管理")
public class ValicodeController {

    @Autowired
    private UserBiz userBiz;
    @Autowired
    private ValicodeBiz valicodeBiz;


    /**
     * 给指定用户发送短信验证码，返回用户手机号（中间四位数用*代替）
     * @param username
     * @return
     */
    @ApiOperation("发送短信验证码")
    @RequestMapping(value = "/sms",method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<String> smsValicode(String username){
        if(StringUtils.isBlank(username)){
            throw new ParamErrorException("用户名不能为空");
        }
        User user = userBiz.getUserByUsername(username);
        if(user == null){
            throw new ParamErrorException("用户不存在");
        }
        valicodeBiz.sendSmsCode(user.getMobilePhone(),username,6);
        String mobilePhone = user.getMobilePhone();
        mobilePhone = mobilePhone.substring(0,3) + "****" + mobilePhone.substring(7);
        return new ObjectRestResponse().data(mobilePhone);
    }

    /**
     * 验证短信验证码是否正确，如果正确则返回200状态码，错误则返回错误提示语
     * @param username
     * @param code
     * @return
     */
    @ApiOperation("验证短信验证码")
    @RequestMapping(value = "/sms/validate",method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse validateCode(String username,String code){
        if(ValidatorUtils.hasAnyBlank(username,code)){
            throw new RuntimeException("参数不能为空");
        }
        valicodeBiz.validateCode(username,code);
        return new BaseResponse();
    }




}
