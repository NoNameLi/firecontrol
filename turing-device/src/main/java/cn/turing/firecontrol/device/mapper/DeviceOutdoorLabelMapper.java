package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceIndoorLabel;
import cn.turing.firecontrol.device.entity.DeviceOutdoorLabel;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceOutdoorLabelMapper extends CommonMapper<DeviceOutdoorLabel> {

    //删除 设施类型判断有多少子站使用此标签
    public List<String> deleteFacilitiesTypeQuery(Integer id);

    List<Map> selectQuery(Map<String, Object> map);

    void updateBatch(String id);

    List<Map> getListById(Map<String, Object> map);

    //根据ids修改是否生成路线标记
    public void updateByIds(@Param(value = "useFlag") String useFlag, @Param(value = "ids") String ids);

    //根据ids修改是否生成路线标记
    public void updateTestByIds(@Param(value = "useTestFlag") String useTestFlag, @Param(value = "ids") String ids);

    Integer getCount(Map<String, Object> map);

    Integer getCountByRoute(Map<String, Object> map);

    //positionSignFlag[-1=全部，0=已标记，1=位标记]
    public List<Map> selectByIdsAndUseFlag(@Param(value = "ids")String ids,@Param(value = "useFlag")String useFlag,@Param(value = "useTestFlag") String useTestFlag,@Param(value = "positionSignFlag") String positionSignFlag);

    List<Map> selectOutdoorLabelList(Map<String, Object> map);

    List<Map> selectOutdoorLabelListByRouteId(@Param(value = "ids")String ids);

    DeviceOutdoorLabel getIndoorLabelByFacilitiesNo(String facilitiesNo);

    DeviceOutdoorLabel getById(Integer id);

    public void updateQrCodePath(DeviceOutdoorLabel entity);

    Integer getNeedInspectionCount(Map<String, Object> map);

    List<Integer> getIdListBytask();

    List<Map> selectOutdoorLabelListById(@Param(value = "ids") String ids);

}