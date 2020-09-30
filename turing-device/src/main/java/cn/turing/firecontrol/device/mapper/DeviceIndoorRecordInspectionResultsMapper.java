package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.dto.TypeNumDto;
import cn.turing.firecontrol.device.entity.DeviceIndoorRecordInspectionResults;
import cn.turing.firecontrol.device.vo.DateNumVO;
import cn.turing.firecontrol.device.vo.InspectionResultsVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceIndoorRecordInspectionResultsMapper extends CommonMapper<DeviceIndoorRecordInspectionResults> {

    List<DeviceIndoorRecordInspectionResults> selectByLabelId(Map map);

    public List<Map> selectQuery(Map map);

    public List<DeviceIndoorRecordInspectionResults> selectExcel(Map map);

    List<DeviceIndoorRecordInspectionResults> selectByFacilitiesNo(Map map);

    public List<String> getSelected(@Param(value = "leakFlag") String leakFlag);

    List<DeviceIndoorRecordInspectionResults> selectByTaskId(Map map);

    Integer selectByTaskIdAndlabalId(Map map);
    List<TypeNumDto> selectStatusCount();
    List<DateNumVO> selectResultTrend(@Param(value = "inspectionResult") String inspectionResult,@Param(value = "tenantId") String tenantId);
    List<InspectionResultsVO> selectAbnormalList(@Param(value = "tenantId") String tenantId);
    int checkInspection(@Param(value = "startDate") java.util.Date startDate, @Param(value = "endDate") Date endDate, @Param(value = "labelId") Integer labelId, @Param(value = "routeId") Integer routeId);
}