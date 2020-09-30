package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.util.BooleanUtil;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.entity.DeviceVideoAnalysisSolution;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.entity.DeviceVideoGroup;
import cn.turing.firecontrol.device.mapper.DeviceVideoExtMapper;
import cn.turing.firecontrol.device.util.YingShiUtil;
import cn.turing.firecontrol.device.vo.DeviceSensorVo;
import cn.turing.firecontrol.device.vo.VideoDeviceVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019/02/20 11:33
 *
 * @Description
 * @Version V1.0
 */
@Service
@Slf4j
public class DeviceVideoExtBiz extends BusinessBiz<DeviceVideoExtMapper, DeviceVideoExt> {

    @Autowired
    private DeviceVideoExtMapper deviceVideoExtMapper;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private YingShiUtil yingShiUtil;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private DeviceVideoAnalysisSolutionBiz deviceVideoAnalysisSolutionBiz;
    //用于获取视频设备直播地址的线程池
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    /**
     *  查询所有需要进行某项分析的视频设备
     * @param analysisCode
     * @return
     */
    public List<DeviceVideoExt> getAllToAnalysisDevices(String analysisCode,String tenantId) {
        if (StringUtils.isBlank(analysisCode)) {
            throw new RuntimeException("分析方案代码不能为空");
        }
        return deviceVideoExtMapper.getAllToAnalysisDevices(analysisCode,tenantId);
    }

    /**
     * 添加视频设备信息
     * @param entity
     */
    @Transactional
    public DeviceVideoExt saveDevice(DeviceVideoExt entity){
        ValidatorUtils.validateEntity(entity);
        DeviceVideoExt queryEntity = new DeviceVideoExt();
        queryEntity.setSensorNo(entity.getSensorNo());
        List<DeviceVideoExt> devices= queryOnlyExt(queryEntity);
        if(devices != null && !devices.isEmpty()){
            throw new RuntimeException("设备已存在");
        }
        //1，调用萤石API添加设备，关闭摄像头加密
        String accessToken = yingShiUtil.getaccessToken(redisTemplate);
        String realSensorNo = entity.getSensorNo().split(":")[0];
        yingShiUtil.addDevice(accessToken,realSensorNo,entity.getDeviceValidateCode());
        yingShiUtil.openLive(accessToken,Lists.newArrayList(entity.getSensorNo()));
        yingShiUtil.offEncrypt(accessToken,realSensorNo,entity.getDeviceValidateCode());
        //2，保存DeviceSensor信息
        DeviceSensor sensor = new DeviceSensor();
        try {
            BeanUtils.copyProperties(sensor,entity);
        } catch (IllegalAccessException e) {
            log.error("添加设备失败",e);
            throw new RuntimeException("添加设备失败",e);
        } catch (InvocationTargetException e) {
            log.error("添加设备失败",e);
            throw new RuntimeException("添加设备失败",e);
        }
        sensor.setStatus("3");
        deviceSensorBiz.insertSelective(sensor);
        //3、保存DeviceVideoExt信息
        entity.setId(sensor.getId());
        mapper.insertSelective(entity.toOnlyExt());
        return entity;
    }



    public DeviceVideoExt saveDevices(DeviceVideoExt entity){
        entity = saveDevice(entity);
        //获取设备的直播地址
        List<DeviceVideoExt> entities = Lists.newArrayList();
        entities.add(entity);
        scheduledThreadPool.schedule(new CaptureAddressThead(entities),1, TimeUnit.MINUTES); //延迟X分钟执行线程
        return entity;
    }

    public void saveDevices(List<DeviceVideoExt> entitys){
        for(DeviceVideoExt entity : entitys){
            saveDevice(entity);
        }
        //获取设备的直播地址
        scheduledThreadPool.schedule(new CaptureAddressThead(entitys),1, TimeUnit.MINUTES); //延迟X分钟执行线程
    }



    /**
     * 由于萤石开放平台添加设备后，无法立即获取直播地址，故采用异步的方式获取视频直播地址
     */
    @AllArgsConstructor
    class CaptureAddressThead implements Runnable{
        private List<DeviceVideoExt> entitys;
        @Override
        public void run() {
            while (!entitys.isEmpty()){
                String accessToken = yingShiUtil.getaccessToken(redisTemplate);
                List<String> sensorNos = Lists.newArrayList();
                for(DeviceVideoExt entity : entitys){
                    sensorNos.add(entity.getSensorNo());
                }
                Map<String,String> addresses =  yingShiUtil.getLiveAddress(accessToken,sensorNos);
                String address = null;
                Iterator<DeviceVideoExt> iterator =  entitys.iterator();
                DeviceVideoExt entity = null;
                while (iterator.hasNext()){
                    entity = iterator.next();
                    address = addresses.get(entity.getSensorNo());
                    if(address != null){
                        entity.setVideoLiveAddress(address);
                        mapper.updateByPrimaryKeySelective(entity.toOnlyExt());
                        iterator.remove();
                    }
                }
                try {
                    //休眠30秒后重试
                    Thread.sleep(30*1000);
                } catch (InterruptedException e) {
                    log.info("线程休眠失败",e);
                }
            }
        }
    }


