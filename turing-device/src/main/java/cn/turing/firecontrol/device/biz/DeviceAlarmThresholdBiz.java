package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.device.mapper.DeviceMeasuringPointMapper;
import cn.turing.firecontrol.device.util.SplitUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceAlarmThreshold;
import cn.turing.firecontrol.device.mapper.DeviceAlarmThresholdMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.ArrayList;
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
public class DeviceAlarmThresholdBiz extends BusinessBiz<DeviceAlarmThresholdMapper,DeviceAlarmThreshold> {

    @Autowired
    private DeviceAlarmThresholdMapper datMapper;
    @Autowired
    private DeviceMeasuringPointMapper dmpMapper;
    //查出所有得测点Id
    public List<Integer> getMeasuringPointIds(){
        return datMapper.getMeasuringPointIds();
    }

    //分页查询搜索测点
    public TableResultResponse<Map<String,Object>> selectPageList(Query query, String measuringPoint){
        //获取当前用户添加的所有的测点报警对应的测点id
        List<Integer> list  = this.getMeasuringPointIds();
        List<Object> maps = new ArrayList<>();
        String ids = SplitUtil.merge(list);
        //当用户没有添加测点报警时直接返回
        if(ids==null||"".equals(ids)){
            return new TableResultResponse(0,maps);
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        //根据测点id查询出测点信息
        List<Map> lists = dmpMapper.selectByIdsOrNamePageList(ids,measuringPoint);
        for(int i=0;i<lists.size();i++){
            Map<String,Object> map=lists.get(i);
            List<Map> deviceAlarmThresholds = this.selectByMeasuringPointId( (Integer) lists.get(i).get("id"));
            map.put("deviceAlarmThresholds",deviceAlarmThresholds);

            maps.add(map);
        }
        return new TableResultResponse(result.getTotal(),maps);
    }

    //根据测点id查出所有的
    public List<Map> selectByMeasuringPointId(int measuringPointIds){
        return datMapper.selectByMeasuringPointId(measuringPointIds);
    }

    //根据ids批量删除
    public void deleteByIds(String ids){
        datMapper.deleteByIds(ids);
    }

    //根据报警等级id删除
    public void deleteByAlId(Integer alId){
        datMapper.deleteByAlId(alId);
    }

    public List<DeviceAlarmThreshold> selectByMpid(Integer mpid) {
        return datMapper.selectByMpid(mpid);
    }

    public DeviceAlarmThreshold selectByAlrmData(Integer id, Double alrmData,String tenantId) {
        Map map = new HashMap();
        map.put("id",id);
        map.put("alrmData",alrmData);
        map.put("tenantId",tenantId);
        return datMapper.selectByAlrmData(map);
    }

    //实时监测  获取等级
    public DeviceAlarmThreshold selectByAlrmLevel(Integer id, Double alrmData){
        Map map = new HashMap();
        map.put("id",id);
        map.put("alrmData",alrmData);
        return datMapper.selectByAlrmLevel(map);
    }


}