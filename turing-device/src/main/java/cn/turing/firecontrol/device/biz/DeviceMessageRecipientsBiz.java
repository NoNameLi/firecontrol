package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.entity.DeviceMessageNotice;
import cn.turing.firecontrol.device.entity.DeviceMessageRecipients;
import cn.turing.firecontrol.device.mapper.DeviceMessageRecipientsMapper;
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
public class DeviceMessageRecipientsBiz extends BusinessBiz<DeviceMessageRecipientsMapper,DeviceMessageRecipients> {

    public List<DeviceMessageRecipients> selectByNotictType(String noticeType,String tenantId){
        return mapper.selectByNotictType(noticeType,tenantId);
    }
    public List<String> findByNotictType(String noticeType){
        return mapper.findByNotictType(noticeType);
    }

    //批量增加删除通知人
    public void updateBatch(String noticeType,List<String> add,List<String> delete){
        DeviceMessageRecipients entity = null;
        if(add!=null&&add.size()>0){
            for(int i=0;i<add.size();i++){
                entity = new DeviceMessageRecipients();
                entity.setNoticeType(noticeType);
                entity.setMessageRecipientsUserid(add.get(i));
                this.insertSelective(entity);
            }
        }
        if(delete!=null&&delete.size()>0){
            for(int i=0;i<delete.size();i++){
                entity = new DeviceMessageRecipients();
                entity.setNoticeType(noticeType);
                entity.setMessageRecipientsUserid(delete.get(i));
                entity.setTenantId(BaseContextHandler.getTenantID());
                mapper.delete(entity);
            }
        }
    }
}