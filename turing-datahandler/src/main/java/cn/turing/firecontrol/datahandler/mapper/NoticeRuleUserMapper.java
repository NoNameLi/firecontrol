package cn.turing.firecontrol.datahandler.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Tenant
public interface NoticeRuleUserMapper extends CommonMapper<NoticeRuleUser> {
    List<NoticeRuleUser> listByUserIds(@Param("userIds") String userIds,@Param("noticeType") String noticeType);

    List<NoticeRuleUser> queryByNoticeRuleId(@Param("noticeRuleId") Long noticeRuleId,@Param("noticeType") String noticeType);

    NoticeRuleUser queryByNoticeRuleIdAndUserId(@Param("noticeRuleId") Long noticeRuleId,@Param("userId") String userId,@Param("noticeType") String noticeType);
}