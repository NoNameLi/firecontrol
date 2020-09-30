package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceCollectingManufacturer;
import cn.turing.firecontrol.device.entity.DeviceSensorManufacturer;
import cn.turing.firecontrol.device.mapper.DeviceCollectingManufacturerMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Service
public class DeviceCollectingManufacturerBiz extends BusinessBiz<DeviceCollectingManufacturerMapper,DeviceCollectingManufacturer> {

    @Autowired
    private DeviceCollectingManufacturerMapper dcmfMapper;
    //分页查询 搜索网关厂商
    public TableResultResponse<DeviceCollectingManufacturer> selectPageList(Query query, String manufacturer){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceCollectingManufacturer entity = new DeviceCollectingManufacturer();
        entity.setCollectingManufacturer(manufacturer);
        List<DeviceCollectingManufacturer> lists = dcmfMapper.selectPageList(entity);
        return new TableResultResponse<DeviceCollectingManufacturer>(result.getTotal(),lists);
    }

    //判断网关厂商是否重复
    public Integer selectByCount(String manufacturer){
        DeviceCollectingManufacturer entity = new DeviceCollectingManufacturer();
        entity.setCollectingManufacturer(manufacturer);
        return dcmfMapper.selectByCount(entity);
    }
    //传感器厂商的下拉框，选择传感器厂商
    public List<String> selectedType(){
        return dcmfMapper.selectedType();
    }

    //根据厂商名称，查询厂商id
    public Integer selectByName(String manufacturerName){
        return mapper.selectByName(manufacturerName);
    }

}