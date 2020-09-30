package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.device.util.SplitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceSensorMpRelation;
import cn.turing.firecontrol.device.mapper.DeviceSensorMpRelationMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.List;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceSensorMpRelationBiz extends BusinessBiz<DeviceSensorMpRelationMapper,DeviceSensorMpRelation> {

    @Autowired
    private DeviceSensorMpRelationMapper dsmrMapper;

    //批量插入传感器与测点关联表
    public boolean insertBatch(int sensorSeriesId,String mpIds){
        boolean flag = true;
        Integer [] ins = SplitUtil.splitInt(mpIds);
        if(ins!=null){
            for(int i=0;i<ins.length;i++){
                DeviceSensorMpRelation entity = new DeviceSensorMpRelation();
                entity.setSensorSeriesId(sensorSeriesId);
                entity.setMpId(ins[i]);
                this.insertSelective(entity);
            }
        }else {
            flag = false;
        }
        return flag;
    }
    //根据传感器系列id获取关联的测点id
    public List<Integer> selectBySensorSeriesId(int sensorSeriesId){
        return  dsmrMapper.selectBySensorSeriesId(sensorSeriesId);
    }

    //批量增加删除传感器关联表
    public boolean updateBatch(int sensorSeriesId,List<Integer> add,List<Integer> delete){
        boolean flag = true;
        if(add!=null&&add.size()>0){
            for(int i=0;i<add.size();i++){
                DeviceSensorMpRelation entity = new DeviceSensorMpRelation();
                entity.setSensorSeriesId(sensorSeriesId);
                entity.setMpId(add.get(i));
                this.insertSelective(entity);
            }
        }
        if(delete!=null&&delete.size()>0){
            for(int i=0;i<delete.size();i++){
                DeviceSensorMpRelation entity = new DeviceSensorMpRelation();
//                entity.setTenantId(BaseContextHandler.getTenantID());
                entity.setSensorSeriesId(sensorSeriesId);
                entity.setMpId(delete.get(i));
                dsmrMapper.delete(entity);
            }
        }
        return flag;
    }

    //根据测点id删除时，删除相应得关联表
    public void deleteByMPIds(Integer mpId){
        dsmrMapper.deleteByMPIds(mpId);
    }

    //根据传感器系列id删除时，删除相应得关联表
    public void deleteBySSIds(Integer sensorSeriesId){
        dsmrMapper.deleteBySSIds(sensorSeriesId);
    }

}