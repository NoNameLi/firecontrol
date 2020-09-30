package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceIndoorLabel;
import cn.turing.firecontrol.device.vo.LabelnspectionVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceIndoorLabelMapper extends CommonMapper<DeviceIndoorLabel> {

    List<Map> selectQuery(Map<String, Object> map);

    void updateBatch(String id);

    List<Map> getListById(Map<String, Object> map);

    //删除 设施类型判断有多少子站使用此标签
    public List<String> deleteFacilitiesTypeQuery(Integer id);

    //根据ids修改是否生成路线标记
    public void updateByIds(@Param(value = "useFlag") String useFlag,@Param(value = "ids") String ids);

    //根据ids修改是否生成路线标记
    public void updateTestByIds(@Param(value = "useTestFlag") String useTestFlag,@Param(value = "ids") String ids);

    Integer getCount(Map<String, Object> map);

    Integer getCountByRoute(Map<String, Object> map);

    public List<Integer> getBuildingId(@Param(value = "ids") String ids,@Param(value = "useFlag") String useFlag,@Param(value = "useTestFlag") String useTestFlag,@Param(value = "routeFlag") String routeFlag,@Param(value = "labelFlag") String labelFlag);
    List<Map> selectIndoorLabelList(Map<String, Object> map);

    public List<Integer> getBuildingId(@Param(value = "ids") String ids);

    public List<Map> selectByBuildingId(@Param(value = "buildingId") Integer buildingId,@Param(value = "ids") String ids,@Param(value = "useFlag") String useFlag,@Param(value = "useTestFlag") String useTestFlag);

    public List<Integer> selectByBuildingIdResultId(@Param(value = "buildingId") Integer buildingId,@Param(value = "ids") String ids,@Param(value = "useFlag") String useFlag,@Param(value = "useTestFlag") String useTestFlag);

    public List<Map> selectIndoorLabelListByBuildingId(@Param(value = "buildingId") Integer buildingId,@Param(value = "ids") String ids);

    DeviceIndoorLabel getIndoorLabelByFacilitiesNo(String facilitiesNo);

    DeviceIndoorLabel getById(Integer id);

    Integer getNeedInspectionCount(Map<String, Object> map);

    public void updateQrCodePath(DeviceIndoorLabel entity);

    List<Integer> getIdListBytask();

    List<Map> selectIndoorLabelListById(@Param(value = "ids")String ids, @Param(value = "zxqy") String zxqy);
    LabelnspectionVO getInspectionInfoById(Integer id);

    List<Map<String,Object>> selectAllStatus();
}