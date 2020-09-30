package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceAlDnRelationBiz;
import cn.turing.firecontrol.device.biz.DeviceAlarmLevelBiz;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.entity.DeviceAlDnRelation;
import cn.turing.firecontrol.device.entity.DeviceAlarmLevel;
import cn.turing.firecontrol.device.util.Constants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.*;

@RestController
@RequestMapping("deviceAlDnRelation")
@CheckClientToken
@CheckUserToken
public class DeviceAlDnRelationController extends BaseController<DeviceAlDnRelationBiz,DeviceAlDnRelation,Integer> {

    @Autowired
    private DeviceAlDnRelationBiz darBiz;
    @Autowired
    private DeviceAlarmLevelBiz dalBiz;
    @Autowired
    private DeviceNoticeBiz dnBiz;

    @ApiOperation("分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse list(@RequestParam String page, @RequestParam String limit){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return dalBiz.selectPageList(query,false);
    }

    @RequestMapping(value = "/updateOrAdd",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改或者增加报警等级绑定的通知方式")
    public ObjectRestResponse update(@RequestBody Map<String,Object> params){
        DeviceAlarmLevel entity = new DeviceAlarmLevel();
        entity.setId((Integer) params.get("id"));
        String noticeIds = (String) params.get("noticeIds");
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(entity!=null){
            DeviceAlarmLevel deviceAlarmLevel=dalBiz.selectById(entity.getId());
            //对象不存在
            if(deviceAlarmLevel==null){
                throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            //根据报警等级id查询已经添加得通知方式
            List<Integer> noIdLists_2= darBiz.selectByAlarmLevelId(entity.getId(), BaseContextHandler.getTenantID());
            //noticeIds为空时删除所有
            if(!"".equals(noticeIds)){
                String[] noIds=noticeIds.split(",");
                List<Integer> noIdLists_1=new ArrayList<>();
                for(int i=0;i<noIds.length;i++) {
                    noIdLists_1.add(Integer.parseInt(noIds[i]));
                }
                List<Integer> add = new ArrayList<Integer>(noIdLists_1);//构建list1的副本
                add.removeAll(noIdLists_2);// 去除相同元素
                List<Integer> delete = new ArrayList<Integer>(noIdLists_2);//构建list2的副本
                delete.removeAll(noIdLists_1);// 去除相同元素
                //批量添加删除，报警等级与通知方式的关联表
                darBiz.updateBatch(entity.getId(),add,delete);
            }else {
                //当取消时传空
                darBiz.updateBatch(entity.getId(),null,noIdLists_2);
            }
        }
        return responseResult;
    }


    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse get(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        DeviceAlarmLevel deviceAlarmLevel = dalBiz.selectById(id);
        //根据报警等级id获取通知方式ids
        List<Integer> intlists = darBiz.selectByAlarmLevelId(id,BaseContextHandler.getTenantID());

        List<Map> deviceNotices =dnBiz.getAll();
        for(int i=0;i<deviceNotices.size();i++){
            Map map=deviceNotices.get(i);
            if(intlists.contains(map.get("id"))){
                map.put("checked",true);
            }else {
                map.put("checked",false);
            }
        }
/*        if(intlists!=null&&intlists.size()>0){
            deviceNotices = dnBiz.getByIds(SplitUtil.merge(intlists));
        }*/
        Map<String,Object> map = new HashMap<>();
        map.put("alarmLevel",deviceAlarmLevel);
        map.put("notice",deviceNotices);
        responseResult.data(map);
        return responseResult;
    }

}