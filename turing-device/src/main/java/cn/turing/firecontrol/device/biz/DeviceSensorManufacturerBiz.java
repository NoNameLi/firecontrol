package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceSensorManufacturer;
import cn.turing.firecontrol.device.mapper.DeviceSensorManufacturerMapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class DeviceSensorManufacturerBiz extends BusinessBiz<DeviceSensorManufacturerMapper,DeviceSensorManufacturer> {

    @Autowired
    private DeviceSensorManufacturerMapper dsmfMapper;
    //分页查看传感器系列与测点
    public TableResultResponse<DeviceSensorManufacturer> selectPageList(Query query, String manufacturer) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceSensorManufacturer entity = new DeviceSensorManufacturer();
        entity.setSensorManufacturer(manufacturer);
        List<DeviceSensorManufacturer> lists = dsmfMapper.selectPageList(entity);
        return new TableResultResponse<DeviceSensorManufacturer>(result.getTotal(),lists);
    }


    //判断传感器厂商是否重复
    public Integer selectByCount(String manufacturer){
        DeviceSensorManufacturer entity = new DeviceSensorManufacturer();
        entity.setSensorManufacturer(manufacturer);
        return dsmfMapper.selectByCount(entity);
    }
    //传感器厂商的下拉框，选择传感器厂商
    public List<String> selectedType(){
        return dsmfMapper.selectedType();
    }

    //传感器厂商的下拉框，选择传感器厂商
    public List<DeviceSensorManufacturer> selectedTypeId(){
        return mapper.selectedTypeId();
    }

    public DeviceSensorManufacturer selecteByName(String para){
        return mapper.selecteByName(para);
    }
}