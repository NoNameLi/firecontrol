package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.IUserFeign;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangpeng
 * @email: 723308025@qq.com
 * @create: 2018-12-03 18:09
 **/
@Component
public class DeviceTasks {

    @Autowired
    private DeviceInspectionSchemeBiz disBiz;
    @Autowired
    private DeviceInspectionTasksBiz ditBiz;
    @Autowired
    private DeviceInspectionTimeBiz deviceInspectionTimeBiz;
    @Autowired
    private DeviceInspectionRouteBiz dirBiz;
    @Autowired
    private DeviceIndoorLabelBiz dilBiz;
    @Autowired
    private DeviceRouteLabelBiz drlBiz;
    @Autowired
    private DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceFacilitiesTypeBiz dftBiz;
    @Autowired
    private DeviceOutdoorLabelBiz dolBiz;
    @Autowired
    private DeviceIndoorRecordInspectionResultsBiz dirirBiz;
    @Autowired
    private DeviceOutdoorRecordInspectionResultsBiz dorirBiz;
    @Autowired
    private IUserFeign iUserFeign;
    @Autowired
    private RedisTemplate redisTemplate;

    private  static final Logger log = LoggerFactory.getLogger(DeviceTasks.class);

    private static final String LOCK = "task-job-lock";

    private static final String KEY = "tasklock";

//    private static final String LOCK1 = "task-job-lock1";
//
//    private static final String KEY1 = "tasklock1";


//    //@Scheduled(cron = "0 0 0 * * *")//每天凌晨
//    //@Scheduled(cron = "0/30 * * * * *")
//    @Scheduled(cron = "0 0/30 * * * ?")//半小时执行一次
//    public void createTask(){
//        boolean lock = false;
//        try{
//            // 获取锁
//            lock = setScheduler(KEY,LOCK);
//            log.info("是否获取到锁:"+lock);
//            if(lock){
//                //if(setScheduler("taskKey1","exist")){
//                    log.info("开始生成任务......");
//                    //redisTemplate.expire(KEY,20, TimeUnit.MINUTES);
//                    List<DeviceInspectionScheme> list = disBiz.getListAll();
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
//                    for(DeviceInspectionScheme inspectionScheme:list){
//                        DeviceInspectionRoute deviceInspectionRoute = dirBiz.getById(inspectionScheme.getInspectionRouteId());
//                        if(deviceInspectionRoute==null || "0".equals(deviceInspectionRoute.getLabelCount())){
//                            continue;
//                        }
//                        if(date.getTime()>=inspectionScheme.getStartTime().getTime()){
//                            if(inspectionScheme.getEndTime()==null || date.getTime()<=inspectionScheme.getEndTime().getTime()){
//                                int days = (int) ((date.getTime() - inspectionScheme.getStartTime().getTime()) / (1000*3600*24));
//                                if(days==0 || days%inspectionScheme.getPatrolCycle()==0){
//                                    //查询巡检计划时段列表
//                                    List<DeviceInspectionTime> inspectionTimeList =deviceInspectionTimeBiz.selectBySchemeId(inspectionScheme.getId(),inspectionScheme.getTenantId());
//                                    if(inspectionTimeList.size()==0){
//                                        continue;
//                                    }
//                                    for(DeviceInspectionTime deviceInspectionTime:inspectionTimeList){
//                                        //根据时段生成巡检任务
//                                        DeviceInspectionTasks deviceInspectionTasks = new DeviceInspectionTasks();
//                                        deviceInspectionTasks.setInspectionRouteId(inspectionScheme.getInspectionRouteId());
//                                        deviceInspectionTasks.setInspectionDate(simpleDateFormat.parse(simpleDateFormat.format(date)));
//                                        deviceInspectionTasks.setStatus("0");
//                                        deviceInspectionTasks.setInspectionTimePeriod(deviceInspectionTime.getInspectionTime());
//                                        //计算时长
//                                        String[] time =deviceInspectionTime.getInspectionTime().split("-");
//                                        String begin= simpleDateFormat.format(date)+" "+time[0].trim()+":00";
//                                        String end = simpleDateFormat.format(date)+" "+time[1].trim()+":00";
//                                        long temp1=sdf.parse(end).getTime();
//                                        long temp2=sdf.parse(begin).getTime();
//                                        long diff = temp1-temp2;
//                                        long day=diff/(24*60*60*1000);
//                                        long hour=(diff/(60*60*1000)-day*24);
//                                        long min=((diff/(60*1000))-day*24*60-hour*60);
//                                        int patrolCycle = (int)(day*24*60+hour*60+min);
//                                        deviceInspectionTasks.setPatrolCycle(patrolCycle);
//                                        deviceInspectionTasks.setDelFlag("0");
//                                        deviceInspectionTasks.setTenantId(inspectionScheme.getTenantId());
//                                        deviceInspectionTasks.setCrtTime(new Date());
//                                        ditBiz.insert(deviceInspectionTasks);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    Thread.sleep(60000);
//                }else{
//                log.info("没有获取到锁，不执行任务!");
//                return;
//            }
////            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            if(lock){
//                delete(KEY);
//                log.info("任务结束，释放锁!");
//            }else {
//                log.info("没有获取到锁，无需释放锁!");
//            }
//        }
//    }

