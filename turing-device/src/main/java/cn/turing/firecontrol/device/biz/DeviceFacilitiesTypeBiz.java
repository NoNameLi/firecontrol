package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceFacilitiesType;
import cn.turing.firecontrol.device.entity.DeviceFireMain;
import cn.turing.firecontrol.device.entity.DeviceInspectionRoute;
import cn.turing.firecontrol.device.entity.DeviceOutdoorLabel;
import cn.turing.firecontrol.device.mapper.DeviceFacilitiesTypeMapper;
import cn.turing.firecontrol.device.mapper.DeviceOutdoorLabelMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceFacilitiesTypeBiz extends BusinessBiz<DeviceFacilitiesTypeMapper,DeviceFacilitiesType> {

    public List<DeviceFacilitiesType> selectByType(String equipmentType) {
        return mapper.selectByType(equipmentType);
    }
    //分页查询搜索测点
    public TableResultResponse<Map> selectPageList(Query query, String equipmentType){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = mapper.selectPageList(equipmentType);
        return new TableResultResponse(result.getTotal(),list);
    }

    public Integer selectByCount(String equipmentType){
        return mapper.selectByCount(equipmentType);
    }

    public Set<Integer> getAllTypeId(){
        return mapper.getAllTypeId();
    }

    public List<String> getByType() {
        return mapper.getByType();
    }

    public DeviceFacilitiesType getById(Integer id) {
        return mapper.getById(id);
    }
}