    /**
     *  根据ID删除设备及其扩展信息
     */
    @Override
    @Transactional
    public void deleteById(Object id){
        DeviceVideoExt device = new DeviceVideoExt();
        device.setId(Long.parseLong(id.toString()));
        List<DeviceVideoExt> list = queryOnlyExt(device);
        if(list == null || list.isEmpty()){
            throw new RuntimeException("设备不存在");
        }
        //1、删除设备信息
        mapper.delete(device);
        //2、逻辑删除设备扩展信息
        device.setDelFlag("1");
        deviceSensorBiz.updateSelectiveById(device);
        //3、如果该序列号的设备都已经删除，则调用萤石API删除设备
        device = list.get(0);
        String[] sensorNoAndChannelNo = device.getSensorNo().split(":");
        String realSensorNo = sensorNoAndChannelNo[0];
        List<DeviceVideoExt> sameRealSensors = querySameRealSensorNos(realSensorNo);
        if(sameRealSensors.isEmpty()){
            yingShiUtil.deleteDevice(yingShiUtil.getaccessToken(redisTemplate),realSensorNo);
        }
    }


    public DeviceSensorVo getById(Long sensorId){
        return deviceVideoExtMapper.queryById(sensorId,BaseContextHandler.getTenantID());
    }

    /**
     * 查询视频设备详细信息
     * 传感器信息
     * 设备组名称
     * 分析解决方案名称
     * @param sensorId
     * @return
     */
    public VideoDeviceVo getVideoDeviceDetail(Long sensorId){
        return deviceVideoExtMapper.getVideoDeviceDetail(sensorId,BaseContextHandler.getTenantID());
    }


    /**
     * 分页查询设备信息
     * @param groupId
     * @return
     */
    public Page<Map<String,Object>> queryByPage(Integer page, Integer limit, Integer groupId, String sensorNo, String status, String deviceName,
                                              String manufacturer, String equipmentType, String model, String isMark){
        Page<Map<String,Object>> pageData =  PageHelper.startPage(page,limit);
        List<Map<String,Object>> list = mapper.queryByPage(groupId,sensorNo,status,deviceName,manufacturer,equipmentType,model,isMark,BaseContextHandler.getTenantID());
        for(Map<String,Object> map : list){
            String[] sn = map.get("sensorNo").toString().split(":");
            map.put("sensorNo",sn[0]);
            map.put("channelNo",sn.length >= 2 ? sn[1] : "1");
        }
        return pageData;
    }

    /**
     * 根据某（几）个属性查询设备
     * @param ext
     * @return
     */
    public List<DeviceVideoExt> queryOnlyExt(DeviceVideoExt ext){
        return mapper.queryOnlyExt(ext);
    }

    /**
     * 分页查询所有需要显示的视频设备
     * @param page
     * @param limit
     * @return
     */
    public Page<Map<String,Object>> queryShowDevice(Integer page,Integer limit){
        Page<Map<String,Object>> pageData = PageHelper.startPage(page,limit);
        mapper.queryDevice("1",null,null,BaseContextHandler.getTenantID());
        return pageData;
    }

    /**
     * 修改设备显示状态
     * @param deviceIds
     * @param isShow
     */
    public void updateDeviceShowStatus(Long[] deviceIds, Boolean isShow){
        String status = BooleanUtil.switchValue(isShow);
        String tenantId = BaseContextHandler.getTenantID();
        mapper.updateDeviceShowStatus(deviceIds,status,tenantId);
    }


    /**
     * 更新设备信息
     * @param entity
     */
    @Override
    @Transactional
    public void updateSelectiveById(DeviceVideoExt entity) {
        //1、更新视频设备基本信息
        mapper.updateByPrimaryKeySelective(entity.toOnlyExt());
        //2、更新视频设备扩展信息
        DeviceSensor sensor = new DeviceSensor();
        try {
            BeanUtils.copyProperties(sensor,entity);
        } catch (IllegalAccessException e) {
            log.error("更新设备信息失败",e);
            throw new RuntimeException("更新设备信息失败",e);
        } catch (InvocationTargetException e) {
            log.error("更新设备信息失败",e);
            throw new RuntimeException("更新设备信息失败",e);
        }
        deviceSensorBiz.updateSelectiveById(entity);
    }


