package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.dto.TypeNumDto;
import cn.turing.firecontrol.device.entity.DeviceIndoorRecordInspectionResults;
import cn.turing.firecontrol.device.entity.DeviceInspectionTasks;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.mapper.DeviceIndoorRecordInspectionResultsMapper;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import cn.turing.firecontrol.device.util.DateUtil;
import cn.turing.firecontrol.device.vo.DateNumVO;
import cn.turing.firecontrol.device.vo.InspectionResultsVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceIndoorRecordInspectionResultsBiz extends BusinessBiz<DeviceIndoorRecordInspectionResultsMapper,DeviceIndoorRecordInspectionResults> {


    public TableResultResponse<DeviceIndoorRecordInspectionResults> selectByLabelId(Query query , Integer labelId, String datestr) {
        String year = null;
        String month = null;
        if(StringUtils.isBlank(datestr)){
            year = DateUtil.getNowYear().toString();
            month = DateUtil.getMonth().toString();
        }else {
            year = datestr.substring(0,4);
            month = datestr.substring(5,7);
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map map = new HashMap();
        map.put("labelId",labelId);
        map.put("year",year);
        map.put("month",month);
        List<DeviceIndoorRecordInspectionResults> list = mapper.selectByLabelId(map);
        List resultList = new ArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        for(DeviceIndoorRecordInspectionResults results:list){
            Map map1 = new HashMap();
            map1.put("id",results.getId());
            map1.put("date",simpleDateFormat.format(results.getInspectionDate()));
            //0=正常/1=异常/2=漏检
            if("1".equals(results.getLeakFlag())){
                map1.put("status","2");
                if(StringUtils.isBlank(results.getInspectionPerson())){
                    map1.put("person","无人接取");
                }else{
                    map1.put("person",results.getInspectionPerson());
                }
            }else{
                map1.put("person",results.getInspectionPerson());
                map1.put("status",results.getInspectionResult());
                if("1".equals(results.getInspectionResult())){
                    map1.put("handling",results.getHandling());
                }
            }
            resultList.add(map1);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    public TableResultResponse<DeviceIndoorRecordInspectionResults> selectQuery(Query query , DeviceIndoorRecordInspectionResults entity,String inspectionStartDate,String inspectionEndDate,String plannedCompletionStartTime,String plannedCompletionEndTime) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map map = new HashMap();
        map.put("entity",entity);
        map.put("inspectionStartDate",inspectionStartDate);
        map.put("inspectionEndDate",inspectionEndDate);
        map.put("plannedCompletionStartTime",plannedCompletionStartTime);
        map.put("plannedCompletionEndTime",plannedCompletionEndTime);
        List<Map> lists = mapper.selectQuery(map);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for(int i=0;i<lists.size();i++){
            Map mapTemp = lists.get(i);
            String inspectionResult = (String)mapTemp.get("inspectionResult");
            if("0".equalsIgnoreCase(inspectionResult)){
                lists.get(i).put("inspectionResult","正常");
                lists.get(i).put("color","#666666");
            }
            if("1".equalsIgnoreCase(inspectionResult)){
                lists.get(i).put("inspectionResult","异常");
                lists.get(i).put("color","#F5A623");
            }
            if(mapTemp.get("inspectionDate")!=null){
                lists.get(i).put("inspectionDate",dateFormat.format((Date) mapTemp.get("inspectionDate")));
            }
            if(mapTemp.get("plannedCompletionTime")!=null){
                lists.get(i).put("plannedCompletionTime",dateFormat.format((Date) mapTemp.get("plannedCompletionTime")));
            }
        }
        return new TableResultResponse(result.getTotal(),lists);
    }

    public List<DeviceIndoorRecordInspectionResults> export( DeviceIndoorRecordInspectionResults entity,String inspectionStartDate,String inspectionEndDate,String plannedCompletionStartTime,String plannedCompletionEndTime) {
        Map map = new HashMap();
        map.put("entity",entity);
        map.put("inspectionStartDate",inspectionStartDate);
        map.put("inspectionEndDate",inspectionEndDate);
        map.put("plannedCompletionStartTime",plannedCompletionStartTime);
        map.put("plannedCompletionEndTime",plannedCompletionEndTime);
        List<DeviceIndoorRecordInspectionResults> lists = mapper.selectExcel(map);
        return lists;
    }

    public List<DeviceIndoorRecordInspectionResults> selectByFacilitiesNo(String facilitiesNo) {
        Map map = new HashMap();
        map.put("facilitiesNo",facilitiesNo);
        return mapper.selectByFacilitiesNo(map);
    }

    public void insert(DeviceIndoorRecordInspectionResults indoorRecordInspectionResults) {
        mapper.insert(indoorRecordInspectionResults);
    }

    public List<DeviceIndoorRecordInspectionResults> selectByTaskId(Integer taskId, Integer labelId) {
        Map map = new HashMap();
        map.put("taskId",taskId);
        map.put("labelId",labelId);
        return mapper.selectByTaskId(map);
    }

    public List<String> getSelected( String leakFlag){
       return mapper.getSelected(leakFlag);
    }

    public Integer selectByTaskIdAndlabalId(Integer taskId, String ids, String leakFlag, String result) {
        Map map = new HashMap();
        map.put("taskId",taskId);
        map.put("ids",ids);
        map.put("leakFlag",leakFlag);
        map.put("result",result);
        return mapper.selectByTaskIdAndlabalId(map);
    }
    public List<TypeNumDto> selectResultCount(){
        List<TypeNumDto> rs =new ArrayList<>();
        List<TypeNumDto> dtos = mapper.selectStatusCount();
        double sum = dtos.stream().mapToDouble(TypeNumDto::getNum).sum();
        if(sum>0){
            double temp=0l;
            for (TypeNumDto dto:dtos) {
                if(dto.getType().equals("0")){
                    temp= new BigDecimal(1.0 * dto.getNum()/sum).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
                }
            }
            rs.add(new TypeNumDto("正常",temp));
            rs.add(new TypeNumDto("异常",1.0-temp));
        }else {
            rs.add(new TypeNumDto("正常",0.0));
            rs.add(new TypeNumDto("异常",0.0));
        }

        return rs;
    }
    public List<DateNumVO> selectResultTrend(){
        //近两周巡检正常数据
        List<DateNumVO> list = mapper.selectResultTrend("0", BaseContextHandler.getTenantID());
        //近两周巡检异常数据
        List<DateNumVO> abnormalList = mapper.selectResultTrend("1",BaseContextHandler.getTenantID());
        for (DateNumVO vo:list) {
            for (DateNumVO d:abnormalList) {
                if(vo.getDateValue().equals(d.getDateValue())){
                    vo.setNum1(d.getNum());
                    break;
                }
            }
        }
        return list;
    }
    public List<InspectionResultsVO> selectAbnormalList(){
        List<InspectionResultsVO> inspectionResultsVOS = mapper.selectAbnormalList(BaseContextHandler.getTenantID());
        if(CollectionUtils.isEmpty(inspectionResultsVOS)){
            List<InspectionResultsVO> rs =new ArrayList<>();
            InspectionResultsVO temp=  new InspectionResultsVO();
            temp.setEquipmentType("");
            temp.setInspectionDate("");
            temp.setInspectionPerson("");
            temp.setPositionDescription("");
            temp.setProblemDescription("");
            rs.add(temp);
            return rs;
        }
        return inspectionResultsVOS;
    }
   public boolean checkInspection(Date startDate,Date endDate, Integer labelId,  Integer routeId){
        if( mapper.checkInspection(startDate,endDate,routeId,labelId)>0){
            return true;
        }
        return false;
   }
}