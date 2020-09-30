package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.core.context.BaseContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.turing.firecontrol.device.entity.DeviceAlDnRelation;
import cn.turing.firecontrol.device.mapper.DeviceAlDnRelationMapper;
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
public class DeviceAlDnRelationBiz extends BusinessBiz<DeviceAlDnRelationMapper,DeviceAlDnRelation> {

   @Autowired
   private DeviceAlDnRelationMapper dadrMapper;

    //根据报警等级id查询出通知方式的id
    public List<Integer> selectByAlarmLevelId(Integer alarmLevelId,String tenantId){
        return dadrMapper.selectByAlarmLevelId(alarmLevelId,tenantId);
    }


    //批量增加删除传报警等级与通知方式关联表
    public boolean updateBatch(int alarmLevelId,List<Integer> add,List<Integer> delete){
        boolean flag = true;
        if(add!=null&&add.size()>0){
            for(int i=0;i<add.size();i++){
                DeviceAlDnRelation entity = new DeviceAlDnRelation();
                entity.setAlId(alarmLevelId);
                entity.setDnId(add.get(i));
                this.insertSelective(entity);
            }
        }
        if(delete!=null&&delete.size()>0){
            for(int i=0;i<delete.size();i++){
                DeviceAlDnRelation entity = new DeviceAlDnRelation();
                entity.setAlId(alarmLevelId);
                entity.setDnId(delete.get(i));
                entity.setTenantId(BaseContextHandler.getTenantID());
                dadrMapper.delete(entity);
            }
        }
        return flag;
    }



}