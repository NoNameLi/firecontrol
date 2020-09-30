package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import cn.turing.firecontrol.device.mapper.DeviceOutdoorLabelMapper;
import cn.turing.firecontrol.device.util.SplitUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceOutdoorLabelBiz extends BusinessBiz<DeviceOutdoorLabelMapper,DeviceOutdoorLabel> {

    @Autowired
    private DeviceRouteLabelBiz drlBiz;
    @Autowired
    private DeviceOutdoorRecordInspectionResultsBiz dorirBiz;

    //删除 设施类型判断有多少子站使用此标签
    public List<String> deleteFacilitiesTypeQuery(Integer id){
        return mapper.deleteFacilitiesTypeQuery(id);
    }

    public TableResultResponse<DeviceOutdoorLabel> selectQuery(Query query, DeviceOutdoorLabel deviceOutdoorLabel, String equipmentType, String dateStrs) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        if(StringUtils.isBlank(dateStrs)){
            map.put("startDate",null);
            map.put("endDate",null);
        }else {
            map.put("startDate", dateStrs+" 00:00:00");
            map.put("endDate", dateStrs+" 23:59:59");
        }
        map.put("deviceOutdoorLabel",deviceOutdoorLabel);
        map.put("equipmentType",equipmentType);
        List<Map> list = mapper.selectQuery(map);
        for(Map map1:list){
            String positionSign = (String) map1.get("positionSign");
            if(positionSign==null||"".equals(positionSign)){
                map1.put("positionSign","未标记");
            }else {
                map1.put("positionSign","已标记");
            }
            if("0".equals(map1.get("status"))){
                map1.put("state","正常");
            }
            if("1".equals(map1.get("status"))){
                map1.put("state","维修中");
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public void updateBatch(String id) {
        mapper.updateBatch(id);
    }

    public List<Map> getListById(List idlist) {
        Map<String ,Object> map = new HashMap<>();
        map.put("ids",idlist);
        return mapper.getListById(map);
    }

    //根据ids修改是否生成路线标记
    public void updateByIds( String useFlag, String ids){
        mapper.updateByIds(useFlag,ids);
    }

    //根据ids修改是否生成路线标记
    public void updateTestByIds( String useTestFlag, String ids){
        mapper.updateTestByIds(useTestFlag,ids);
    }

    public Integer getCount(String status,String useFlag,String resultFlag) {
        Map<String ,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("useFlag",useFlag);
        map.put("resultFlag",resultFlag);
        return mapper.getCount(map);
    }

    public Integer getCountByRoute(String status) {
        Map<String ,Object> map = new HashMap<>();
        map.put("status",status);
        return mapper.getCountByRoute(map);
    }

    //positionSignFlag[-1=全部，0=已标记，1=位标记]
    public List<Map> selectByIdsAndUseFlag(String ids,String useFlag, String useTestFlag,String positionSignFlag){
        return mapper.selectByIdsAndUseFlag(ids,useFlag,useTestFlag,positionSignFlag);
    }

    public TableResultResponse selectOutdoorLabelList(Query query, String status) {
//        List<Integer> labelIdList = mapper.getIdListBytask();
//        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
//        Map<String ,Object> map = new HashMap<>();
//        if("2".equals(status)){
//            map.put("status",null);
//            map.put("useFlag","1");
//        }else{
//            map.put("status",status);
//            map.put("useFlag",null);
//        }
//        List<Map> list = mapper.selectOutdoorLabelList(map);
//        for(Map map1:list){
//            if(labelIdList.contains(map1.get("id"))){
//                map1.put("flag","0");
//            }else {
//                map1.put("flag","1");
//            }
//        }
        List<Integer> labelIdList = mapper.getIdListBytask();
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        if(!"2".equals(status)){
            Map<String ,Object> map = new HashMap<>();
            map.put("status",status);
            map.put("useFlag",null);
            List<Map> list = mapper.selectOutdoorLabelList(map);
            for(Map map1:list){
                if(labelIdList.contains(map1.get("id")) && "0".equals(map1.get("resultFlag"))){
                    map1.put("flag","0");
                }else {
                    map1.put("flag","1");
                }
            }
            return new TableResultResponse(result.getTotal(),list);
        }else{
            List<Map> list =mapper.selectOutdoorLabelListById(SplitUtil.merge(labelIdList));
            for(Map map1:list){
                map1.put("flag","0");//需巡检
            }
            return new TableResultResponse(result.getTotal(),list);
        }
    }

    public TableResultResponse selectOutdoorLabelListByRouteId(Query query, Integer routeId,Integer taskId) {
        List<Integer> idList = drlBiz.selectByRouteId(routeId,"0");
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = mapper.selectOutdoorLabelListByRouteId(SplitUtil.merge(idList));
        for(Map map:list){
//            if("0".equals(map.get("resultFlag"))){
//                if("1".equals(map.get("status"))){
//                    map.put("type","维修中");
//                }
//            }else{
//                List<DeviceOutdoorRecordInspectionResults> resultsList = dorirBiz.selectByFacilitiesNo(map.get("facilitiesNo").toString());
//                for(DeviceOutdoorRecordInspectionResults inspectionResults:resultsList){
//                    map.put("resultsId",inspectionResults.getId());
//                }
//                if("1".equals(map.get("resultFlag"))){
//                    map.put("type","正常");
//                }
//                if("2".equals(map.get("resultFlag"))){
//                    map.put("type","异常");
//                }
//                if("3".equals(map.get("resultFlag"))){
//                    map.put("type","跳过");
//                }
//            }
            List<DeviceOutdoorRecordInspectionResults> inspectionResults = dorirBiz.selectByTaskId(taskId,Integer.valueOf(map.get("id").toString()));
            if(inspectionResults.size()==0){
                map.put("resultFlag","0");
            }else{
                for(int i=0;i<1;i++){
                    if("0".equals(inspectionResults.get(0).getLeakFlag())){
                        if("0".equals(inspectionResults.get(0).getInspectionResult())){
                            map.put("resultFlag","1");
                        }
                        if("1".equals(inspectionResults.get(0).getInspectionResult())){
                            map.put("resultFlag","2");
                        }
                    }else{
                        map.put("resultFlag","3");
                    }
                    map.put("resultsId",inspectionResults.get(0).getId());
                }
            }
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public DeviceOutdoorLabel getIndoorLabelByFacilitiesNo(String facilitiesNo) {
        return mapper.getIndoorLabelByFacilitiesNo(facilitiesNo);
    }

    public DeviceOutdoorLabel getById(Integer id) {
        return mapper.getById(id);
    }

    public void updateQrCodePath(DeviceOutdoorLabel entity){
        mapper.updateQrCodePath(entity);
    }

    public Integer getNeedInspectionCount(String status,String useFlag,String resultFlag) {
        Map<String ,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("useFlag",useFlag);
        map.put("resultFlag",resultFlag);
        return mapper.getNeedInspectionCount(map);
    }


}