package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.mapper.DeviceIndoorLabelMapper;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.vo.LabelnspectionVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.List;
import java.util.logging.SimpleFormatter;
import java.util.stream.IntStream;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class DeviceIndoorLabelBiz extends BusinessBiz<DeviceIndoorLabelMapper,DeviceIndoorLabel> {

    @Autowired
    private DeviceRouteLabelBiz drlBiz;
    @Autowired
    private DeviceIndoorRecordInspectionResultsBiz dirirBiz;
    @Autowired
    private  DeviceInspectionSchemeBiz schemeBiz;
    @Autowired
    private DeviceInspectionTimeBiz inspectionTimeBiz;
    @Autowired
    private DeviceIndoorRecordInspectionResultsBiz indoorRecordInspectionResultsBiz;

    //删除 设施类型判断有多少子站使用此标签
    public List<String> deleteFacilitiesTypeQuery(Integer id){
        return mapper.deleteFacilitiesTypeQuery(id);
    }
    public TableResultResponse<DeviceIndoorLabel> selectQuery(Query query, String ids, DeviceIndoorLabel deviceIndoorLabel,String equipmentType,String dateStrs) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        //当ids为空或者-1是查询全部
        if(!("-1".equals(ids)||ids == null)){
            String[] idstr =ids.split(",");
            List idlist = Arrays.asList(idstr);
            map.put("ids",idlist);
        }
        if(StringUtils.isBlank(dateStrs)){
            map.put("startDate",null);
            map.put("endDate",null);
        }else {
            map.put("startDate", dateStrs+" 00:00:00");
            map.put("endDate", dateStrs+" 23:59:59");
        }
        map.put("deviceIndoorLabel",deviceIndoorLabel);
        map.put("equipmentType",equipmentType);
        List<Map> list = mapper.selectQuery(map);
        for(Map map1:list){
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

    public List<Integer> getBuildingId(String ids,String useFlag,String useTestFlag,String routeFlag,String labelFlag){
        return mapper.getBuildingId(ids,useFlag,useTestFlag,routeFlag,labelFlag);
    }


    public TableResultResponse selectIndoorLabelList(Query query, String status, String zxqy) {
//        List<Integer> labelIdList = mapper.getIdListBytask();
//        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
//        Map<String ,Object> map = new HashMap<>();
//        map.put("zxqy",zxqy);
//        if("2".equals(status)){
//            map.put("status",null);
//            map.put("useFlag","1");
//        }else{
//            map.put("status",status);
//            map.put("useFlag",null);
//        }
//        List<Map> list = mapper.selectIndoorLabelList(map);
//        for(Map map1:list){
//            if(labelIdList.contains(map1.get("id"))){
//                map1.put("flag","0");//需巡检
//            }else {
//                map1.put("flag","1");
//            }
//        }
        List<Integer> labelIdList = mapper.getIdListBytask();
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        if(!"2".equals(status)){
            Map<String ,Object> map = new HashMap<>();
            map.put("zxqy",zxqy);
            map.put("status",status);
            map.put("useFlag",null);
            List<Map> list = mapper.selectIndoorLabelList(map);
            for(Map map1:list){
                if(labelIdList.contains(map1.get("id")) && "0".equals(map1.get("resultFlag"))){
                    map1.put("flag","0");//需巡检
                }else {
                    map1.put("flag","1");
                }
            }
            return new TableResultResponse(result.getTotal(),list);
        }else{
            List<Map> list =mapper.selectIndoorLabelListById(SplitUtil.merge(labelIdList), zxqy);
            for(Map map1:list){
                map1.put("flag","0");//需巡检
            }
            return new TableResultResponse(result.getTotal(),list);
        }
    }


    public List<Map> selectByBuildingId(Integer buildingId,String ids, String useFlag,String useTestFlag){
        return mapper.selectByBuildingId(buildingId,ids,useFlag,useTestFlag);
    }

    public List<Integer> selectByBuildingIdResultId(Integer buildingId,String ids, String useFlag, String useTestFlag){
        return mapper.selectByBuildingIdResultId(buildingId,ids,useFlag,useTestFlag);
    }

    public TableResultResponse selectIndoorLabelListByBuildingId(Query query,Integer routeId,Integer taskId, Integer buildingId) {
        List<Integer> idList = drlBiz.selectByRouteId(routeId,"0");
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = mapper.selectIndoorLabelListByBuildingId(buildingId,SplitUtil.merge(idList));
        for(Map map:list){
            List<DeviceIndoorRecordInspectionResults> resultsList =dirirBiz.selectByTaskId(taskId,Integer.valueOf(map.get("id").toString()));
            if(resultsList.size()==0){
                map.put("resultFlag","0");
            }else{
                for(int i=0;i<1;i++){
                    if("0".equals(resultsList.get(0).getLeakFlag())){
                        if("0".equals(resultsList.get(0).getInspectionResult())){
                            map.put("resultFlag","1");
                        }
                        if("1".equals(resultsList.get(0).getInspectionResult())){
                            map.put("resultFlag","2");
                        }
                    }
                    if("1".equals(resultsList.get(0).getLeakFlag())){
                        map.put("resultFlag","3");
                    }
                    map.put("resultsId",resultsList.get(0).getId());
                }
            }
//            if("0".equals(map.get("resultFlag"))){
//                if("1".equals(map.get("status"))){
//                    map.put("type","维修中");
//                }
//            }else{
//                List<DeviceIndoorRecordInspectionResults> resultsList = dirirBiz.selectByFacilitiesNo(map.get("facilitiesNo").toString());
//                for(DeviceIndoorRecordInspectionResults inspectionResults:resultsList){
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
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    public List<Map> selectListByBuildingId(Integer routeId, Integer buildingId, Integer taskId) {
        //查询该路线的设施id
        List<Integer> idList = drlBiz.selectByRouteId(routeId,"0");
        List<Map> list = mapper.selectIndoorLabelListByBuildingId(buildingId,SplitUtil.merge(idList));
        for(Map map:list){
            List<DeviceIndoorRecordInspectionResults> inspectionResultsList = dirirBiz.selectByTaskId(taskId,Integer.valueOf(map.get("id").toString()));
            if(inspectionResultsList.size()==0){
                map.put("resultFlag","0");
            }else{
                for(int i=0;i<1;i++){
                    if("0".equals(inspectionResultsList.get(0).getLeakFlag())){
                        if("0".equals(inspectionResultsList.get(0).getInspectionResult())){
                            map.put("resultFlag","1");
                        }
                        if("1".equals(inspectionResultsList.get(0).getInspectionResult())){
                            map.put("resultFlag","2");
                        }
                    }else{
                        map.put("resultFlag","3");
                    }
                    map.put("resultsId",inspectionResultsList.get(0).getId());
                }
            }
        }
        return list;
    }

    public DeviceIndoorLabel getIndoorLabelByFacilitiesNo(String facilitiesNo) {
        return mapper.getIndoorLabelByFacilitiesNo(facilitiesNo);
    }

    public DeviceIndoorLabel getById(Integer id) {
        return mapper.getById(id);
    }

    public void updateQrCodePath(DeviceIndoorLabel entity){
        mapper.updateQrCodePath(entity);
    }

    public Integer getNeedInspectionCount(String status,String useFlag,String resultFlag) {
        Map<String ,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("useFlag",useFlag);
        map.put("resultFlag",resultFlag);
        return mapper.getNeedInspectionCount(map);
    }

    public LabelnspectionVO getInspectionInfoById(Integer id){
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LabelnspectionVO rs = mapper.getInspectionInfoById(id);
        try {
            if(rs!=null){
                DeviceIndoorRecordInspectionResults params=new DeviceIndoorRecordInspectionResults();
                params.setLabelId(id);
                params.setLeakFlag("0");
                List<DeviceIndoorRecordInspectionResults> list = indoorRecordInspectionResultsBiz.selectList(params);
                if(CollectionUtils.isNotEmpty(list)){
                    rs.setResultFlag(list.get(list.size()-1).getInspectionResult());
                }

                rs.setNextInspectionTime("无");
                //查询计划
                DeviceInspectionScheme param=new DeviceInspectionScheme();
                param.setDelFlag("0");
                param.setInspectionRouteId(rs.getRouteId());
                //正常情况下只有一条数据，一条路线对应一个计划，因为很多垃圾数据会有多条，防止报错查询列表取第一条
                List<DeviceInspectionScheme> deviceInspectionSchemes = schemeBiz.selectList(param);
                if(CollectionUtils.isNotEmpty(deviceInspectionSchemes)){
                    DeviceInspectionScheme scheme=deviceInspectionSchemes.get(0);
                    rs.setPatrolCycle(scheme.getPatrolCycle());
                    // TODO 查询巡检时间段
                    DeviceInspectionTime param1=new DeviceInspectionTime();
                    param1.setInspectionSchemeId(scheme.getId());
                    List<DeviceInspectionTime> times = inspectionTimeBiz.selectList(param1);
                    String nextInspectionDate = getInspectionDate(scheme.getStartTime(), scheme.getEndTime(), scheme.getPatrolCycle());
                    String s, s1;
                    if(nextInspectionDate!=null){
                         //如果下一次巡检日期在今天之后，取第一个时间段
                        if(f1.parse(nextInspectionDate).after(f1.parse(f1.format(new Date())))){
                             s = nextInspectionDate + " " + times.get(0).getInspectionTime().substring(0,5) + ":00";
                             s1 =nextInspectionDate + " " + times.get(0).getInspectionTime().substring(6) + ":00";
                            rs.setNextInspectionTime(s+"至"+s1);
                        }else {
                            //如果是当天，则看当天的时段是否已经执行巡检
                            boolean temp=false;
                            for (DeviceInspectionTime inspectionTime:times) {
                                String time = inspectionTime.getInspectionTime();
                                 s = nextInspectionDate + " " + time.substring(0,5) + ":00";
                                 s1 =nextInspectionDate + " " + time.substring(6) + ":00";
                                Date startDate = f2.parse(s);
                                Date endDate =  f2.parse(s1);
                                Date now= new Date();
                                if(now.before(endDate)){
                                    //查询该时段是否巡检
                                    boolean exist = indoorRecordInspectionResultsBiz.checkInspection(startDate, endDate, rs.getRouteId(), id);
                                    if(!exist){
                                        temp=true;
                                        rs.setNextInspectionTime(s+"至"+s1);
                                    }
                                }
                            }
                            //当天没有下一次巡检时段，则取下一次
                            if(!temp){
                                Date toDate = new DateTime(f1.parse(nextInspectionDate)).plusDays(scheme.getPatrolCycle()).toDate();
                                if(toDate.before(scheme.getEndTime())){
                                     s = f1.format(toDate) + " " + times.get(0).getInspectionTime().substring(0,5) + ":00";
                                     s1 =f1.format(toDate) + " " + times.get(0).getInspectionTime().substring(6) + ":00";
                                    rs.setNextInspectionTime(s+"至"+s1);
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return rs;
        }

        return rs;
    }
    private static String getInspectionDate(Date  start,Date end,Integer patrolCycle){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date temp=null;
        //暂定义最多1000次
       for(int i=1;1<1000;i++){
           temp = new DateTime(start).plusDays(patrolCycle * i).toDate();
           format.format(temp);
           try {
               if(format.parse(format.format(temp)).getTime()-format.parse(format.format(new Date())).getTime()>=0){
                break;
               }
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }
       if(end!=null){
           if(temp!=null && temp.after(end)){
               return null;
           }
       }

        return format.format(temp);
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd");
        Date start = f2.parse("2019-09-06");
        getInspectionDate(start,null,1);
    }
/*
    public static void main(String[] args) {
        List<DeviceInspectionTime> times =new ArrayList<>();
        DeviceInspectionTime d1=new DeviceInspectionTime();
        d1.setInspectionTime("05:00-23:38");
       *//* DeviceInspectionTime d2=new DeviceInspectionTime();
        d2.setInspectionTime("16:00-17:30");
        DeviceInspectionTime d3=new DeviceInspectionTime();
        d3.setInspectionTime("19:00-20:30");*//*

        times.add(d1);
       *//* times.add(d2);
        times.add(d3);*//*
        String nextInspectionDate="2019-07-27";
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
        String s, s1;
       try {
           if(nextInspectionDate!=null){
               //如果下一次巡检日期在今天之后，取第一个时间段
               if(f1.parse(nextInspectionDate).after(f1.parse(f1.format(new Date())))){
                   s = nextInspectionDate + " " + times.get(0).getInspectionTime().substring(0,5) + ":00";
                   s1 =nextInspectionDate + " " + times.get(0).getInspectionTime().substring(6) + ":00";
                   System.out.println("巡检日期在今天之后："+s+"至"+s1);
                  // rs.setNextInspectionTime(s+"至"+s1);
               }else {
                   //如果是当天，则看当天的时段是否已经执行巡检
                   boolean temp=false;
                   for (DeviceInspectionTime inspectionTime:times) {
                       String time = inspectionTime.getInspectionTime();
                       s = nextInspectionDate + " " + time.substring(0,5) + ":00";
                       s1 =nextInspectionDate + " " + time.substring(6) + ":00";
                       Date startDate = f2.parse(s);
                       Date endDate =  f2.parse(s1);
                       Date now= new Date();
                       if(now.before(endDate)){
                           //查询该时段是否巡检
                           //boolean exist = indoorRecordInspectionResultsBiz.checkInspection(startDate, endDate, rs.getRouteId(), id);
                           if(1==0){
                               temp=true;
                               System.out.println("巡检日期在今天："+s+"至"+s1);
                               //rs.setNextInspectionTime(s+"至"+s1);
                           }
                       }
                   }
                   //当天没有下一次巡检时段，则取下一次
                   if(!temp){
                       Date toDate = new DateTime(f1.parse(nextInspectionDate)).plusDays(2).toDate();
                       if(1==1){
                           s = f1.format(toDate) + " " + times.get(0).getInspectionTime().substring(0,5) + ":00";
                           s1 =f1.format(toDate) + " " + times.get(0).getInspectionTime().substring(6) + ":00";
                           System.out.println("巡检日期在今天,但是已经执行完成："+s+"至"+s1);
                           //rs.setNextInspectionTime(s+"至"+s1);
                       }

                   }
               }
           }
       }catch (ParseException e){

       }
    }*/

   /* public static void main(String[] args) {
        Integer patrolCycle=3;
        Date start=new DateTime(new Date()).minusDays(2).toDate();
        Date end=new DateTime(new Date()).plusDays(0).toDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date temp=null;
        //暂定义最多1000次
        for(int i=1;1<1000;i++){
            temp = new DateTime(start).plusDays(patrolCycle * i).toDate();
            format.format(temp);
            try {
                if(format.parse(format.format(temp)).getTime()-format.parse(format.format(new Date())).getTime()>=0){
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(temp!=null && temp.after(end)){
            System.out.println("已经过期");
        }
        System.out.println("最近巡检日："+format.format(temp));
    }*/

    /**
     * 获取所有设备的状态信息
     * @return
     */
    public List<Map<String,Object>> getAllStatus(){
        return mapper.selectAllStatus();
    }

}