package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceFireMain;
import cn.turing.firecontrol.device.mapper.DeviceFireMainMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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
public class DeviceFireMainBiz extends BusinessBiz<DeviceFireMainMapper,DeviceFireMain> {

    //分页查询搜索测点
    public TableResultResponse<Map> selectPageList(Query query,DeviceFireMain deviceFireMain){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> lists = mapper.selectPageList(deviceFireMain);
        for(int i=0;i<lists.size();i++){
            String gis = (String) lists.get(i).get("gis");
            if(StringUtils.isBlank(gis)){
                lists.get(i).put("gis","未标记");
            }else {
                lists.get(i).put("gis","已标记");
            }
        }
        return new TableResultResponse(result.getTotal(),lists);
    }

    //主机+ip是否重复
    public Integer selectIgnoreTenantByCount(String serverIp,String port){
        DeviceFireMain deviceFireMain = new DeviceFireMain();
        deviceFireMain.setServerIp(serverIp);
        deviceFireMain.setPort(port);
        return mapper.selectIgnoreTenantByCount(deviceFireMain);
    }

    //查询所有的没有删除的数据
    public List<DeviceFireMain> getIgnoreTenantAll(){
        return mapper.getIgnoreTenantAll();
    }

    public DeviceFireMain getById(Integer id) {
        return mapper.getById(id);
    }
}