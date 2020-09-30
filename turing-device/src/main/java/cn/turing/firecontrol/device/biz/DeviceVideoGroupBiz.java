package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.entity.DeviceVideoGroup;
import cn.turing.firecontrol.device.mapper.DeviceVideoGroupMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created on 2019/02/20 11:32
 *
 * @Description
 * @Version V1.0
 */
@Service
public class DeviceVideoGroupBiz extends BusinessBiz<DeviceVideoGroupMapper, DeviceVideoGroup> {

    @Autowired
    private DeviceVideoGroupMapper deviceVideoGroupMapper;
    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;

    /**
     * 重写插入方法，增加参数校验和重名判断
     * @param entity
     */
    @Override
    public void insertSelective(DeviceVideoGroup entity) {
        ValidatorUtils.validateEntity(entity);
        List<DeviceVideoGroup> existGroups = deviceVideoGroupMapper.selectByGroupName(entity.getDeviceGroupName());
        if(!existGroups.isEmpty()){
            throw new ParamErrorException("设备组名称已存在");
        }
        super.insertSelective(entity);
    }

    /**
     * 更新设备组
     * @param entity
     */
    @Override
    public void updateSelectiveById(DeviceVideoGroup entity) {
        Integer groupId = entity.getId();
        if(groupId == null){
            throw new ParamErrorException("设备组ID不能为空");
        }
        DeviceVideoGroup group = super.selectById(entity.getId());
        if(group == null){
            throw new ParamErrorException("设备组不存在");
        }
        //更新前有图片，更新后无图片，则删除关联的传感器的位置标记
        if(StringUtils.isNotBlank(group.getDeviceGroupImage()) && StringUtils.isBlank(entity.getDeviceGroupImage())){
            mapper.removePositionSignByGroup(group.getId());
        }
        super.updateSelectiveById(entity);
    }

    /**
     * 重写删除方法，删除设备组前先删除组下面的设备
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Object id) {
        if(id == null){
            throw new RuntimeException("设备组ID不能为空");
        }
        DeviceVideoGroup group = selectById(id);
        if(group == null){
            throw new RuntimeException("设备组不存在");
        }
        //1、删除设备组下面的设备
        DeviceVideoExt device = new DeviceVideoExt();
        device.setDeviceGroupId(group.getId());
        List<DeviceVideoExt> devices = deviceVideoExtBiz.queryOnlyExt(device);
        for(DeviceVideoExt d : devices){
            deviceVideoExtBiz.deleteById(d.getId());
        }
        //2、逻辑删除设备组
        group.setDelFlag("1");
        updateSelectiveById(group);
    }

    @Override
    public List<DeviceVideoGroup> selectListAll() {
        DeviceVideoGroup deviceVideoGroup = new DeviceVideoGroup();
        deviceVideoGroup.setDelFlag("0");
        return super.selectList(deviceVideoGroup);
    }

    @Override
    public DeviceVideoGroup selectById(Object id) {
        DeviceVideoGroup entity = super.selectById(id);
        if(entity !=null && "1".equals(entity.getDelFlag())){
            return null;
        }
        return entity;
    }

    /**
     * 分页查询设备组
     * @param page
     * @param limit
     * @return
     */
    public TableResultResponse<DeviceVideoGroup> queryByPage(Integer page, Integer limit){
        Page<DeviceVideoGroup> pageData = PageHelper.startPage(page,limit);
        selectListAll();
        return new TableResultResponse<>(pageData.getTotal(),pageData.getResult());
    }

    /**
     * 查询所有设备组及组下面的设备树形结构数据
     * @param deviceNoOrName 设备编号或名称搜索关键字
     * @return
     */
    public List<Map<String,Object>> queryDeviceTree(String deviceNoOrName,String groupId,Boolean hasSolution,String sensorNo,String deviceName){
        return mapper.queryDeviceTree(deviceNoOrName,groupId,hasSolution,sensorNo,deviceName);
    }

    /**
     * 查询设备编号对应的设备组
     * @param sensorNos
     * @return
     */
    public Map<String,Map<String,Object>> queryBySensorNos(List<String> sensorNos){
        Map<String,Map<String,Object>> res =Maps.newHashMap();
        if(sensorNos == null || sensorNos.isEmpty()){
            return res;
        }
        List<Map<String,Object>> list = mapper.queryBySensorNos(sensorNos);
        for(Map<String,Object> m : list){
            res.put(m.get("sensorNo").toString(),m);
        }
        return res;
    }
}