    /**
     * 删除前一天的任务,更新设施标签巡检状态并生成漏检记录
     */
    @Scheduled(cron = "0 0 0 * * *")//每天凌晨
//    @Scheduled(cron = "0/30 * * * * *")
//    @Scheduled(cron = "0 0/10 * * * ?")//半小时执行一次
    public void delectTask(){
        boolean lock = false;
        try{
            // 获取锁
            lock = setScheduler(KEY,LOCK);
            log.info("是否获取到锁:"+lock);
            if(lock){
                //if(setScheduler("taskKey2","exist")){
                    log.info("开始清算任务......");
                    //redisTemplate.expire(KEY1,20, TimeUnit.MINUTES);
                    List<DeviceInspectionTasks> list =ditBiz.getListAll();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for(DeviceInspectionTasks deviceInspectionTasks:list){
                        //查询巡检路线
                        DeviceInspectionRoute deviceInspectionRoute = dirBiz.getById(deviceInspectionTasks.getInspectionRouteId());
                        if(deviceInspectionRoute==null){
                            ditBiz.update(deviceInspectionTasks.getId());
                            continue;
                        }
                        if("0".equals(deviceInspectionRoute.getRouteFlag())){//室内路线
                            //查询该巡检路线的设施标签id
                            List<Integer> list1 = drlBiz.getByRouteId(deviceInspectionRoute.getId(),"0",deviceInspectionTasks.getTenantId());
                            for(Integer id:list1){
                                DeviceIndoorLabel deviceIndoorLabel =dilBiz.getById(id);
                                if(deviceIndoorLabel==null){
                                    continue;
                                }
                                DeviceBuilding deviceBuilding = dbBiz.getById(deviceIndoorLabel.getBuildingId());
                                DeviceFacilitiesType deviceFacilitiesType = dftBiz.getById(deviceIndoorLabel.getFacilitiesTypeId());
                                DeviceIndoorRecordInspectionResults indoorRecordInspectionResults = new DeviceIndoorRecordInspectionResults();
                                indoorRecordInspectionResults.setLabelId(id);
                                indoorRecordInspectionResults.setLeakFlag("1");
                                indoorRecordInspectionResults.setbName(deviceBuilding.getBName());
                                indoorRecordInspectionResults.setFloor(deviceIndoorLabel.getFloor());
                                indoorRecordInspectionResults.setFacilitiesNo(deviceIndoorLabel.getFacilitiesNo());
                                indoorRecordInspectionResults.setEquipmentType(deviceFacilitiesType.getEquipmentType());
                                indoorRecordInspectionResults.setPositionDescription(deviceIndoorLabel.getPositionDescription());
                                String plannedCompletionTime = simpleDateFormat.format(deviceInspectionTasks.getInspectionDate()).substring(0,10)+" "+deviceInspectionTasks.getInspectionTimePeriod().substring(6,11);
                                indoorRecordInspectionResults.setPlannedCompletionTime(sdf.parse(plannedCompletionTime));//计划完成时间
                                indoorRecordInspectionResults.setTenantId(deviceIndoorLabel.getTenantId());
                                indoorRecordInspectionResults.setTaskId(deviceInspectionTasks.getId());
                                indoorRecordInspectionResults.setDelFlag("0");
                                indoorRecordInspectionResults.setCrtTime(new Date());
                                indoorRecordInspectionResults.setInspectionDate(new Date());
                                //如果任务状态为未接取
                                if("0".equals(deviceInspectionTasks.getStatus())){
                                    indoorRecordInspectionResults.setInspectionPerson("无人接取");
                                    indoorRecordInspectionResults.setProblemDescription("无人接取任务");//漏检原因
                                    //dirirBiz.insertSelective(indoorRecordInspectionResults);
                                    dirirBiz.insert(indoorRecordInspectionResults);
                                }
                                if("1".equals(deviceInspectionTasks.getStatus())|| "2".equals(deviceInspectionTasks.getStatus())){
                                    //如果设施标签巡检结果为未检测
                                    if("0".equals(deviceIndoorLabel.getResultFlag()) && deviceInspectionTasks.getUserId()!=null){
                                        //根据巡检人员id查询巡检人员
                                        JSONObject jsonObject = iUserFeign.getUserById(deviceInspectionTasks.getUserId());
                                        String username =jsonObject.getJSONObject("data").getString("username");
                                        indoorRecordInspectionResults.setInspectionPerson(username);
                                        indoorRecordInspectionResults.setProblemDescription(username+"("+jsonObject.getJSONObject("data").getString("name")+") 漏检");//漏检原因
                                        //dirirBiz.insertSelective(indoorRecordInspectionResults);
                                        dirirBiz.insert(indoorRecordInspectionResults);
                                    }
                                }
                                //更新标签巡检结果状态
                                deviceIndoorLabel.setResultFlag("0");//未检测
                                dilBiz.updateSelectiveById(deviceIndoorLabel);
                            }
                        }
                        if("1".equals(deviceInspectionRoute.getRouteFlag())){//室外路线
                            List<Integer> list1 = drlBiz.getByRouteId(deviceInspectionRoute.getId(),"1",deviceInspectionTasks.getTenantId());
                            for(Integer id:list1){
                                DeviceOutdoorLabel deviceOutdoorLabel =dolBiz.getById(id);
                                if(deviceOutdoorLabel==null){
                                    continue;
                                }
                                DeviceFacilitiesType deviceFacilitiesType = dftBiz.getById(deviceOutdoorLabel.getFacilitiesTypeId());
                                DeviceOutdoorRecordInspectionResults outdoorRecordInspectionResults = new DeviceOutdoorRecordInspectionResults();
                                outdoorRecordInspectionResults.setLabelId(id);
                                outdoorRecordInspectionResults.setLeakFlag("1");
                                outdoorRecordInspectionResults.setFacilitiesNo(deviceOutdoorLabel.getFacilitiesNo());
                                outdoorRecordInspectionResults.setEquipmentType(deviceFacilitiesType.getEquipmentType());
                                outdoorRecordInspectionResults.setPositionDescription(deviceOutdoorLabel.getPositionDescription());
                                String plannedCompletionTime = simpleDateFormat.format(deviceInspectionTasks.getInspectionDate()).substring(0,10)+" "+deviceInspectionTasks.getInspectionTimePeriod().substring(6,11);
                                outdoorRecordInspectionResults.setPlannedCompletionTime(sdf.parse(plannedCompletionTime));//计划完成时间
                                outdoorRecordInspectionResults.setTenantId(deviceOutdoorLabel.getTenantId());
                                outdoorRecordInspectionResults.setTaskId(deviceInspectionTasks.getId());
                                outdoorRecordInspectionResults.setDelFlag("0");
                                outdoorRecordInspectionResults.setCrtTime(new Date());
                                outdoorRecordInspectionResults.setInspectionDate(new Date());
                                //如果任务状态为未接取
                                if("0".equals(deviceInspectionTasks.getStatus())){
                                    outdoorRecordInspectionResults.setInspectionPerson("无人接取");
                                    outdoorRecordInspectionResults.setProblemDescription("无人接取任务");//漏检原因
                                    //dorirBiz.insertSelective(outdoorRecordInspectionResults);
                                    dorirBiz.insert(outdoorRecordInspectionResults);
                                }
                                if("1".equals(deviceInspectionTasks.getStatus())|| "2".equals(deviceInspectionTasks.getStatus())){
                                    //如果设施标签巡检结果为未检测
                                    if("0".equals(deviceOutdoorLabel.getResultFlag()) && deviceInspectionTasks.getUserId()!=null){
                                        //根据巡检人员id查询巡检人员
                                        JSONObject jsonObject = iUserFeign.getUserById(deviceInspectionTasks.getUserId());
                                        String username = jsonObject.getJSONObject("data").getString("username");
                                        outdoorRecordInspectionResults.setInspectionPerson(username);
                                        outdoorRecordInspectionResults.setProblemDescription(username+"("+jsonObject.getJSONObject("data").getString("name")+") 漏检");//漏检原因
                                        //dorirBiz.insertSelective(outdoorRecordInspectionResults);
                                        dorirBiz.insert(outdoorRecordInspectionResults);
                                    }
                                }
                                //更新标签巡检结果状态
                                deviceOutdoorLabel.setResultFlag("0");//未检测
                                dolBiz.updateSelectiveById(deviceOutdoorLabel);
                            }
                        }
                        //删除前一天的所有任务
                        ditBiz.update(deviceInspectionTasks.getId());
                    }
                    Thread.sleep(60000);
                }else{
                log.info("没有获取到锁，不执行任务!");
                return;
            }
            //}
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(lock){
                createTasks();
                delete(KEY);
                log.info("任务结束，释放锁!");
            }else {
                log.info("没有获取到锁，无需释放锁!");
            }
        }
    }

