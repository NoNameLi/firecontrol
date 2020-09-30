package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.EntityUtils;
import cn.turing.firecontrol.device.dto.FireDoorSensorDto;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.mapper.DeviceSensorFdExtMapper;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.vo.FdSensorVo;
import cn.turing.firecontrol.device.vo.FireDoorVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.turing.firecontrol.device.mapper.DeviceFireDoorMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceFireDoorBiz extends BusinessBiz<DeviceFireDoorMapper,DeviceFireDoor>{

    @Autowired
    private DeviceFireDoorMapper deviceFireDoorMapper;

    @Autowired
    private DeviceSensorFdExtMapper deviceSensorFdExtMapper;

    @Autowired
    private DeviceBuildingBiz deviceBuildingBiz;

    @Autowired
    protected DeviceSensorTypeBiz dstBiz;

    @Autowired
    private DeviceSensorBiz deviceSensorBiz;

    @Autowired
    private DeviceFloorLayoutBiz deviceFloorLayoutBiz;

    //正常状态
    public static final String  DELELE_FLAG_NO = "0";
    //删除状态
    public static final String  DELELE_FLAG_YES = "1";
    //新增时，传感器数量默认值
    public static final int FIRE_DOOR_INIT_SENSOR_NUM = 0;

    //非门磁传感器
    public static final String NOT_MC_SENSOR = "0";
    //开门状态
    public static final String FIRE_DOOR_STATUS_OPEN = "1";
    //关门状态
    public static final String FIRE_DOOR_STATUS_CLOSED = "2";


    /**
     * 添加防火门
     * @param door
     * @return
     */
    @Transactional
    public void addFireDoor(DeviceFireDoor door){
        /**
         * 验证数据
         */
        //缺少参数
        if(door.getBuildingId() == null
                || door.getDoorNormalStatus() == null
                || door.getFloor() == null
                || door.getDoorName() == null
                || door.getPositionDescription() == null
                ){

            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }
        //不合法参数
        if(door.getId() != null
                || (!FIRE_DOOR_STATUS_OPEN.equals(door.getDoorNormalStatus())
                    &&  !FIRE_DOOR_STATUS_CLOSED.equals(door.getDoorNormalStatus()) )
                || door.getPositionSign() != null
                || door.getSensorNum() != null){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }
        DeviceBuilding building = deviceBuildingBiz.getById(door.getBuildingId());
        if(building == null || DELELE_FLAG_YES.equals(building.getDelFlag())){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }

        /**
         * 设置缺省项
         */
        door.setDelFlag(DELELE_FLAG_NO);
        door.setSensorNum(FIRE_DOOR_INIT_SENSOR_NUM);

        //保存数据
        this.insertSelective(door);
    }

    /**
     * 修改防火门
     * @param door
     * @return
     */
    @Transactional
    public void updateFireDoor(DeviceFireDoor door){
        /**
         * 验证数据
         */
        //缺少参数
        if(door.getId() == null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }

        //不合法参数
        DeviceFireDoor dbRecord = deviceFireDoorMapper.selectByPrimaryKey(door.getId());
        if(dbRecord == null
                ||(door.getDoorNormalStatus() != null && !FIRE_DOOR_STATUS_OPEN.equals(door.getDoorNormalStatus())
                &&  !FIRE_DOOR_STATUS_CLOSED.equals(door.getDoorNormalStatus()))){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }

        //设置不更新项
        door.setBuildingId(null);
        door.setSensorNum(null);
        door.setDoorStatus(null);
        door.setPositionSign(null);
        door.setDelFlag(DELELE_FLAG_NO);

        //保存数据　
        this.updateSelectiveById(door);
       // deviceFireDoorMapper.updateByPrimaryKeySelective(door);
    }

    /**
     * 删除防火门
     * @param doorIds
     * @return
     */
    @Transactional
    public void deleteFireDoor(String doorIds){
        if(doorIds.isEmpty()){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }
        DeviceFireDoor  door = new DeviceFireDoor();
        for(String id : doorIds.split(",")){
            Long doorId = Long.valueOf(id);
            door.setId(doorId);
            door.setDelFlag(DELELE_FLAG_YES);

            //删除关联感传器
            List<DeviceSensorFdExt> exts = deviceSensorFdExtMapper.selectSensorExtByDoorId(doorId);
            DeviceSensor sensor = new DeviceSensor();
            sensor.setDelFlag(DELELE_FLAG_YES);
            for(DeviceSensorFdExt ext : exts){
                sensor.setId(ext.getId());
                deviceSensorBiz.updateSelectiveById(sensor);
            }
            deviceSensorFdExtMapper.deleteFireDoorSensorExt(doorId);
            //删除防火门
            deviceFireDoorMapper.updateByPrimaryKeySelective(door);
        }

    }

    /**
     * 防火门列表分页查询
     * @param page
     * @param limit
     * @param door
     * @return
     */
    public TableResultResponse listFireDoor(Integer page,Integer limit,DeviceFireDoor door){
        Page<Object> result = PageHelper.startPage(page,limit,true);
        List<FireDoorVo> list = deviceFireDoorMapper.listFireDoor(door);
        return new TableResultResponse(result.getTotal(),list);
    }


    /**
     * 增加防火门传感器
     * @param fireDoorId  防火门ID
     * @param sensorNo  设备编号
     * @param manufacturer　厂商
     * @param model　型号
     * @param equipmentType　设备类型（系列）
     */
    @Transactional
    public void addSensor(Integer channelId,Long fireDoorId,String sensorNo, String manufacturer,
                           String model,String equipmentType){

        /**
         * 校验参数
         */
        DeviceFireDoor  door = deviceFireDoorMapper.selectByPrimaryKey(fireDoorId);
        if(door == null ||  DELELE_FLAG_YES.equals(door.getDelFlag())){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }

        List<DeviceSensorType> typeList = dstBiz.selectByType(manufacturer,model,equipmentType);

        if(typeList.size() == 0 ){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }else if(typeList.size() > 1){
            throw new RuntimeException(Constants.SENSOR_TYPE_REPEAT);
        }

        if(this.deviceSensorBiz.selectBySensorNo(sensorNo) != null){
            throw new IllegalArgumentException("传感器编号重复");
        }

        /**
         * 组装数据
         */
        DeviceSensor sensor = new DeviceSensor();
        sensor.setBuildingId(door.getBuildingId());
        sensor.setChannelId(channelId);
        sensor.setDelFlag(DELELE_FLAG_NO);
        sensor.setFloor(door.getFloor());
        sensor.setPositionDescription(door.getPositionDescription());
        sensor.setPositionSign(door.getPositionSign());
        sensor.setSensorNo(sensorNo);
        sensor.setSensorTypeId(typeList.get(0).getId());
        //默认设置状态正常
        sensor.setStatus("2");
        //默认数据字段正常
        sensor.setFieldStatus("1");

        DeviceSensorFdExt sensorExt = new DeviceSensorFdExt();
        sensorExt.setDelFlag(DELELE_FLAG_NO);
        sensorExt.setFireDoorId(fireDoorId);

        /**
         * 保存数据
         */
        deviceSensorBiz.saveSensorWithKeyReturn(sensor);
        sensorExt.setId(sensor.getId());
        EntityUtils.setCreatAndUpdatInfo(sensorExt);
        this.deviceSensorFdExtMapper.insertSelective(sensorExt);

        this.deviceFireDoorMapper.increaseSensorNum(door.getId());

    }

    /**
     * 获取防火门所在楼层的平面图
     * @param fireDoorId 防火门ID
     * @return
     */
    public DeviceFloorLayout getFloorLayout(Long fireDoorId){
        DeviceFireDoor  door = deviceFireDoorMapper.selectByPrimaryKey(fireDoorId);
        if(door == null ||  DELELE_FLAG_YES.equals(door.getDelFlag())){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }
        DeviceFloorLayout layout = new DeviceFloorLayout();
        layout.setBuildId(door.getBuildingId());
        layout.setFileFloor(door.getFloor());
        DeviceFloorLayout  realLayout = deviceFloorLayoutBiz.selectOne(layout);
        return realLayout;
    }

    /**
     * @param fireDoorId 防火门ID
     * @param position 防火门位置
     * @return
     */
    @Transactional
    public void signFireDoor(Long fireDoorId,String position){
        DeviceFireDoor  door = deviceFireDoorMapper.selectByPrimaryKey(fireDoorId);
        if(door == null
                ||  DELELE_FLAG_YES.equals(door.getDelFlag())){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }
        DeviceFireDoor newDoor = new DeviceFireDoor();
        newDoor.setId(fireDoorId);
        newDoor.setPositionSign(position);
        deviceFireDoorMapper.updateByPrimaryKeySelective(newDoor);
    }

    /**
     * 防火门传感器列表
     * @param dto
     * @return
     */
    public TableResultResponse  listSensor(Integer page, Integer limit,FireDoorSensorDto dto){
        Page<Object> result = PageHelper.startPage(page,limit,true);
        List<FdSensorVo> list = this.deviceSensorFdExtMapper.listSensor(dto);
        return new TableResultResponse(result.getTotal(),list);
    }

    /**
     * 更新传感器
     * @param sensor
     * @return
     */
    @Transactional
    public void updateFdSensor(FireDoorSensorDto sensor){
        /**
         * 校验参数
         * 1.数据库是否合法的防火门及传感器记录
         * 2.传入的传感器类型是否合法
         */
        if(sensor.getId() == null
                || sensor.getEquipmentType() == null
                || sensor.getManufacturer() == null
                || sensor.getModel() == null){
            throw new IllegalArgumentException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }
        FireDoorSensorDto searchDto = new FireDoorSensorDto();
        searchDto.setId(sensor.getId());
        List<FdSensorVo> list = this.deviceSensorFdExtMapper.listSensor(searchDto);
        if(list.size() == 0){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }else if(list.size() > 1){
            throw new RuntimeException("传感器数据异常");
        }


        if(sensor.getSensorNo() != null){
            DeviceSensor dbSensor = this.deviceSensorBiz.selectBySensorNo(sensor.getSensorNo());
            //当存在编号为所传编号且存在的实体ID与当前更新实体不一致时，提示传感器编号重复。
            if(dbSensor != null && dbSensor.getId().longValue() != sensor.getId().longValue()){
                throw new IllegalArgumentException("传感器编号重复");
            }
        }

        List<DeviceSensorType> typeList = dstBiz.selectByType(sensor.getManufacturer(),sensor.getModel(),sensor.getEquipmentType());
        if(typeList.size() == 0 ){
            throw new IllegalArgumentException(Constants.API_MESSAGE_APP_PARAM_ERROR);
        }else if(typeList.size()>1){
            throw new RuntimeException(Constants.SENSOR_TYPE_REPEAT);
        }

        /**
         * 更新传感器
         */
        DeviceSensor sensorEntity = new DeviceSensor();
        sensorEntity.setId(sensor.getId());
        sensorEntity.setSensorNo(sensor.getSensorNo());
        sensorEntity.setSensorTypeId(typeList.get(0).getId());
        deviceSensorBiz.updateSelectiveById(sensorEntity);
    }

    /**
     * 删除传感器
     * @param sensorIds
     * @return
     */
    @Transactional
    public void deleteFdSensor(String sensorIds){
        String[] ids = sensorIds.split(",");
        DeviceSensor sensor = new DeviceSensor();
        DeviceSensorFdExt ext = new DeviceSensorFdExt();
        for(String id : ids){
            DeviceSensorFdExt dbExt = deviceSensorFdExtMapper.selectByPrimaryKey(Long.valueOf(id));
            if(dbExt == null){
                continue;
            }
            sensor.setId(dbExt.getId());
            sensor.setDelFlag(DELELE_FLAG_YES);
            deviceSensorBiz.updateSelectiveById(sensor);
            ext.setId(Long.valueOf(id));
            ext.setDelFlag(DELELE_FLAG_YES);
            int  delCount = this.deviceSensorFdExtMapper.updateByPrimaryKeySelective(ext);
            //更新防火门传感器个数
            if(delCount == 1){
                 this.deviceFireDoorMapper.decreaseSensorNum(dbExt.getFireDoorId());
            }
        }

    }

    public String getFireDoorNormalStatus(String sensorNo){
        String status = deviceFireDoorMapper.getFireDoorNormalStatus(sensorNo);
        return status == null? "0":status;
    }

    public void updateSensorDataStatus(String sensorNo,String status){
        DeviceSensor dbSensor = deviceSensorBiz.selectBySensorNo(sensorNo);
        if(dbSensor != null){
            if(status == null
                    || (!FIRE_DOOR_STATUS_OPEN.equals(status) && FIRE_DOOR_STATUS_CLOSED.equals(status))){
                status = "0";
            }
            DeviceSensorFdExt dbExt = deviceSensorFdExtMapper.getById(dbSensor.getId());
            if(dbExt == null){
                return;
            }
            //更新传感器数据状态
            this.deviceSensorFdExtMapper.updateDoorStatus(dbSensor.getId(),status);
            //更新防火门数据状态
            List<DeviceSensorFdExt> extList = this.deviceSensorFdExtMapper.selectSensorExtByDoorId(dbExt.getFireDoorId());
            if(extList.size() > 0){
                JSONArray jsonArray = new JSONArray();
                for(DeviceSensorFdExt ext : extList){
                    JSONObject obj = new JSONObject();
                    obj.put("sensorId",ext.getId());
                    obj.put("doorStatus",ext.getDoorStatus());
                    jsonArray.add(obj);
                }
                this.deviceFireDoorMapper.updateDoorStatus(dbExt.getFireDoorId(),jsonArray.toJSONString());
            }
        }

    }
}
