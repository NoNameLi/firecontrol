package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceCollectingDevice;
import cn.turing.firecontrol.device.mapper.DeviceCollectingDeviceMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

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
public class DeviceCollectingDeviceBiz extends BusinessBiz<DeviceCollectingDeviceMapper,DeviceCollectingDevice> {

    @Autowired
    private DeviceCollectingDeviceMapper deviceCollectingDeviceMapper;

    public TableResultResponse<Map> selectQuery(Query query, DeviceCollectingDevice entity) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = deviceCollectingDeviceMapper.selectQuery(entity);
        for(int i=0;i<list.size();i++){
            Map<String,Object> map = list.get(i);
            String status = (String) map.get("status");
            if("0".equals(status)){
                map.put("status","故障");
            }else if ("1".equals(status)){
                map.put("status","正常");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public List<DeviceCollectingDevice> selectByCollectingDeviceTypeId(Integer id) {
        return  deviceCollectingDeviceMapper.selectByCollectingDeviceTypeId(id);
    }

    //获取所有的厂商
    public List<String> getManufacturer(){
        return deviceCollectingDeviceMapper.getManufacturer();
    }

    //获取所有的系列
    public List<String> getEquipmentType(){
        return deviceCollectingDeviceMapper.getEquipmentType();
    }

    //查看代号是否重复
    public Integer selectByCount(String code){
        return deviceCollectingDeviceMapper.selectByCount(code);
    }

    //网关删除前查询1.5
    public List<String> deleteCollectingQuery(Integer id){
        return mapper.deleteCollectingQuery(id);
    }

}