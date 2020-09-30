package cn.turing.firecontrol.datahandler.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.datahandler.entity.NoticeLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
@Tenant
public interface NoticeLogMapper extends CommonMapper<NoticeLog> {

    List<NoticeLog> listNoticeLog(@Param("mobilePhone") String mobilePhone, @Param("noticeType") String noticeType);

    NoticeLog queryLastLog(@Param("sensorId") Long sensorId,@Param("channelId") Integer channelId,@Param("userId") String userId);
}