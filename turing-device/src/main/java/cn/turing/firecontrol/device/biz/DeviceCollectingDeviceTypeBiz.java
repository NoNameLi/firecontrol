package cn.turing.firecontrol.device.biz;

import org.springframework.beans.factory.annotation.Autowired;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceCollectingDeviceType;
import cn.turing.firecontrol.device.mapper.DeviceCollectingDeviceTypeMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.List;

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
public class DeviceCollectingDeviceTypeBiz extends BusinessBiz<DeviceCollectingDeviceTypeMapper,DeviceCollectingDeviceType> {

    @Autowired
    private DeviceCollectingDeviceTypeMapper dcdtMapper;
    //根据传采集系列设备的的id查询出有多少站点使用了改采集设备，返回子站点的id
    public List<String> selectByCollectingDeviceSeriesIdResultTenantId(Integer collectingDeviceSeriesId){
        return dcdtMapper.selectByCollectingDeviceSeriesIdResultTenantId(collectingDeviceSeriesId);
    }

    //根据厂商绑定的网关，查询多少人使用了改厂商的网关，返回子站点的id-
    public List<String> selectByCollectingManufacturerIdResultTenantID(Integer manufacturerId){
        return dcdtMapper.selectByCollectingManufacturerIdResultTenantID(manufacturerId);
    }

    @Autowired
    private  DeviceCollectingDeviceTypeMapper deviceCollectingDeviceTypeMapper;

    public TableResultResponse<Map<String, Object>> selectPageList(Query query, String manufacturer, String equipmentType, String model) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceCollectingDeviceType entity = new DeviceCollectingDeviceType();
        entity.setManufacturer(manufacturer);
        entity.setEquipmentType(equipmentType);
        entity.setModel(model);
        List<Map> list = deviceCollectingDeviceTypeMapper.selectPageList(entity);
/*        for(int i=0;i<list.size();i++){
            Map map=list.get(i);
            String unit = "";
            if("0".equals(map.get("maintenanceCycleUnit"))){
                unit = "小时";
            }else if("1".equals(map.get("maintenanceCycleUnit"))){
                unit = "天";
            }else if("2".equals(map.get("maintenanceCycleUnit"))){
                unit = "年";
            }
            //维保周期
            map.put("maintenanceCycle",map.get("maintenanceCycleValue")+unit);
        }*/
        return new TableResultResponse(result.getTotal(),list);
    }


    public List<DeviceCollectingDeviceType> selectByType(String manufacturer, String equipmentType, String model) {
        //把参数手动封装在Map中
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("manufacturer", manufacturer);
        map.put("equipmentType", equipmentType);
        map.put("model", model);
        return deviceCollectingDeviceTypeMapper.selectByType(map);
    }

    //获取所有的厂商
    public List<String> getManufacturer(){
       return dcdtMapper.getManufacturer();
    }

    //获取所有的系列
    public List<String> getEquipmentType(){
        return dcdtMapper.getEquipmentType();
    }

    //获取根据厂商获取所有的类型
    public List<String> getEquipmentTypeByMF(String manufacturer){
        return dcdtMapper.getEquipmentTypeByMF(manufacturer);
    }

    //根据厂商，类型获取所有的型号
    public List<String> getModel(String manufacturer, String equipmentType){
        //把参数手动封装在Map中
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("manufacturer", manufacturer);
        map.put("equipmentType", equipmentType);
        return  dcdtMapper.getModel(map);
    }

    public void updateCollecting(DeviceCollectingDeviceType entity){
        mapper.updateCollecting(entity);
    }

}