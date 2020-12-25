package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceMessageRecipientsBiz;
import cn.turing.firecontrol.device.entity.DeviceMessageRecipients;
import cn.turing.firecontrol.device.feign.IDataHandlerFeign;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.vo.UserVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceMessageRecipients")
@CheckClientToken
@CheckUserToken
public class DeviceMessageRecipientsController extends BaseController<DeviceMessageRecipientsBiz, DeviceMessageRecipients, Integer> {

    @Autowired
    private IUserFeign iUserFeign;

    @Autowired
    private IDataHandlerFeign iDataHandlerFeign;

    private static final Logger log = LoggerFactory.getLogger(DeviceMessageRecipientsController.class);

    @RequestMapping(value = "/findByMeassage", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("批量查询用户信息（模糊搜索）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeType", value = "通知类型[0=预警通知/1=报警通知/2=故障通知]", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "真实姓名", paramType = "query"),
            @ApiImplicitParam(name = "mobilePhone", value = "电话", paramType = "query")
    })
    public ObjectRestResponse findByMeassage(@RequestParam String noticeType, String username, String name, String mobilePhone) {
/*        ObjectRestResponse resultResponse = null;
        List<DeviceMessageRecipients> lists = baseBiz.selectByNotictType(noticeType,BaseContextHandler.getTenantID());
        StringBuffer stringBuffer = new StringBuffer("");
        if(lists!=null&&lists.size()>0){
            for(int i=0;i<lists.size();i++){
                if(i==lists.size()-1){
                    stringBuffer.append(lists.get(i).getMessageRecipientsUserid());
                }else {
                    stringBuffer.append(lists.get(i).getMessageRecipientsUserid()+",");
                }
            }
        }
        String ids = stringBuffer.toString();*/
        //当请求的userId为空，的用户时，不需要掉接口
//        if("".equals(ids)){
//            resultResponse = new TableResultResponse();
//        }else {

//            resultResponse = iUserFeign.queryUsers("",username,name,mobilePhone,true);
//        }
        String tenantId = BaseContextHandler.getTenantID();
        log.info("用户站点ID:" + tenantId);
        return iUserFeign.queryUsers("", username, name, mobilePhone, true, tenantId);
    }

    @RequestMapping(value = "/pagelist", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeType", value = "通知类型[0=预警通知/1=报警通知/2=故障通知]", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页显示条数", paramType = "query")
    })
    public Object pagelist(@RequestParam String noticeType, String page, String limit) {
        TableResultResponse resultResponse = new TableResultResponse();
        ObjectRestResponse objectResponse = new ObjectRestResponse();
        List<DeviceMessageRecipients> lists = baseBiz.selectByNotictType(noticeType, BaseContextHandler.getTenantID());
        StringBuffer stringBuffer = new StringBuffer("");
        if (lists != null && lists.size() > 0) {
            for (int i = 0; i < lists.size(); i++) {
                if (i == lists.size() - 1) {
                    stringBuffer.append(lists.get(i).getMessageRecipientsUserid());
                } else {
                    stringBuffer.append(lists.get(i).getMessageRecipientsUserid() + ",");
                }
            }
        }
        String ids = stringBuffer.toString();
        //当请求的userId为空，的用户时，不需要掉接口
        if ("".equals(ids)) {
            if (ValidatorUtils.hasAnyBlank(page, limit)) {
                objectResponse.setData(new ArrayList<>());
                return objectResponse;
            } else {
                return resultResponse;
            }
        } else {

            TableResultResponse response = null;
            List list = null;
            if (page == null || "".equals(page.trim()) || Integer.valueOf(page) <= 0) {
                page = "1";
            }
            if (limit == null || "".equals(limit.trim()) || Integer.valueOf(limit) <= 0) {
                limit = "10";
            }

            response = iUserFeign.queryUsersByPage(ids, null, null, null, false, page, limit, BaseContextHandler.getTenantID());

            list = response.getData().getRows();
            List<UserVo> userVoList = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            if (list != null && list.size() > 0) {
                for (Object obj : list) {
                    JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(obj));
                    UserVo vo = new UserVo();
                    vo.setGroupName(json.getString("groupName"));
                    vo.setId(json.getString("id"));
                    vo.setMobilePhone(json.getString("mobilePhone"));
                    vo.setName(json.getString("name"));
                    vo.setUsername(json.getString("username"));
                    vo.setHasNoticeRule(0);
                    userVoList.add(vo);
                    sb.append(vo.getId());
                    sb.append(",");
                }

                ObjectRestResponse ruleUserList = iDataHandlerFeign.listByUserIds(sb.substring(0, sb.length() - 1), noticeType);

                JSONArray arr = JSONArray.parseArray(JSONArray.toJSONString(ruleUserList.getData()));
                for (UserVo vo : userVoList) {
                    for (int i = 0; i < arr.size(); i++) {
                        String userId = ((JSONObject) arr.get(i)).getString("userId");
                        if (vo.getId().equals(userId)) {
                            vo.setHasNoticeRule(1);
                        }
                    }
                }

                response.getData().setRows(userVoList);
            }
            return response;
        }

    }

    @RequestMapping(value = "/addBatch", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("批量添加通知联系人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeType", value = "通知类型[0=预警通知/1=报警通知/2=故障通知]", paramType = "query"),
            @ApiImplicitParam(name = "userIds", value = "接收人的userId用逗号隔开", paramType = "query")
    })
    public ObjectRestResponse addBatch(@RequestBody Map<String, String> param) {
        String noticeType = param.get("noticeType");
        String userIds = param.get("userIds");
        if (ValidatorUtils.hasAnyBlank(noticeType)) {
            throw new RuntimeException("缺少参数");
        }
        //查询已经添加的usesIds
        List<String> noIdLists_2 = baseBiz.findByNotictType(noticeType);
        //userIds为空时删除所有
        if (StringUtils.isNotBlank(userIds)) {
            String[] noIds = userIds.split(",");
            List<String> noIdLists_1 = new ArrayList<>();
            for (int i = 0; i < noIds.length; i++) {
                noIdLists_1.add(noIds[i]);
            }
            List<String> add = new ArrayList<String>(noIdLists_1);//构建list1的副本
            add.removeAll(noIdLists_2);// 去除相同元素
            List<String> delete = new ArrayList<String>(noIdLists_2);//构建list2的副本
            delete.removeAll(noIdLists_1);// 去除相同元素
            //批量添加删除，报警等级与通知方式的关联表
            baseBiz.updateBatch(noticeType, add, delete);
        } else {
            //当取消时传空
            baseBiz.updateBatch(noticeType, null, noIdLists_2);
        }
        return new ObjectRestResponse();
    }


    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", paramType = "query"),
            @ApiImplicitParam(name = "noticeType", value = "通知类型[0=预警通知/1=报警通知/2=故障通知]", paramType = "query")
    })
    public ObjectRestResponse delete(@RequestParam String noticeType, @RequestParam String id) {
        //根据用户id删除
        DeviceMessageRecipients entity = new DeviceMessageRecipients();
        entity.setNoticeType(noticeType);
        entity.setMessageRecipientsUserid(id);
        entity.setTenantId(BaseContextHandler.getTenantID());
        baseBiz.delete(entity);
        return new ObjectRestResponse();
    }
}