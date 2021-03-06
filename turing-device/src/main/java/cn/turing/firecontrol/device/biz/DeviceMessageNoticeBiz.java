package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.entity.DeviceMessageNotice;
import cn.turing.firecontrol.device.mapper.DeviceMessageNoticeMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceMessageNoticeBiz extends BusinessBiz<DeviceMessageNoticeMapper,DeviceMessageNotice> {

    //根据报警类型查询出已经添加的通知方式
    public List<Integer> selectByNoticeType(String noticeType,String tenantId){
        return mapper.selectByNoticeType(noticeType,tenantId);
    }

    //批量增加删除报警通知
    public void updateBatch(String noticeType,List<Integer> add,List<Integer> delete){
        DeviceMessageNotice entity = null;
        if(add!=null&&add.size()>0){
            for(int i=0;i<add.size();i++){
                entity = new DeviceMessageNotice();
                entity.setNoticeType(noticeType);
                entity.setNoticeId(add.get(i));
                this.insertSelective(entity);
            }
        }
        if(delete!=null&&delete.size()>0){
            for(int i=0;i<delete.size();i++){
                entity = new DeviceMessageNotice();
                entity.setNoticeType(noticeType);
                entity.setNoticeId(delete.get(i));
                entity.setTenantId(BaseContextHandler.getTenantID());
                mapper.delete(entity);
            }
        }
    }
}