    public boolean setScheduler(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            return operations.setIfAbsent(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public  ValueOperations<String,Object> getValueOperations(){
        ValueOperations<String,Object> vo = redisTemplate.opsForValue();
        return vo;
    }

    public Object get(String key) {
        return getValueOperations().get(key);
    }

    public void delete(String key) {
        RedisOperations<String, Object> operations = getValueOperations().getOperations();
        operations.delete(key);
    }

    public void createTasks(){
        try{
            List<DeviceInspectionScheme> list = disBiz.getListAll();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            for(DeviceInspectionScheme inspectionScheme:list){
                DeviceInspectionRoute deviceInspectionRoute = dirBiz.getById(inspectionScheme.getInspectionRouteId());
                if(deviceInspectionRoute==null || "0".equals(deviceInspectionRoute.getLabelCount())){
                    continue;
                }
                if(date.getTime()>=inspectionScheme.getStartTime().getTime()){
                    if(inspectionScheme.getEndTime()==null || date.getTime()<=inspectionScheme.getEndTime().getTime()){
                        int days = (int) ((date.getTime() - inspectionScheme.getStartTime().getTime()) / (1000*3600*24));
                        if(days==0 || days%inspectionScheme.getPatrolCycle()==0){
                            //查询巡检计划时段列表
                            List<DeviceInspectionTime> inspectionTimeList =deviceInspectionTimeBiz.selectBySchemeId(inspectionScheme.getId(),inspectionScheme.getTenantId());
                            if(inspectionTimeList.size()==0){
                                continue;
                            }
                            for(DeviceInspectionTime deviceInspectionTime:inspectionTimeList){
                                //根据时段生成巡检任务
                                DeviceInspectionTasks deviceInspectionTasks = new DeviceInspectionTasks();
                                deviceInspectionTasks.setInspectionRouteId(inspectionScheme.getInspectionRouteId());
                                deviceInspectionTasks.setInspectionDate(simpleDateFormat.parse(simpleDateFormat.format(date)));
                                deviceInspectionTasks.setStatus("0");
                                deviceInspectionTasks.setInspectionTimePeriod(deviceInspectionTime.getInspectionTime());
                                //计算时长
                                String[] time =deviceInspectionTime.getInspectionTime().split("-");
                                String begin= simpleDateFormat.format(date)+" "+time[0].trim()+":00";
                                String end = simpleDateFormat.format(date)+" "+time[1].trim()+":00";
                                long temp1=sdf.parse(end).getTime();
                                long temp2=sdf.parse(begin).getTime();
                                long diff = temp1-temp2;
                                long day=diff/(24*60*60*1000);
                                long hour=(diff/(60*60*1000)-day*24);
                                long min=((diff/(60*1000))-day*24*60-hour*60);
                                int patrolCycle = (int)(day*24*60+hour*60+min);
                                deviceInspectionTasks.setPatrolCycle(patrolCycle);
                                deviceInspectionTasks.setDelFlag("0");
                                deviceInspectionTasks.setTenantId(inspectionScheme.getTenantId());
                                deviceInspectionTasks.setCrtTime(new Date());
                                ditBiz.insert(deviceInspectionTasks);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
