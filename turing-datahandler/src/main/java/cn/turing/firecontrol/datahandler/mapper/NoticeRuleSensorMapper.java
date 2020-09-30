package cn.turing.firecontrol.datahandler.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleSensor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
@Tenant
public interface NoticeRuleSensorMapper extends CommonMapper<NoticeRuleSensor> {
    void deleteByIds(@Param("noticeRuleId") Long noticeRuleId,@Param("sensorIds") String sensorIds);
    NoticeRuleSensor queryBySensorIdAndChannelId(@Param("sensorId") Long sensorId,@Param("channelId") Integer channelId);
    int deleteByNoticeRuleId(@Param("noticeRuleId") Long noticeRuleId);

}