    /**
     * 不分设备组，查询设备
      * @param isShow
     * @param hasSolution
     * @return
     */
    public List<Map<String,Object>> queryDevices(Boolean isShow,Boolean hasSolution,Integer groupId){
        String showFlag = null;
        if(isShow != null){
            showFlag = BooleanUtil.switchValue(isShow);
        }
        return mapper.queryDevice(showFlag,hasSolution,groupId,BaseContextHandler.getTenantID());
    }

    /**
     * 更新设备分析方案，solutionID为空则是取消方案
     * @param solutionId
     * @param deviceId
     */
    public void configSolution(Integer solutionId, Long deviceId){
        DeviceVideoExt ext = new DeviceVideoExt();
        ext.setId(deviceId);
        List<DeviceVideoExt> list = queryOnlyExt(ext);
        if(list == null || list.isEmpty()){
            throw new RuntimeException("设备不存在");
        }
        if(solutionId != null){
            DeviceVideoAnalysisSolution solution = deviceVideoAnalysisSolutionBiz.selectById(solutionId);
            if(solution == null){
                throw new RuntimeException("分析方案不存在");
            }
        }
        mapper.updateSolution(solutionId,deviceId);
    }


    /**
     * 更新设备扩展信息
     * @param ext
     */
    public void updateOnlyExt(DeviceVideoExt ext){
        DeviceVideoExt entity = ext.toOnlyExt();
        mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 返回视频设备状态统计
     * @return
     */
    public int[] countVideoSensor(){
        int[] sum = new int[4];
        Map<String,Object> map = mapper.countVideoSensor();
        sum[0] = ((BigDecimal)map.get("alarm")).intValue();
        sum[1] = ((BigDecimal)map.get("broken")).intValue();
        sum[2] = ((BigDecimal)map.get("normal")).intValue();
        sum[3] = sum[0] + sum[1] + sum[2];
        return sum;
    }

    /**
     * 查询同一个真实设备序列号的设备列表
     * @param realSensorNo
     * @return
     */
    public List<DeviceVideoExt> querySameRealSensorNos(String realSensorNo){
        return mapper.querySameRealSensorNos(realSensorNo);
    }




    /**
     * 获取监控地址及AccessToken
     * @return  //ezopen:// [验证码@] open.ys7.com/[deviceSerial]/[channelNo] [.hd].rec[?begin= yyyyMMddhhmmss&end= yyyyMMddhhmmss]
     */
    public Map<String,String> getMonitorUrl(Long deivceId, Boolean isHd,Date begin, Date end){
        Map<String,String> map = new HashMap<>();
        map.put("accessToken",yingShiUtil.getaccessToken(redisTemplate));
        DeviceVideoExt queryEntity = new DeviceVideoExt();
        queryEntity.setId(deivceId);
        List<DeviceVideoExt> entities = queryOnlyExt(queryEntity);
        if(entities == null || entities.isEmpty()){
            throw new RuntimeException("设备不存在");
        }
        DeviceVideoExt entity = entities.get(0);
        String[] sensorNoAndChannelNo = entity.getSensorNo().split(":");
        String realSensorNo = sensorNoAndChannelNo[0];
        String channelNo = sensorNoAndChannelNo.length >= 2 ? sensorNoAndChannelNo[1] : "1";
        StringBuilder monitorUrl = new StringBuilder("ezopen://open.ys7.com/");
        monitorUrl.append(realSensorNo).append("/").append(channelNo);
        if(isHd != null && isHd){
            monitorUrl.append(".hd");
        }
        monitorUrl.append(".rec");
        if(begin!=null && end != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            monitorUrl.append("?begin=").append(dateFormat.format(begin)).append("&end=").append(dateFormat.format(end));
        }
        map.put("monitorUrl",monitorUrl.toString());
        return map;
    }


    /**
     * 获取设备截图
     * @param deviceId
     * @return
     */
    public String getImage(Long deviceId){
        DeviceVideoExt ext = new DeviceVideoExt();
        ext.setId(deviceId);
        List<DeviceVideoExt> exts = queryOnlyExt(ext);
        if(exts == null || exts.isEmpty()){
            throw new ParamErrorException("设备不存在");
        }
        ext = exts.get(0);
        String[] sensorNoAndChannelNo = ext.getSensorNo().split(":");
        String sensorNo = sensorNoAndChannelNo[0];
        String channelNo = sensorNoAndChannelNo.length >= 2 ? sensorNoAndChannelNo[1] : "1";
        String accessToken = yingShiUtil.getaccessToken(redisTemplate);
        String image = yingShiUtil.getImageUrl(accessToken,sensorNo,Integer.valueOf(channelNo));
        return image;
    }


    public int updateOnlyExtSelectiveBySensorNo(DeviceVideoExt entity){
        return mapper.updateOnlyExtSelectiveBySensorNo(entity);
    }

}
