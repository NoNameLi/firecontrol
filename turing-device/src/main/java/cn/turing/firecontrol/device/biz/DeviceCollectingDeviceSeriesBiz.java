package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceCollectingDeviceSeries;
import cn.turing.firecontrol.device.mapper.DeviceCollectingDeviceSeriesMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.List;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Service
public class DeviceCollectingDeviceSeriesBiz extends BusinessBiz<DeviceCollectingDeviceSeriesMapper,DeviceCollectingDeviceSeries> {

    @Autowired
    private DeviceCollectingDeviceSeriesMapper dcdsMapper;
    //分页查询搜索测点
    public TableResultResponse<DeviceCollectingDeviceSeries> selectPageList(Query query, String type){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        DeviceCollectingDeviceSeries entity = new DeviceCollectingDeviceSeries();
        entity.setType(type);
        List<DeviceCollectingDeviceSeries> lists = dcdsMapper.selectPageList(entity);
        return new TableResultResponse<>(result.getTotal(),lists);
    }

    //根据查询字段计数
    public int selectByCount(String type) {
        DeviceCollectingDeviceSeries entity=new DeviceCollectingDeviceSeries();
        entity.setType(type);
        int count =  dcdsMapper.selectByCount(entity);
        return count;
    }

    //采集设备的下拉框
    public List<String> selectedType(){
        return dcdsMapper.selectedType();
    }
}