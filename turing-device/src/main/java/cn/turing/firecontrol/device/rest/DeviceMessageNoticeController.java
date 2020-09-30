package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.DeviceMessageNotice;
import cn.turing.firecontrol.device.entity.DeviceMessageRecipients;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.SplitUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceMessageNotice")
@CheckClientToken
@CheckUserToken
public class DeviceMessageNoticeController extends BaseController<DeviceMessageNoticeBiz,DeviceMessageNotice,Integer> {

    @Autowired
    private DeviceNoticeBiz dnBiz;
    @Autowired
    private DeviceMessageRecipientsBiz dmrBiz;
    @Autowired
    private DeviceAlDnRelationBiz darBiz;
    @Autowired
    private IUserFeign iUserFeign;


    @RequestMapping(value = "/updateOrAdd",method = RequestMethod.POST)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "通知方式id用逗号隔开",paramType = "query"),
            @ApiImplicitParam(name = "noticeType",value = "通知类型[1=报警通知/2=故障通知]",paramType = "query")
    })
    @ApiOperation("修改报警通知和故障通知通知方式")
    public ObjectRestResponse updateOrAdd(@RequestBody  Map<String,String> param){
        String ids = param.get("ids");
        String noticeType = param.get("noticeType");
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(StringUtils.isBlank(noticeType)){
            throw new RuntimeException("报警通知类型不能为空");
        }
        //根据报警等级id查询已经添加得通知方式
        List<Integer> noIdLists_2 = baseBiz.selectByNoticeType(noticeType,BaseContextHandler.getTenantID());
        //noticeIds为空时删除所有
        if(StringUtils.isNotBlank(ids)){
            String[] noIds=ids.split(",");
            List<Integer> noIdLists_1=new ArrayList<>();
            for(int i=0;i<noIds.length;i++) {
                noIdLists_1.add(Integer.parseInt(noIds[i]));
            }
            List<Integer> add = new ArrayList<Integer>(noIdLists_1);//构建list1的副本
            add.removeAll(noIdLists_2);// 去除相同元素
            List<Integer> delete = new ArrayList<Integer>(noIdLists_2);//构建list2的副本
            delete.removeAll(noIdLists_1);// 去除相同元素
            //批量添加删除，报警等级与通知方式的关联表
            baseBiz.updateBatch(noticeType,add,delete);
        }else {
            //当取消时传空
            baseBiz.updateBatch(noticeType,null,noIdLists_2);
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectByNoticeType",method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeType",value = "通知类型[1=报警通知/2=故障通知]",paramType = "query")
    })
    @ApiOperation("根据通知类型查询通知方式")
    public ObjectRestResponse get(@RequestParam String noticeType){
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        if(StringUtils.isBlank(noticeType)){
            throw new RuntimeException("报警通知类型不能为空");
        }
        //根据通知方式类型获取通知方式ids
        List<Integer> intlists = baseBiz.selectByNoticeType(noticeType, BaseContextHandler.getTenantID());
        List<Map> deviceNotices =dnBiz.getAll();
        for(int i=0;i<deviceNotices.size();i++){
            Map map=deviceNotices.get(i);
            if(intlists.contains(map.get("id"))){
                map.put("checked",true);
            }else {
                map.put("checked",false);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("notice",deviceNotices);
        responseResult.data(map);
        return responseResult;
    }



    @IgnoreUserToken
    @RequestMapping(value = "/messageNotice",method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeType",value = "通知类型[0=预警通知/1=报警通知/2=故障通知]",paramType = "query"),
            @ApiImplicitParam(name = "levelId",value = "预警等级Id，当通知类型方式为预警通知时",paramType = "query")
    })
    @ApiOperation("消息接收人，消息通知方式")
    public ObjectRestResponse messageNotice(@RequestParam String noticeType,Integer levelId,@RequestParam String tenantId){
        ObjectRestResponse resultResponse = null;
        if(StringUtils.isBlank(noticeType)){
            throw new RuntimeException("报警通知类型不能为空");
        }
        Map<String,Object> map = new HashMap<>();
        //查询接收人
        List<DeviceMessageRecipients> recipients = dmrBiz.selectByNotictType(noticeType,tenantId);
        StringBuffer stringBuffer = new StringBuffer("");
        if(recipients!=null&&recipients.size()>0){
            for(int i=0;i<recipients.size();i++){
                if(i==recipients.size()-1){
                    stringBuffer.append(recipients.get(i).getMessageRecipientsUserid());
                }else {
                    stringBuffer.append(recipients.get(i).getMessageRecipientsUserid()+",");
                }
            }
        }else {
            //当没有接收人的时候默认推给站点管理员
            JSONObject temp =  iUserFeign.getTenantAdmin(tenantId);
            if(temp.isEmpty()||temp.get("id")==null){
                throw new RuntimeException("tenant--->"+tenantId+",没有找到！");
            }
            stringBuffer.append(temp.get("id").toString());
        }
        String ids = stringBuffer.toString();
        resultResponse = iUserFeign.queryUsers(ids,null,null,null,false,tenantId);
        List<Map> recipientsMap =(List<Map>) resultResponse.getData();
        List<DeviceNotice> notices = new ArrayList<>();
        String temp = "";
        //查询通知方式
//            当为预警时
        if("0".equals(noticeType)&&levelId!=null){
            //根据报警等级id获取通知方式ids
            temp = SplitUtil.merge(darBiz.selectByAlarmLevelId(levelId,tenantId));
            if(StringUtils.isNotBlank(temp)){
                notices = dnBiz.selectByAlarmLevelIdResult(temp);
            }else {
//                //当没有通知方式时默认推送APP
//                DeviceNotice deviceNotice = new DeviceNotice();
//                deviceNotice.setId(1);
//                deviceNotice.setNotice("APP推送");
//                notices.add(deviceNotice);
            }
            //当为报警时，故障时
        }else if("1".equals(noticeType)||"2".equals(noticeType)){
            temp = SplitUtil.merge(baseBiz.selectByNoticeType(noticeType,tenantId));
            if(StringUtils.isNotBlank(temp)){
                notices = dnBiz.selectByNoticeTypeResult(temp);
            }else {
//                //当没有通知方式时默认推送APP
//                DeviceNotice deviceNotice = new DeviceNotice();
//                deviceNotice.setId(1);
//                deviceNotice.setNotice("APP推送");
//                notices.add(deviceNotice);
            }
        }
        List<String> deviceList = new ArrayList<>();
        for(int i=0;i<notices.size();i++){
            DeviceNotice deviceNotice = notices.get(i);
            if(deviceNotice.getNotice()!=null&&"APP推送".equals(deviceNotice.getNotice().trim())){
                deviceList.add("0");
            }
            if(deviceNotice.getNotice()!=null&&"短信推送".equals(deviceNotice.getNotice().trim())){
                deviceList.add("1");
            }
            if(deviceNotice.getNotice()!=null&&"语音电话".equals(deviceNotice.getNotice().trim())){
                deviceList.add("2");
            }
        }
        map.put("notice",deviceList);
        map.put("recipients",recipientsMap);
        return new ObjectRestResponse<>().data(map);
    }



}