package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceMessageNotice;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Tenant
@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceMessageNoticeMapper extends CommonMapper<DeviceMessageNotice> {

    //根据报警类型查询出已经添加的通知方式
    public List<Integer> selectByNoticeType(@Param(value = "noticeType") String noticeType, @Param(value = "tenantId") String tenantId);
}