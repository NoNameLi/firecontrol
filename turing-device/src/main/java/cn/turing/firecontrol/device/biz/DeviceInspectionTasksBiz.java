package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.mapper.DeviceInspectionTasksMapper;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.vo.LabelCountVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceInspectionTasksBiz extends BusinessBiz<DeviceInspectionTasksMapper,DeviceInspectionTasks> {

    @Autowired
    private DeviceInspectionRouteBiz dirBiz;
    @Autowired
    private DeviceRouteLabelBiz drlBiz;
    @Autowired
    private IUserFeign iUserFeign;
    @Autowired
    private DeviceIndoorRecordInspectionResultsBiz dirirBiz;
    @Autowired
    private DeviceOutdoorRecordInspectionResultsBiz dorirBiz;

    public TableResultResponse<DeviceInspectionTasks> selectMyTasksList(Query query, String userId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String,Object> map = new HashMap();
        map.put("userId",userId);
        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){
            map.put("tenantId", null);
        }else{
            map.put("tenantId", BaseContextHandler.getTenantID());
        }
        List<DeviceInspectionTasks> list = mapper.selectMyTasksList(map);
        List resultList = new ArrayList();
        for(DeviceInspectionTasks deviceInspectionTasks:list){
            DeviceInspectionRoute deviceInspectionRoute = dirBiz.selectById(deviceInspectionTasks.getInspectionRouteId());
            if(deviceInspectionRoute==null){
                continue;
            }
            Map map1 = new HashMap();
            map1.put("id",deviceInspectionTasks.getId());
            map1.put("routeId",deviceInspectionRoute.getId());//路线id
            map1.put("routeName",deviceInspectionRoute.getRouteName());
            map1.put("date",simpleDateFormat.format(deviceInspectionTasks.getInspectionDate())+" "+deviceInspectionTasks.getInspectionTimePeriod());
            map1.put("status",deviceInspectionTasks.getStatus());
            if("0".equals(deviceInspectionRoute.getRouteFlag())){//室内路线
                List<Integer> idList = drlBiz.selectByRouteId(deviceInspectionRoute.getId(),"0");
                //LabelCountVo labelCountVo =drlBiz.getIndoorCount(deviceInspectionRoute.getId());
                map1.put("type","0");
                if(idList.size()>0){
                    Integer jumpCount = dirirBiz.selectByTaskIdAndlabalId(deviceInspectionTasks.getId(), SplitUtil.merge(idList),"1",null);
                    Integer checkedCount = dirirBiz.selectByTaskIdAndlabalId(deviceInspectionTasks.getId(), SplitUtil.merge(idList),"0",null);
                    Integer uncheckedCount =idList.size()-jumpCount-checkedCount;
                    if(uncheckedCount<0){
                        map1.put("uncheckedCount","0");
                    }else{
                        map1.put("uncheckedCount",uncheckedCount);
                    }
                    map1.put("jumpCount",jumpCount);
                    map1.put("checkedCount",checkedCount);
                }else{
                    map1.put("uncheckedCount","0");
                    map1.put("jumpCount","0");
                    map1.put("checkedCount","0");
                }
            }
            if("1".equals(deviceInspectionRoute.getRouteFlag())){//室外路线
                List<Integer> idList = drlBiz.selectByRouteId(deviceInspectionRoute.getId(),"0");
                //LabelCountVo labelCountVo =drlBiz.getOutdoorCount(deviceInspectionRoute.getId());
                map1.put("type","1");
                if(idList.size()>0){
                    Integer jumpCount = dorirBiz.selectByTaskIdAndlabalId(deviceInspectionTasks.getId(), SplitUtil.merge(idList),"1",null);
                    Integer checkedCount = dorirBiz.selectByTaskIdAndlabalId(deviceInspectionTasks.getId(), SplitUtil.merge(idList),"0",null);
                    Integer uncheckedCount =idList.size()-jumpCount-checkedCount;
                    if(uncheckedCount<0){
                        map1.put("uncheckedCount","0");
                    }else{
                        map1.put("uncheckedCount",uncheckedCount);
                    }
                    map1.put("jumpCount",jumpCount);
                    map1.put("checkedCount",checkedCount);
                }else{
                    map1.put("uncheckedCount","0");
                    map1.put("jumpCount","0");
                    map1.put("checkedCount","0");
                }
            }
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    public TableResultResponse<DeviceInspectionTasks> selectTasksList(Query query) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String,Object> map = new HashMap();
        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){
            map.put("tenantId", null);
        }else{
            map.put("tenantId", BaseContextHandler.getTenantID());
        }
        map.put("status", "0");
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<DeviceInspectionTasks> list = mapper.selectTasksList(map);
        List resultList = new ArrayList();
        for(DeviceInspectionTasks deviceInspectionTasks:list){
            Map map1 = new HashMap();
            DeviceInspectionRoute deviceInspectionRoute = dirBiz.selectById(deviceInspectionTasks.getInspectionRouteId());
            map1.put("id",deviceInspectionTasks.getId());//任务id
            map1.put("routeId",deviceInspectionRoute.getId());//路线id
            map1.put("routeName",deviceInspectionRoute.getRouteName());
            map1.put("date",simpleDateFormat.format(deviceInspectionTasks.getInspectionDate())+" "+deviceInspectionTasks.getInspectionTimePeriod());
            map1.put("count",deviceInspectionRoute.getLabelCount());
            map1.put("patrolCycle",deviceInspectionTasks.getPatrolCycle());//巡检时长
            if("0".equals(deviceInspectionRoute.getRouteFlag())){
                map1.put("type","0");//室内路线
            }else{
                map1.put("type","1");//室外路线
            }
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    public DeviceInspectionTasks selectByRouteId(Integer routeId, String userID) {
        Map map = new HashMap();
        map.put("routeId",routeId);
        map.put("userID",userID);
        return mapper.selectByRouteId(map);
    }

    public void insert(DeviceInspectionTasks deviceInspectionTasks) {
       mapper.insert(deviceInspectionTasks);
    }

    public void update(Integer id) {
        mapper.update(id);
    }

    public List<DeviceInspectionTasks> getListAll() {
        return mapper.getListAll();
    }

    public DeviceInspectionTasks getById(Integer taskId) {
        return mapper.getById(taskId);
    }

    public Boolean getByRouteId(Integer routeId,Integer id) {
        Map<String,Object> map = new HashMap();
        map.put("routeId", routeId);
        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){
            map.put("tenantId", null);
        }else{
            map.put("tenantId", BaseContextHandler.getTenantID());
        }
        DeviceInspectionRoute deviceInspectionRoute =dirBiz.selectById(routeId);
        List<DeviceInspectionTasks> list= mapper.getByRouteId(map);
        //List<Integer> idList = drlBiz.selectByRouteId(routeId,"0");
        List countList = new ArrayList();
        for(DeviceInspectionTasks tasks:list){
            if("0".equals(deviceInspectionRoute.getRouteFlag())){//室内路线
                List<DeviceIndoorRecordInspectionResults> inspectionResults= dirirBiz.selectByTaskId(tasks.getId(),id);
                countList.add(inspectionResults.size());
            }else{
                List<DeviceOutdoorRecordInspectionResults> inspectionResults =dorirBiz.selectByTaskId(tasks.getId(),id);
                countList.add(inspectionResults.size());
            }
        }
        if(countList.contains(0)){
            return true;
        }else{
            return false;
        }
    }

    public List<DeviceInspectionTasks> getAllList() {
        Map<String,Object> map = new HashMap();
        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){
            map.put("tenantId", null);
        }else{
            map.put("tenantId", BaseContextHandler.getTenantID());
        }
        return mapper.getAllList(map);
    }
    public Long selectTaskCount(){
        return mapper.selectTaskCount();
    }
}