package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceVideoAnalysisSolution;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface DeviceVideoAnalysisSolutionMapper extends CommonMapper<DeviceVideoAnalysisSolution> {

    /**
     * 查询各分析方案的设备使用数，两个KEY：id、solutionName,solutionImage, solutionCount
     *
     * @param tenantId
     * @return
     */
    List<Map<String,Object>> querySolutionsAndDeviceCount(String tenantId);






}