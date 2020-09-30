/*
 *
 *  *  Copyright (C) 2018  Wanghaobin<463540703@qq.com>
 *
 *  *  This program is free software; you can redistribute it and/or modify
 *  *  it under the terms of the GNU General Public License as published by
 *  *  the Free Software Foundation; either version 2 of the License, or
 *  *  (at your option) any later version.
 *
 *  *  This program is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  GNU General Public License for more details.
 *
 *  *  You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package cn.turing.firecontrol.device.config;

import cn.turing.firecontrol.common.data.IUserDepartDataService;
import cn.turing.firecontrol.common.data.MybatisDataInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author hanyong
 * @create 2018/2/11.
 */
@Configuration
public class DepartDataConfig {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private IUserDepartDataService userDepartDataService;

    @PostConstruct
    public void init(){
        sqlSessionFactory.getConfiguration().addInterceptor(new MybatisDataInterceptor(userDepartDataService,"cn.turing.firecontrol.device.mapper.DeviceSensorMapper.selectBySensorNo",
                "cn.turing.firecontrol.device.mapper.DeviceSensorMapper.getAllIgnoreTenantSensorNo",
                "cn.turing.firecontrol.device.mapper.DeviceBuildingMapper.getById", "cn.turing.firecontrol.device.mapper.DeviceSensorTypeMapper.getById","cn.turing.firecontrol.device.mapper.DeviceSensorMapper.updateStatus",
                "cn.turing.firecontrol.device.mapper.DeviceSensorSeriesMapper.selectBySensorType","cn.turing.firecontrol.device.mapper.DeviceAlarmThresholdMapper.selectByAlrmData",
                "cn.turing.firecontrol.device.mapper.DeviceMessageNoticeMapper.selectByNoticeType","cn.turing.firecontrol.device.mapper.DeviceMessageRecipientsMapper.selectByNotictType",
                "cn.turing.firecontrol.device.mapper.DeviceAlDnRelationMapper.selectByAlarmLevelId","cn.turing.firecontrol.device.mapper.DeviceNetworkingUnitMapper.getById",
                "cn.turing.firecontrol.device.mapper.DeviceSensorMapper.getById","cn.turing.firecontrol.device.mapper.DeviceHardwareFacilitiesMapper.getById",
                "cn.turing.firecontrol.device.mapper.DeviceFireMainMapper.selectIgnoreTenantByCount",
                "cn.turing.firecontrol.device.mapper.DeviceFireMainMapper.getIgnoreTenantAll",
                "cn.turing.firecontrol.device.mapper.DeviceFireMainSensorMapper.selectIgnoreTenantIpAndPortAndSensor",
                "cn.turing.firecontrol.device.mapper.DeviceFireMainSensorMapper.updateSensorStatus","cn.turing.firecontrol.device.mapper.DeviceFireMainMapper.getById",
                "cn.turing.firecontrol.device.mapper.DeviceInspectionRouteMapper.getById","cn.turing.firecontrol.device.mapper.DeviceRouteLabelMapper.getByRouteId",
                "cn.turing.firecontrol.device.mapper.DeviceOutdoorLabelMapper.getById","cn.turing.firecontrol.device.mapper.DeviceIndoorLabelMapper.getById",
                "cn.turing.firecontrol.device.mapper.DeviceUploadInformationMapper.selectByIdTemp","cn.turing.firecontrol.device.mapper.DeviceInspectionTasksMapper.getById",
                "cn.turing.firecontrol.device.mapper.DeviceFireDoorMapper.getFireDoorNormalStatus",
                "cn.turing.firecontrol.device.mapper.DeviceFireDoorMapper.updateDoorStatus",
                "cn.turing.firecontrol.device.mapper.DeviceSensorFdExtMapper.getById",
                "cn.turing.firecontrol.device.mapper.DeviceSensorFdExtMapper.updateDoorStatus",
                "cn.turing.firecontrol.device.mapper.DeviceSensorFdExtMapper.selectSensorExtByDoorId",
                "cn.turing.firecontrol.device.mapper.DeviceIndoorRecordInspectionResultsMapper.selectResultTrend",
                "cn.turing.firecontrol.device.mapper.DeviceIndoorRecordInspectionResultsMapper.selectAbnormalList"

        ));
//        sqlSessionFactory.getConfiguration().addInterceptor(new TenantMybatisInterceptor());
    }
}
