package cn.turing.firecontrol.datahandler.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.datahandler.entity.NoticeRule;
import cn.turing.firecontrol.datahandler.vo.NoticeRuleVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Tenant
public interface NoticeRuleMapper extends CommonMapper<NoticeRule> {
    List<NoticeRuleVo> listNoticeRuleForUser(@Param("userId") String userId, @Param("channelId") Integer channelId,
                                             @Param("ruleName") String ruleName,@Param("noticeType") String noticeType);
    NoticeRule queryById(@Param("id") Long id);
    List<NoticeRule> listNoticeRule(@Param("channelId") Integer channelId);

}