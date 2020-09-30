package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.entity.DeviceTestRoute;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import cn.turing.firecontrol.device.mapper.DeviceTestRouteMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

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
public class DeviceTestRouteBiz extends BusinessBiz<DeviceTestRouteMapper,DeviceTestRoute> {


    //分页查询搜索路线
    public TableResultResponse<Map> selectPageList(Query query, String routeName, String routeFlag){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = mapper.selectPageList(routeName,routeFlag);
        return new TableResultResponse(result.getTotal(),list);
    }

    public Integer selectByCount(String routeName, String routeFlag){
        return mapper.selectByCount(routeName,routeFlag);
    }

    public List<Map> getNotIds(String ids,Integer id){
        return mapper.getNotIds(ids,id);
    }
}