package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.entity.DeviceInspectionScheme;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.mapper.DeviceInspectionSchemeMapper;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceInspectionSchemeBiz extends BusinessBiz<DeviceInspectionSchemeMapper,DeviceInspectionScheme> {

    @Autowired
    private IUserFeign iUserFeign;

    //根据巡检路线判断绑定的巡检计划-
    public List<DeviceInspectionScheme> selectByInspectionRouteId(Integer routeId){
        Map<String,Object> map = new HashMap();
        map.put("routeId",routeId);
        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){
            map.put("tenantId", null);
        }else{
            map.put("tenantId", BaseContextHandler.getTenantID());
        }
        return mapper.selectByInspectionRouteId(map);
    }

    public TableResultResponse<Map> selectPageList(Query query, String routeName, Integer patrolCycle, String startTimeFirst, String startTimeLast, String endTimeFirst, String endTimeLast){
        String tenantId = null;
        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){
            tenantId =null;
        }else{
           tenantId = BaseContextHandler.getTenantID();
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = mapper.selectPageList(routeName,patrolCycle,startTimeFirst,startTimeLast,endTimeFirst,endTimeLast,tenantId);
        return  new TableResultResponse(result.getTotal(),list);
    }

    public List<Integer> getAllInspectionTouteId(Integer id){
        Map<String,Object> map = new HashMap();
        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){
            map.put("tenantId", null);
        }else{
            map.put("tenantId", BaseContextHandler.getTenantID());
        }
        map.put("id",id);
        return mapper.getAllInspectionTouteId(map);
    }

    public List<DeviceInspectionScheme> getListAll() {
        return mapper.getListAll();
    }

}