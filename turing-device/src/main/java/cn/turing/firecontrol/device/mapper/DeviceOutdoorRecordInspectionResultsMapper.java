package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceIndoorRecordInspectionResults;
import cn.turing.firecontrol.device.entity.DeviceOutdoorRecordInspectionResults;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceOutdoorRecordInspectionResultsMapper extends CommonMapper<DeviceOutdoorRecordInspectionResults> {

    List<DeviceOutdoorRecordInspectionResults> selectByLabelId(Map map);

    public List<Map> selectQuery(Map map);

    public List<DeviceOutdoorRecordInspectionResults> selectExcel(Map map);

    List<DeviceOutdoorRecordInspectionResults> selectByFacilitiesNo(Map map);

    List<DeviceOutdoorRecordInspectionResults> selectByTaskId(Map map);

    public List<String> getSelected(@Param(value = "leakFlag") String leakFlag);

    Integer selectByTaskIdAndlabalId(Map map);
}