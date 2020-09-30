package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.turing.firecontrol.device.entity.DeviceNetworkingUnit;
import cn.turing.firecontrol.device.mapper.DeviceNetworkingUnitMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Service
public class DeviceNetworkingUnitBiz extends BusinessBiz<DeviceNetworkingUnitMapper,DeviceNetworkingUnit> {

    @Autowired
    private DeviceNetworkingUnitMapper deviceNetworkingUnitMapper;

/*    //分页查询，根据单位名称搜索
    public TableResultResponse<Map<String, Object>> selectPageList(Query query, String unitName) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceNetworkingUnit entity = new DeviceNetworkingUnit();
        entity.setOName(unitName);
        List<DeviceNetworkingUnit> list = deviceNetworkingUnitMapper.selectPageList(entity);
        return new TableResultResponse(result.getTotal(),list);
    }*/

    //分页查询，根据单位名称搜索
    public List<DeviceNetworkingUnit> getAll( String unitName) {
        DeviceNetworkingUnit entity = new DeviceNetworkingUnit();
        entity.setOName(unitName);
        List<DeviceNetworkingUnit> list = deviceNetworkingUnitMapper.selectPageList(entity);
        return list;
    }
    //查看测点，代号名是否重复
    public Integer selectByCount(String oName){
        DeviceNetworkingUnit entity=new DeviceNetworkingUnit();
        entity.setOName(oName);
        return deviceNetworkingUnitMapper.selectByCount(entity);
    }

    public DeviceNetworkingUnit getById(Integer id) {
        return deviceNetworkingUnitMapper.getById(id);
    }

    public List<DeviceNetworkingUnit> getAllUnit(String oName){
        return mapper.getAllUnit(oName);
    }
}