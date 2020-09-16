/*
Navicat MySQL Data Transfer

Source Server         : TMC图灵线
Source Server Version : 50721
Source Host           : mysql-cn-south-1-cba0ae12d7a54c5b.public.jcloud.com:3306
Source Database       : turing_device

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-07-05 16:38:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for device_abnormal
-- ----------------------------
DROP TABLE IF EXISTS `device_abnormal`;
CREATE TABLE `device_abnormal` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `BUILD_ID` int(11) DEFAULT NULL COMMENT '建筑列表id',
  `EQU_ID` int(11) DEFAULT NULL COMMENT '设备id',
  `ALRM_CATEGORY` char(100) DEFAULT NULL COMMENT '报警类型：0：故障，1：火警',
  `ALRM_TYPE` char(100) DEFAULT NULL COMMENT '报警类型',
  `ALRM_DATE` datetime DEFAULT NULL COMMENT '报警时间',
  `HANDLE_FLAG` char(1) DEFAULT NULL COMMENT '是否处理[1=是/0=否]',
  `FIRE_FLAG` char(1) DEFAULT NULL COMMENT '是否真实火警[2=火警测试/1=是/0=否]',
  `CONFIR_DATE` datetime DEFAULT NULL COMMENT '确认时间',
  `HANDLE_DATE` datetime DEFAULT NULL COMMENT '处理时间',
  `CONFIR_PERSON` varchar(100) DEFAULT NULL COMMENT '确认人',
  `HANDLE_PERSON` varchar(100) DEFAULT NULL COMMENT '处理人',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `ALRM_DATA` double(10,3) DEFAULT NULL COMMENT '传感器数据',
  `LOG_ID` char(64) DEFAULT NULL COMMENT '日志ID',
  `UNIT` varchar(100) DEFAULT NULL COMMENT '测点代号',
  `B_NAME` varchar(200) DEFAULT NULL COMMENT '建筑名称',
  `SENSOR_NO` varchar(50) DEFAULT NULL COMMENT '传感器编号',
  `EQUIPMENT_TYPE` varchar(255) DEFAULT NULL COMMENT '设备类型',
  `FLOOR` int(11) DEFAULT NULL COMMENT '楼层',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `MEASURING_POINT` varchar(100) DEFAULT NULL COMMENT '测点',
  `LEVEL` varchar(20) DEFAULT NULL COMMENT '报警等级',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统id',
  `DATA_UNIT` varchar(100) DEFAULT NULL COMMENT '数据单位',
  PRIMARY KEY (`ID`),
  KEY `device_abnormal_alrm_date` (`ALRM_DATE`),
  KEY `abnormal_index` (`ALRM_CATEGORY`,`HANDLE_FLAG`,`FIRE_FLAG`),
  KEY `countIndex` (`EQU_ID`,`ALRM_CATEGORY`),
  KEY `queryPage` (`ALRM_DATE`,`ALRM_CATEGORY`,`LEVEL`)
) ENGINE=InnoDB AUTO_INCREMENT=473003 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_alarm_level
-- ----------------------------
DROP TABLE IF EXISTS `device_alarm_level`;
CREATE TABLE `device_alarm_level` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `LEVEL` varchar(20) DEFAULT NULL COMMENT '报警等级',
  `COLOR` varchar(26) DEFAULT NULL COMMENT '报警颜色',
  `SORT` int(11) DEFAULT NULL COMMENT '排序',
  `LEVEL_INSTRUCTIONS` varchar(255) DEFAULT NULL COMMENT '等级说明',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_alarm_threshold
-- ----------------------------
DROP TABLE IF EXISTS `device_alarm_threshold`;
CREATE TABLE `device_alarm_threshold` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `MP_ID` int(11) DEFAULT NULL COMMENT '测点id',
  `AL_ID` int(11) DEFAULT NULL COMMENT '报警等级id',
  `ALARM_MIN` double DEFAULT NULL COMMENT '报警最小值',
  `ALARM_MAX` double DEFAULT NULL COMMENT '报警最大值',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `ALARM_SYMBOL_MAX` char(1) DEFAULT NULL COMMENT '预警等级是否包含最大值[0=不包含/1=包含]',
  `ALARM_SYMBOL_MIN` char(1) DEFAULT NULL COMMENT '预警等级是否包含最小值[0=不包含/1=包含]',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_al_dn_relation
-- ----------------------------
DROP TABLE IF EXISTS `device_al_dn_relation`;
CREATE TABLE `device_al_dn_relation` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `AL_ID` int(11) DEFAULT NULL COMMENT '报警等级id',
  `DN_ID` int(11) DEFAULT NULL COMMENT '通知方式id',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_building
-- ----------------------------
DROP TABLE IF EXISTS `device_building`;
CREATE TABLE `device_building` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `BID` int(11) DEFAULT NULL COMMENT '建筑物ID（字段无意义，暂时保留）',
  `OID` int(11) DEFAULT NULL COMMENT '建筑管理单位ID',
  `B_NAME` varchar(200) DEFAULT NULL COMMENT '建筑名称',
  `B_ADDRESS` varchar(200) DEFAULT NULL COMMENT '建筑地址',
  `ZXQY` char(6) DEFAULT NULL COMMENT '6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）',
  `STREET` char(3) DEFAULT NULL COMMENT '3位街道编码（应符合现行国家标准《县以下行政区划代码编码规则》GB10114的规定）',
  `ROAD` varchar(60) DEFAULT NULL COMMENT '路名',
  `MNPH` varchar(20) DEFAULT NULL COMMENT '门弄牌号',
  `LDZ` varchar(20) DEFAULT NULL COMMENT '楼栋幢',
  `ADDRESS_DETAIL` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `GIS` varchar(100) DEFAULT NULL COMMENT '地理坐标经度的十进制表达式',
  `LINKMAN` varchar(50) DEFAULT NULL COMMENT '联系人',
  `LINKPHONE` varchar(50) DEFAULT NULL COMMENT '联系电话',
  `B_STATE` char(1) DEFAULT NULL COMMENT '建筑情况[1=使用中,0未使用]',
  `B_TIME` datetime DEFAULT NULL COMMENT '竣工时间',
  `PROPERT_RIGHT` char(1) DEFAULT NULL COMMENT '建筑产权及使用情况[0=独家产权，独立使用、1=独立产权，多家使用、2=多家产权，多家使用]',
  `B_AREA` double(10,2) DEFAULT NULL COMMENT '建筑面积',
  `B_ZD_AREA` double(10,2) DEFAULT NULL COMMENT '占地面积',
  `B_HIGHT` double(6,2) DEFAULT NULL COMMENT '建筑高度',
  `B_ZC_AREA` double(10,2) DEFAULT NULL COMMENT '标准层面积',
  `UP_FLOOR` int(3) DEFAULT NULL COMMENT '地上层数',
  `UP_FLOOR_AREA` double(10,2) DEFAULT NULL COMMENT '地上面积',
  `UNDER_FLOOR` int(3) DEFAULT NULL COMMENT '地下层数',
  `UNDER_FLOOR_AREA` double(10,2) DEFAULT NULL COMMENT '地下面积',
  `B_STORE` varchar(20) DEFAULT NULL COMMENT '建筑分类',
  `B_STRTURE` varchar(20) DEFAULT NULL COMMENT '建筑结构',
  `B_STRTURE1` varchar(100) DEFAULT NULL COMMENT '建筑其他结构',
  `CTRLROOM_PLACE` varchar(100) DEFAULT NULL COMMENT '消防控制室位置',
  `FIRE_RATE` varchar(10) DEFAULT NULL COMMENT '耐火等级',
  `FIRE_DANGER` varchar(20) DEFAULT NULL COMMENT '火灾危险性',
  `MOSTWORKERR` int(11) DEFAULT NULL COMMENT '最大容纳人数',
  `LIFT_COUNT` int(3) DEFAULT NULL COMMENT '消防电梯数',
  `LIFT_PLACE` varchar(200) DEFAULT NULL COMMENT '消防电梯位置',
  `REFUGE_NUMBER` int(3) DEFAULT NULL COMMENT '避难层数量',
  `REFUGE_AREA` double(10,2) DEFAULT NULL COMMENT '避难层面积',
  `REFUGE_PLACE` varchar(200) DEFAULT NULL COMMENT '避难层位置',
  `USE_KIND` varchar(200) DEFAULT NULL,
  `HAVE_FIREPROOF` char(1) DEFAULT NULL COMMENT '是否有自动消防设施[0=无、1=有]',
  `XFSS` varchar(255) DEFAULT NULL COMMENT '消防设施',
  `XFSS_OTHER` varchar(200) DEFAULT NULL COMMENT '其他消防设施',
  `XFSS_INTACT` char(1) DEFAULT NULL COMMENT '设施完好情况[1=合格；2=不合格]',
  `NEAR_BUILDING` varchar(500) DEFAULT NULL COMMENT '毗邻建筑情况',
  `GEOG_INFO` varchar(200) DEFAULT NULL COMMENT '地理情况',
  `HAVE_CTRLROOM` char(1) DEFAULT NULL COMMENT '消防控制室情况[0=无；1=有]',
  `USE_TYPE` varchar(10) DEFAULT NULL COMMENT '建筑用途分类',
  `SYS_ORGAN_ID` int(11) DEFAULT '-1' COMMENT '消防管辖单位（默认-1）',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_building_image
-- ----------------------------
DROP TABLE IF EXISTS `device_building_image`;
CREATE TABLE `device_building_image` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `BUILDING_NAME` varchar(255) DEFAULT NULL,
  `BUILDING_ID` int(11) DEFAULT NULL,
  `BUILDING_IMAGE_X` double DEFAULT NULL,
  `BUILDING_IMAGE_Y` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='赤松观大屏-建筑物与在平面图的坐标关系表';

-- ----------------------------
-- Table structure for device_check_test_item
-- ----------------------------
DROP TABLE IF EXISTS `device_check_test_item`;
CREATE TABLE `device_check_test_item` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `FACILITIES_TYPE_ID` int(11) DEFAULT NULL COMMENT '设施类型id',
  `ITEM_FLAG` char(1) DEFAULT NULL COMMENT '检查项或检测项[0=检查项/1=检测项]',
  `CHECK_TEST_ITEM` varchar(255) DEFAULT NULL COMMENT '检查检测项',
  `FLAG` char(1) DEFAULT NULL COMMENT '单选或输入[0=单选/1=输入]',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=778 DEFAULT CHARSET=utf8 COMMENT='检查和检测项表';

-- ----------------------------
-- Table structure for device_collecting_device
-- ----------------------------
DROP TABLE IF EXISTS `device_collecting_device`;
CREATE TABLE `device_collecting_device` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `COLLECTING_DEVIC_TYPE_ID` int(11) DEFAULT NULL COMMENT '采集设备类型id',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态[0=故障/1=正常]（需要产品确认值）',
  `NO` varchar(255) DEFAULT NULL COMMENT '编号',
  `NETWORK_FORM` char(1) DEFAULT NULL COMMENT '网络形式 [](需要产品确认值)',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `GEOGRAPHICAL_POSITION_SIGN` varchar(255) DEFAULT NULL COMMENT '地理位置',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_collecting_device_series
-- ----------------------------
DROP TABLE IF EXISTS `device_collecting_device_series`;
CREATE TABLE `device_collecting_device_series` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '类型编号为了统一显示时采用网关设备类型ID',
  `TYPE` varchar(100) DEFAULT NULL COMMENT '采集设备类型',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `COLLECTING_TYPE_ID` int(11) DEFAULT NULL COMMENT '网关设备类型ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_collecting_device_type
-- ----------------------------
DROP TABLE IF EXISTS `device_collecting_device_type`;
CREATE TABLE `device_collecting_device_type` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `EQUIPMENT_TYPE` varchar(255) DEFAULT NULL COMMENT '设备类型',
  `MANUFACTURER` varchar(255) DEFAULT NULL COMMENT '厂商',
  `MODEL` varchar(255) DEFAULT NULL COMMENT '型号',
  `MAINTENANCE_CYCLE_UNIT` char(1) DEFAULT NULL COMMENT '维保周期单位[0=天/1=月/2=年](单位暂定，待产品规定)',
  `MAINTENANCE_CYCLE_VALUE` int(11) DEFAULT NULL COMMENT '维保周期数值',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `MANUFACTURER_ID` int(11) DEFAULT NULL COMMENT '厂商ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_collecting_manufacturer
-- ----------------------------
DROP TABLE IF EXISTS `device_collecting_manufacturer`;
CREATE TABLE `device_collecting_manufacturer` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `COLLECTING_MANUFACTURER` varchar(255) DEFAULT NULL COMMENT '网关厂商',
  `CODE_NAME` varchar(255) DEFAULT NULL COMMENT '网关厂商代号',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_data_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `device_data_dictionary`;
CREATE TABLE `device_data_dictionary` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `PARM_TYPE` varchar(20) DEFAULT NULL COMMENT '参数类别',
  `PARM_NAME` varchar(20) DEFAULT NULL COMMENT '参数说明',
  `PARM_VALUE` varchar(120) DEFAULT NULL COMMENT '参数代号',
  `PARENT_TYPE` varchar(20) DEFAULT NULL COMMENT '父级参数类别',
  `PARENT_VALUE` varchar(120) DEFAULT NULL COMMENT '父级参数代号',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_early_warning
-- ----------------------------
DROP TABLE IF EXISTS `device_early_warning`;
CREATE TABLE `device_early_warning` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `BUILD_ID` int(11) DEFAULT NULL COMMENT '建筑列表id',
  `EQU_ID` int(11) DEFAULT NULL COMMENT '设备id',
  `ALRM_TYPE` char(100) DEFAULT NULL COMMENT '报警类型(预警等级)',
  `ALRM_DATE` datetime DEFAULT NULL COMMENT '报警时间',
  `HANDLE_FLAG` char(1) DEFAULT NULL COMMENT '是否处理[1=是/0=否]',
  `HANDLE_DATE` datetime DEFAULT NULL COMMENT '处理时间',
  `HANDLE_PERSON` varchar(100) DEFAULT NULL COMMENT '处理人',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `ALRM_DATA` double(10,3) DEFAULT NULL COMMENT '传感器数据',
  `LOG_ID` char(64) DEFAULT NULL COMMENT '日志ID',
  `UNIT` varchar(100) DEFAULT NULL COMMENT '测点代号',
  `B_NAME` varchar(200) DEFAULT NULL COMMENT '建筑名称',
  `SENSOR_NO` varchar(50) DEFAULT NULL COMMENT '传感器编号',
  `EQUIPMENT_TYPE` varchar(255) DEFAULT NULL COMMENT '设备类型',
  `FLOOR` int(11) DEFAULT NULL COMMENT '楼层',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `MEASURING_POINT` varchar(100) DEFAULT NULL COMMENT '测点',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统id',
  PRIMARY KEY (`ID`),
  KEY `device_abnormal_alrm_date` (`ALRM_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_facilities_abnormal
-- ----------------------------
DROP TABLE IF EXISTS `device_facilities_abnormal`;
CREATE TABLE `device_facilities_abnormal` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `FIRE_COCK_ID` int(11) DEFAULT NULL COMMENT '消火栓id',
  `EQU_ID` int(11) DEFAULT NULL COMMENT '设备id',
  `ALRM_TYPE` char(100) DEFAULT NULL COMMENT '报警类型',
  `ALRM_DATE` datetime DEFAULT NULL COMMENT '报警时间',
  `HANDLE_FLAG` char(1) DEFAULT NULL COMMENT '是否处理[1=是/0=否]',
  `FAULT_FLAG` char(1) DEFAULT NULL COMMENT '是否故障[1=误报/0=故障]',
  `CONFIR_DATE` datetime DEFAULT NULL COMMENT '确认时间',
  `HANDLE_DATE` datetime DEFAULT NULL COMMENT '处理时间',
  `CONFIR_PERSON` varchar(100) DEFAULT NULL COMMENT '确认人',
  `HANDLE_PERSON` varchar(100) DEFAULT NULL COMMENT '处理人',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `ALRM_DATA` double(10,3) DEFAULT NULL COMMENT '传感器数据',
  `LOG_ID` char(64) DEFAULT NULL COMMENT '日志ID',
  `UNIT` varchar(100) DEFAULT NULL COMMENT '测点代号',
  `DATA_UNIT` varchar(100) DEFAULT NULL COMMENT '数据单位',
  `HYDRANT_NAME` varchar(200) DEFAULT NULL COMMENT '消火栓名称',
  `SENSOR_NO` varchar(50) DEFAULT NULL COMMENT '传感器编号',
  `EQUIPMENT_TYPE` varchar(255) DEFAULT NULL COMMENT '设备类型',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `MEASURING_POINT` varchar(100) DEFAULT NULL COMMENT '测点',
  `LEVEL` varchar(20) DEFAULT NULL COMMENT '报警等级',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统id',
  PRIMARY KEY (`ID`),
  KEY `device_abnormal_alrm_date` (`ALRM_DATE`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8 COMMENT='硬件设施异常记录表';

-- ----------------------------
-- Table structure for device_facilities_type
-- ----------------------------
DROP TABLE IF EXISTS `device_facilities_type`;
CREATE TABLE `device_facilities_type` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `EQUIPMENT_TYPE` varchar(255) DEFAULT NULL COMMENT '设备类型',
  `CHECK_ITEM_COUNT` varchar(255) DEFAULT NULL COMMENT '检查项数',
  `TEST_ITEM_COUNT` varchar(255) DEFAULT NULL COMMENT '检测项数',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='设施类型表';

-- ----------------------------
-- Table structure for device_fire_door
-- ----------------------------
DROP TABLE IF EXISTS `device_fire_door`;
CREATE TABLE `device_fire_door` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `BUILDING_ID` int(11) NOT NULL COMMENT '所属建筑物ID',
  `DOOR_NAME` varchar(100) NOT NULL COMMENT '防火门名称',
  `DOOR_NORMAL_STATUS` char(1) NOT NULL COMMENT '防火门类型：１常开门２常闭门',
  `FLOOR` int(3) NOT NULL COMMENT '所属楼层',
  `POSITION_DESCRIPTION` varchar(100) NOT NULL COMMENT '防火门位置描述',
  `POSITION_SIGN` varchar(50) DEFAULT NULL COMMENT '在楼层平面图中的坐标，以逗号分隔',
  `SENSOR_NUM` int(3) NOT NULL DEFAULT '0' COMMENT '传感器数量',
  `DOOR_STATUS` varchar(200) DEFAULT NULL COMMENT 'JSON:[{''deviceId'':1,''status'':1},{''deviceId'':1,''status'':1}]',
  `DEL_FLAG` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识1表示已删除 0表示正常',
  `CRT_USER_NAME` varchar(100) NOT NULL COMMENT '创建人名称',
  `CRT_USER_ID` varchar(32) NOT NULL COMMENT '创建人用户ID ',
  `CRT_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '更新人姓名',
  `UPD_USER_ID` varchar(32) DEFAULT NULL COMMENT '更新人用户ID',
  `UPD_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DEPART_ID` varchar(32) DEFAULT NULL COMMENT '保留字段',
  `TENANT_ID` varchar(32) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='防火门表';

-- ----------------------------
-- Table structure for device_fire_main
-- ----------------------------
DROP TABLE IF EXISTS `device_fire_main`;
CREATE TABLE `device_fire_main` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SERVER_IP` varchar(200) DEFAULT NULL COMMENT '服务器IP',
  `PORT` varchar(100) DEFAULT NULL COMMENT '端口号',
  `GIS` varchar(100) DEFAULT NULL COMMENT '地理坐标经度的十进制表达式',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8 COMMENT='消防主机表';

-- ----------------------------
-- Table structure for device_fire_main_abnormal
-- ----------------------------
DROP TABLE IF EXISTS `device_fire_main_abnormal`;
CREATE TABLE `device_fire_main_abnormal` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `BUILD_ID` int(11) DEFAULT NULL COMMENT '建筑列表id',
  `FIRE_MAIN_ID` int(11) DEFAULT NULL COMMENT '消防主机id',
  `EQU_ID` int(11) DEFAULT NULL COMMENT '设备id',
  `SERIES` varchar(255) DEFAULT NULL COMMENT '系列',
  `SENSOR_LOOP` varchar(255) DEFAULT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL COMMENT '地址',
  `ALRM_CATEGORY` char(100) DEFAULT NULL COMMENT '报警类型：0：故障，1：火警',
  `ALRM_TYPE` char(100) DEFAULT NULL COMMENT '报警类型',
  `ALRM_DATE` datetime DEFAULT NULL COMMENT '报警时间',
  `HANDLE_FLAG` char(1) DEFAULT NULL COMMENT '是否处理[1=是/0=否]',
  `FIRE_FLAG` char(1) DEFAULT NULL COMMENT '是否真实火警[2=火警测试/1=是/0=否]',
  `HANDLE_DATE` datetime DEFAULT NULL COMMENT '处理时间',
  `HANDLE_PERSON` varchar(100) DEFAULT NULL COMMENT '处理人',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `ALRM_DATA` double(10,3) DEFAULT NULL COMMENT '传感器数据',
  `LOG_ID` char(64) DEFAULT NULL COMMENT '日志ID',
  `B_NAME` varchar(200) DEFAULT NULL COMMENT '建筑名称',
  `FLOOR` int(11) DEFAULT NULL COMMENT '楼层',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统id',
  PRIMARY KEY (`ID`),
  KEY `device_abnormal_alrm_date` (`ALRM_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消防主机异常表';

-- ----------------------------
-- Table structure for device_fire_main_sensor
-- ----------------------------
DROP TABLE IF EXISTS `device_fire_main_sensor`;
CREATE TABLE `device_fire_main_sensor` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `BUILDING_ID` int(11) DEFAULT NULL COMMENT '建筑列表id',
  `FIRE_MAIN_ID` int(11) DEFAULT NULL COMMENT '消防主机id',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统id',
  `SERIES` varchar(255) DEFAULT NULL COMMENT '系列',
  `SENSOR_LOOP` varchar(255) DEFAULT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL COMMENT '地址',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态[0=故障/1=报警/2=正常/3=未启用/4=离线]（需要产品确认）',
  `FLOOR` int(11) DEFAULT NULL COMMENT '楼层',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `POSITION_SIGN` varchar(50) DEFAULT NULL COMMENT '传感器在平面图的位置标记（中间用,号隔开）',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `STATUS_TIME` datetime DEFAULT NULL COMMENT '数据上传时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=286 DEFAULT CHARSET=utf8 COMMENT='消防主机传感器表';

-- ----------------------------
-- Table structure for device_floor_layout
-- ----------------------------
DROP TABLE IF EXISTS `device_floor_layout`;
CREATE TABLE `device_floor_layout` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `BUILD_ID` int(11) DEFAULT NULL COMMENT '建筑ID',
  `FILE_FLOOR` int(3) DEFAULT NULL COMMENT '附件所属楼层',
  `FILE_NAME` varchar(50) DEFAULT NULL COMMENT '文件名',
  `FILE_TYPE` varchar(10) DEFAULT NULL COMMENT '文件类型/后缀名',
  `FILE_PATH` varchar(200) DEFAULT NULL COMMENT '文件路径',
  `MEMO` varchar(100) DEFAULT NULL COMMENT '说明',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '文件删除标记（0未删除，1已删除）',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=260 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_gy_user
-- ----------------------------
DROP TABLE IF EXISTS `device_gy_user`;
CREATE TABLE `device_gy_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(50) NOT NULL COMMENT '用户id',
  `user` varchar(50) NOT NULL COMMENT '客户名',
  `sensorId` varchar(20) NOT NULL COMMENT '设备编号',
  `user_api` varchar(50) NOT NULL COMMENT '用户提供的api',
  `sensorTpye` varchar(30) NOT NULL COMMENT '设备类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_hardware_facilities
-- ----------------------------
DROP TABLE IF EXISTS `device_hardware_facilities`;
CREATE TABLE `device_hardware_facilities` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `OID` int(11) DEFAULT NULL COMMENT '联网单位单位ID',
  `HYDRANT_NAME` varchar(255) DEFAULT NULL COMMENT '消火栓名称',
  `AREA` varchar(255) DEFAULT NULL COMMENT '所属区域',
  `ZXQY` char(6) DEFAULT NULL COMMENT '6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）',
  `FACILITY_TYPE` char(1) DEFAULT NULL COMMENT '设施类型[0=室外消火栓]',
  `HYDRANT_TYPE` char(1) DEFAULT NULL COMMENT '消火栓类型[0=地下式/1=地上式/2=直埋伸缩]',
  `OUTLET` char(1) DEFAULT NULL COMMENT '出水口[0=单口式/1=双口式/2=三出水口式]',
  `OUTLET_TYPE_ONE` char(1) DEFAULT NULL COMMENT '第一出水口类型[0=外螺旋式/1=内扣式]',
  `OUTLET_VALUE_ONE` int(11) DEFAULT NULL COMMENT '第一出水口数值',
  `OUTLET_TYPE_TWO` char(1) DEFAULT NULL COMMENT '第二出水口类型[0=外螺旋式/1=内扣式]',
  `OUTLET_VALUE_TWO` int(11) DEFAULT NULL COMMENT '第二出水口数值',
  `OUTLET_TYPE_THREE` char(1) DEFAULT NULL COMMENT '第三出水口类型[0=外螺旋式/1=内扣式]',
  `OUTLET_VALUE_THREE` int(11) DEFAULT NULL COMMENT '第三出水口数值',
  `PROTECTION_RADIUS` int(11) DEFAULT NULL COMMENT '保护半径',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `WATER_PIPE` char(1) DEFAULT NULL COMMENT '水管道[0=高压给水/1=临时高压给水/2=低压给水]',
  `GIS` varchar(255) DEFAULT NULL COMMENT '地理坐标经度的十进制表达式',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COMMENT='硬件设施表';

-- ----------------------------
-- Table structure for device_indoor_label
-- ----------------------------
DROP TABLE IF EXISTS `device_indoor_label`;
CREATE TABLE `device_indoor_label` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `FACILITIES_TYPE_ID` int(11) DEFAULT NULL COMMENT '设施类型id',
  `BUILDING_ID` int(11) DEFAULT NULL COMMENT '建筑id',
  `FACILITIES_NO` varchar(200) DEFAULT NULL COMMENT '设施编号',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态[0=正常/1=维修中]',
  `FLOOR` int(11) DEFAULT NULL COMMENT '楼层',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `LAST_INSPECTION_TIME` datetime DEFAULT NULL COMMENT '最近巡检时间',
  `QR_CODE_PATH` varchar(200) DEFAULT NULL COMMENT '二维码路径',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `USE_FLAG` char(1) DEFAULT NULL COMMENT '是否生成路线[1=是/0=否]',
  `RESULT_FLAG` char(1) DEFAULT NULL COMMENT '检测结果[1=正常/0=未检测/2=异常3=跳过]',
  `USE_TEST_FLAG` char(1) DEFAULT '0' COMMENT '是否生成维保路线[1=是/0=否]',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=393 DEFAULT CHARSET=utf8 COMMENT='室内标签表';

-- ----------------------------
-- Table structure for device_indoor_record_inspection_results
-- ----------------------------
DROP TABLE IF EXISTS `device_indoor_record_inspection_results`;
CREATE TABLE `device_indoor_record_inspection_results` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `UPLOAD_PICTURE_IDS` varchar(255) DEFAULT NULL COMMENT '上传图片ids',
  `LABEL_ID` int(11) DEFAULT NULL COMMENT '标签id',
  `LEAK_FLAG` char(1) DEFAULT NULL COMMENT '是否漏检[1=是/0=否]',
  `B_NAME` varchar(200) DEFAULT NULL COMMENT '建筑名称',
  `FLOOR` int(11) DEFAULT NULL COMMENT '楼层',
  `FACILITIES_NO` varchar(200) DEFAULT NULL COMMENT '设施编号',
  `EQUIPMENT_TYPE` varchar(255) DEFAULT NULL COMMENT '设施类型',
  `EQUIPMENT_STATUS` varchar(255) DEFAULT NULL COMMENT '设施状态变更',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `INSPECTION_RESULT` char(1) DEFAULT NULL COMMENT '巡检结果[0=正常/1=异常]',
  `PROBLEM_DESCRIPTION` varchar(200) DEFAULT NULL COMMENT '问题描述',
  `HANDLING` char(1) DEFAULT NULL COMMENT '处理方式[0=已自行处理/1=上报维修]',
  `INSPECTION_PERSON` varchar(100) DEFAULT NULL COMMENT '巡检人',
  `INSPECTION_DATE` datetime DEFAULT NULL COMMENT '巡检时间',
  `MOBILE_PHONE` varchar(255) DEFAULT NULL COMMENT '联系电话',
  `PLANNED_COMPLETION_TIME` datetime DEFAULT NULL COMMENT '计划完成时间',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `TASK_ID` int(11) DEFAULT NULL COMMENT '任务id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16060 DEFAULT CHARSET=utf8 COMMENT='室内设施巡检结果记录表';

-- ----------------------------
-- Table structure for device_inspection_route
-- ----------------------------
DROP TABLE IF EXISTS `device_inspection_route`;
CREATE TABLE `device_inspection_route` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ROUTE_NAME` varchar(200) DEFAULT NULL COMMENT '路线名',
  `LABEL_COUNT` varchar(200) DEFAULT NULL COMMENT '巡检设施个数',
  `ROUTE_FLAG` char(1) DEFAULT NULL COMMENT '0=室内路线,1=室外路线',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8 COMMENT='巡检路线表';

-- ----------------------------
-- Table structure for device_inspection_scheme
-- ----------------------------
DROP TABLE IF EXISTS `device_inspection_scheme`;
CREATE TABLE `device_inspection_scheme` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `INSPECTION_ROUTE_ID` int(11) DEFAULT NULL COMMENT '巡检路线id',
  `START_TIME` date DEFAULT NULL COMMENT '开始时间',
  `END_TIME` date DEFAULT NULL COMMENT '开始时间',
  `PATROL_CYCLE` int(3) DEFAULT NULL COMMENT '巡检周期（单位：天）',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `TIME_COUNT` int(11) DEFAULT NULL COMMENT '巡检时间个数',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8 COMMENT='巡检计划表';

-- ----------------------------
-- Table structure for device_inspection_tasks
-- ----------------------------
DROP TABLE IF EXISTS `device_inspection_tasks`;
CREATE TABLE `device_inspection_tasks` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `USER_ID` varchar(36) DEFAULT NULL COMMENT '巡检人员id',
  `INSPECTION_ROUTE_ID` int(11) DEFAULT NULL COMMENT '巡检路线id',
  `INSPECTION_DATE` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '巡检时间',
  `INSPECTION_TIME_PERIOD` varchar(16) DEFAULT NULL COMMENT '巡检时段',
  `PATROL_CYCLE` int(3) DEFAULT NULL COMMENT '巡检时长',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态[0=未接取/1=已接取/2=进行中/3=完成]',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2605 DEFAULT CHARSET=utf8 COMMENT='巡检任务表';

-- ----------------------------
-- Table structure for device_inspection_time
-- ----------------------------
DROP TABLE IF EXISTS `device_inspection_time`;
CREATE TABLE `device_inspection_time` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `INSPECTION_SCHEME_ID` int(11) DEFAULT NULL COMMENT '巡检计划id',
  `INSPECTION_TIME` varchar(16) DEFAULT NULL COMMENT '巡检时段',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=348 DEFAULT CHARSET=utf8 COMMENT='巡检时段表';

-- ----------------------------
-- Table structure for device_item_value
-- ----------------------------
DROP TABLE IF EXISTS `device_item_value`;
CREATE TABLE `device_item_value` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ITEM_ID` int(11) DEFAULT NULL COMMENT '检查项id',
  `RESULTS_ID` int(11) DEFAULT NULL COMMENT '巡检结果记录id',
  `INSPECTION_FLAG` char(1) DEFAULT NULL COMMENT '0=室内设施巡检,1=室外设施巡检',
  `ITEM_VAULE` varchar(255) DEFAULT NULL COMMENT '检查值',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7905 DEFAULT CHARSET=utf8 COMMENT='巡检结果记录对应检测项值表';

-- ----------------------------
-- Table structure for device_measuring_point
-- ----------------------------
DROP TABLE IF EXISTS `device_measuring_point`;
CREATE TABLE `device_measuring_point` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `MEASURING_POINT` varchar(100) DEFAULT NULL COMMENT '测点',
  `CODE_NAME` varchar(100) DEFAULT NULL COMMENT '测点代号',
  `DATA_UNIT` varchar(100) DEFAULT NULL COMMENT '数据单位',
  `MEASURING_POINT_TYPE` char(1) DEFAULT NULL COMMENT '测点类型[0=火警测点/1=监测测点]',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `NORMAL_VALUE_MAX` double DEFAULT NULL COMMENT '测点正常值最大',
  `NORMAL_SYMBOL_MAX` char(1) DEFAULT NULL COMMENT '测点正常值是否包含最大值[0=不包含/1=包含]',
  `NORMAL_VALUE_MIN` double DEFAULT NULL COMMENT '测点正常值最小',
  `NORMAL_SYMBOL_MIN` char(1) DEFAULT NULL COMMENT '测点正常值是否包含最小值[0=不包含/1=包含]',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=221 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_message_notice
-- ----------------------------
DROP TABLE IF EXISTS `device_message_notice`;
CREATE TABLE `device_message_notice` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NOTICE_TYPE` char(1) DEFAULT NULL COMMENT '通知类型[1=报警通知/2=故障通知]',
  `NOTICE_ID` int(11) DEFAULT NULL COMMENT '通知方式表id',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=701 DEFAULT CHARSET=utf8 COMMENT='消息通知方式表';

-- ----------------------------
-- Table structure for device_message_recipients
-- ----------------------------
DROP TABLE IF EXISTS `device_message_recipients`;
CREATE TABLE `device_message_recipients` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NOTICE_TYPE` char(1) DEFAULT NULL COMMENT '通知类型[0=预警通知/1=报警通知/2=故障通知]',
  `MESSAGE_RECIPIENTS_USERID` varchar(255) DEFAULT NULL COMMENT '消息接收人userId',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=581 DEFAULT CHARSET=utf8 COMMENT='消息接收人表';

-- ----------------------------
-- Table structure for device_networking_unit
-- ----------------------------
DROP TABLE IF EXISTS `device_networking_unit`;
CREATE TABLE `device_networking_unit` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键UUID',
  `OID` int(11) DEFAULT NULL COMMENT '单位ID',
  `O_NAME` varchar(100) DEFAULT NULL COMMENT '单位名称',
  `O_LICENSE` varchar(100) DEFAULT NULL COMMENT '统一社会信用代码',
  `O_LICENSE_TIME` datetime DEFAULT NULL COMMENT '单位注册时间',
  `O_ADDRESS` varchar(200) DEFAULT NULL COMMENT '单位地址',
  `XZQY` char(6) DEFAULT NULL COMMENT '6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）',
  `STREET` char(3) DEFAULT NULL COMMENT '3位街道编码（应符合现行国家标准《县以下行政区划代码编码规则》GB10114的规定）',
  `ROAD` varchar(60) DEFAULT NULL COMMENT '路名',
  `MNPH` varchar(200) DEFAULT NULL,
  `LDZ` varchar(20) DEFAULT NULL COMMENT '楼栋幢',
  `ADDRESS_DETAIL` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `GIS` varchar(100) DEFAULT NULL COMMENT '地理坐标经纬度',
  `O_PHONE` varchar(50) DEFAULT NULL COMMENT '单位电话',
  `SAFE_DUTY_NAME` varchar(50) DEFAULT NULL COMMENT '单位消防安全责任人姓名',
  `SAFE_DUTY_PHONE` varchar(50) DEFAULT NULL COMMENT '单位消防安全责任人电话',
  `SAFE_DUTY_IDCARD` varchar(50) DEFAULT NULL COMMENT '单位安全消防责任人身份证号',
  `LEGAL_NAME` varchar(50) DEFAULT NULL COMMENT '企业法人姓名',
  `LEGAL_PHONE` varchar(50) DEFAULT NULL COMMENT '企业法人电话',
  `LEGAL_IDCARD` varchar(50) DEFAULT NULL COMMENT '企业法人身份证号',
  `SAFE_MANAGER_NAME` varchar(50) DEFAULT NULL COMMENT '单位消防安全管理人员姓名',
  `SAFE_MANAGER_PHONE` varchar(50) DEFAULT NULL COMMENT '单位消防安全管理人员电话',
  `SAFE_MANAGER_IDCARD` varchar(50) DEFAULT NULL COMMENT '单位安全消防管理人员身份证号',
  `O_LINKMAN` varchar(50) DEFAULT NULL COMMENT '单位联系人',
  `O_LINKPHONE` varchar(50) DEFAULT NULL COMMENT '单位联系电话',
  `O_TYPE` char(1) DEFAULT NULL COMMENT '单位类别[1=重点单位；2=一般单位；3=九小场所；4=其他单位',
  `O_NATURE` varchar(255) DEFAULT NULL COMMENT '单位性质',
  `O_CLASS` varchar(20) DEFAULT NULL COMMENT '单位类型',
  `KEYUNIT_TIME` datetime DEFAULT NULL COMMENT '确定重点单位时间',
  `IS_KEYUNIT` char(1) DEFAULT NULL COMMENT '是否重点单位[0=否；1=是]',
  `O_OTHER` varchar(200) DEFAULT NULL COMMENT '单位其他情况',
  `CHANGETIME` datetime DEFAULT NULL COMMENT '修改时间',
  `CREATETIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CHANGEACC` varchar(255) DEFAULT NULL,
  `CREATEACC` varchar(255) DEFAULT NULL,
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_notice
-- ----------------------------
DROP TABLE IF EXISTS `device_notice`;
CREATE TABLE `device_notice` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NOTICE` varchar(50) DEFAULT NULL COMMENT '通知方式',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_outdoor_label
-- ----------------------------
DROP TABLE IF EXISTS `device_outdoor_label`;
CREATE TABLE `device_outdoor_label` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `FACILITIES_TYPE_ID` int(11) DEFAULT NULL COMMENT '设施类型id',
  `OID` int(11) DEFAULT NULL COMMENT '联网单位ID',
  `FACILITIES_NO` varchar(200) DEFAULT NULL COMMENT '设施编号',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态[0=正常/1=维修中]',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `LAST_INSPECTION_TIME` datetime DEFAULT NULL COMMENT '最近巡检时间',
  `QR_CODE_PATH` varchar(200) DEFAULT NULL COMMENT '二维码路径',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `POSITION_SIGN` varchar(50) DEFAULT NULL COMMENT '位置标记（中间用,号隔开）',
  `USE_FLAG` char(1) DEFAULT NULL COMMENT '是否生成路线[1=是/0=否]',
  `RESULT_FLAG` char(1) DEFAULT NULL COMMENT '检测结果[1=正常/0=未检测/2=异常3=跳过]',
  `USE_TEST_FLAG` char(1) DEFAULT '0' COMMENT '是否维保生成路线[1=是/0=否]',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8 COMMENT='室外标签表';

-- ----------------------------
-- Table structure for device_outdoor_record_inspection_results
-- ----------------------------
DROP TABLE IF EXISTS `device_outdoor_record_inspection_results`;
CREATE TABLE `device_outdoor_record_inspection_results` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `UPLOAD_PICTURE_IDS` varchar(255) DEFAULT NULL COMMENT '上传图片ids',
  `LABEL_ID` int(11) DEFAULT NULL COMMENT '标签id',
  `LEAK_FLAG` char(1) DEFAULT NULL COMMENT '是否漏检[1=是/0=否]',
  `FACILITIES_NO` varchar(200) DEFAULT NULL COMMENT '设施编号',
  `EQUIPMENT_TYPE` varchar(255) DEFAULT NULL COMMENT '设施类型',
  `EQUIPMENT_STATUS` varchar(255) DEFAULT NULL COMMENT '设施状态变更',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `INSPECTION_RESULT` char(1) DEFAULT NULL COMMENT '巡检结果[0=正常/1=异常]',
  `PROBLEM_DESCRIPTION` varchar(200) DEFAULT NULL COMMENT '问题描述',
  `HANDLING` char(1) DEFAULT NULL COMMENT '处理方式[0=已自行处理/1=上报维修]',
  `INSPECTION_PERSON` varchar(100) DEFAULT NULL COMMENT '巡检人',
  `INSPECTION_DATE` datetime DEFAULT NULL COMMENT '巡检时间',
  `MOBILE_PHONE` varchar(255) DEFAULT NULL COMMENT '联系电话',
  `PLANNED_COMPLETION_TIME` datetime DEFAULT NULL COMMENT '计划完成时间',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `TASK_ID` int(11) DEFAULT NULL COMMENT '任务id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9895 DEFAULT CHARSET=utf8 COMMENT='室外设施巡检结果记录表';

-- ----------------------------
-- Table structure for device_route_label
-- ----------------------------
DROP TABLE IF EXISTS `device_route_label`;
CREATE TABLE `device_route_label` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ROUTE_ID` int(11) DEFAULT NULL COMMENT '路线id',
  `LABEL_ID` int(11) DEFAULT NULL COMMENT '标签id',
  `LABEL_FLAG` char(1) DEFAULT NULL COMMENT '0=室内标签,1=室外标签',
  `ROUTE_FLAG` char(1) DEFAULT NULL COMMENT '0=巡检路线,1=检测路线',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1517 DEFAULT CHARSET=utf8 COMMENT='路线和标签关联表';

-- ----------------------------
-- Table structure for device_sensor
-- ----------------------------
DROP TABLE IF EXISTS `device_sensor`;
CREATE TABLE `device_sensor` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `BUILDING_ID` int(11) DEFAULT NULL COMMENT '建筑列表id',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统id',
  `SENSOR_TYPE_ID` int(11) DEFAULT NULL COMMENT '传感器类型id',
  `CD_ID` int(11) DEFAULT NULL COMMENT '采集设备id(备用字段)',
  `FIELD_STATUS` char(1) DEFAULT NULL COMMENT '数据字段状态[0=不正常/1=正常]（设备，厂商，型号，楼层是否正常）',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态[0=故障/1=报警/2=正常]（需要产品确认）',
  `SENSOR_NO` varchar(200) DEFAULT NULL COMMENT '传感器编号',
  `FLOOR` int(11) DEFAULT NULL COMMENT '楼层',
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '位置描述',
  `POSITION_SIGN` varchar(50) DEFAULT NULL COMMENT '传感器在平面图的位置标记（中间用,号隔开）',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `STATUS_TIME` datetime DEFAULT NULL COMMENT '数据上传时间',
  `HYDRANT_ID` int(11) DEFAULT NULL COMMENT '室外消火栓id',
  PRIMARY KEY (`ID`),
  KEY `sensor_index` (`STATUS`,`SENSOR_NO`,`CHANNEL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5355 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_sensor_fd_ext
-- ----------------------------
DROP TABLE IF EXISTS `device_sensor_fd_ext`;
CREATE TABLE `device_sensor_fd_ext` (
  `ID` bigint(11) NOT NULL,
  `FIRE_DOOR_ID` bigint(11) NOT NULL COMMENT '防火门编号',
  `DOOR_STATUS` char(1) NOT NULL DEFAULT '0' COMMENT '传感器表示的门状态0:非门磁传感器　1:开门状态　2:关闭状态',
  `DEL_FLAG` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除状态。1表示已删除 0表示正常',
  `CRT_USER_NAME` varchar(100) NOT NULL COMMENT '创建人名称',
  `CRT_USER_ID` varchar(32) NOT NULL COMMENT '创建人用户ID ',
  `CRT_TIME` datetime NOT NULL,
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '更新人姓名',
  `UPD_USER_ID` varchar(32) DEFAULT NULL COMMENT '更新人用户ID',
  `UPD_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='防火门传感器扩展信息';

-- ----------------------------
-- Table structure for device_sensor_manufacturer
-- ----------------------------
DROP TABLE IF EXISTS `device_sensor_manufacturer`;
CREATE TABLE `device_sensor_manufacturer` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `SENSOR_MANUFACTURER` varchar(255) DEFAULT NULL COMMENT '传感器厂商',
  `CODE_NAME` varchar(255) DEFAULT NULL COMMENT '传感器厂商代号',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_sensor_mp_relation
-- ----------------------------
DROP TABLE IF EXISTS `device_sensor_mp_relation`;
CREATE TABLE `device_sensor_mp_relation` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `SENSOR_SERIES_ID` int(11) DEFAULT NULL COMMENT '传感器系列id',
  `MP_ID` int(11) DEFAULT NULL COMMENT '测点id',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=744 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_sensor_series
-- ----------------------------
DROP TABLE IF EXISTS `device_sensor_series`;
CREATE TABLE `device_sensor_series` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '类型编号为了统一显示时采用传感器类型ID',
  `COLOR` varchar(20) DEFAULT NULL COMMENT '传感器颜色',
  `SENSOR_TYPE` varchar(100) DEFAULT NULL COMMENT '传感器类型',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `TYPE` char(1) DEFAULT NULL COMMENT '类型[0=室内/1=室外]',
  `SENSOR_TYPE_ID` int(11) DEFAULT NULL COMMENT '传感器类型ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_sensor_temp
-- ----------------------------
DROP TABLE IF EXISTS `device_sensor_temp`;
CREATE TABLE `device_sensor_temp` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `BUILDING_ID` int(11) DEFAULT NULL,
  `CHANNEL_ID` int(11) DEFAULT NULL,
  `SENSOR_TYPE_ID` int(11) DEFAULT NULL,
  `CD_ID` int(11) DEFAULT NULL,
  `FIELD_STATUS` char(1) DEFAULT NULL,
  `STATUS` char(1) DEFAULT NULL,
  `SENSOR_NO` varchar(50) DEFAULT NULL,
  `FLOOR` int(11) DEFAULT NULL,
  `POSITION_DESCRIPTION` varchar(255) DEFAULT NULL,
  `POSITION_SIGN` varchar(50) DEFAULT NULL,
  `DEL_FLAG` char(1) DEFAULT '0',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL,
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP,
  `UPD_USER_NAME` varchar(100) DEFAULT NULL,
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP,
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_sensor_type
-- ----------------------------
DROP TABLE IF EXISTS `device_sensor_type`;
CREATE TABLE `device_sensor_type` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `EQUIPMENT_TYPE` varchar(255) DEFAULT NULL COMMENT '设备类型',
  `MANUFACTURER` varchar(255) DEFAULT NULL COMMENT '厂商',
  `MODEL` varchar(255) DEFAULT NULL COMMENT '型号',
  `DATA_UNIT` varchar(50) DEFAULT NULL COMMENT '数据单位',
  `DATA_ACQUISITION_CYCLE_UNIT` char(1) DEFAULT NULL COMMENT '数据采集周期单位[0=秒/1=分钟/2=小时/3=天/4=月](单位暂定，待产品规定)',
  `DATA_ACQUISITION_CYCLE_VALUE` int(11) DEFAULT NULL COMMENT '数据采集周期数值',
  `DATA_ACQUISITION_CYCLE_SECOND` int(11) DEFAULT NULL COMMENT '数据采集周期转化为秒',
  `ACQUISITION_DELAY_TIME_UNIT` char(1) DEFAULT NULL COMMENT '采集延时时间单位[0=秒/1=分钟/2=小时/3=天/4=月](单位暂定，待产品规定)',
  `ACQUISITION_DELAY_TIME_VALUE` int(11) DEFAULT NULL COMMENT '采集延时时间数值',
  `ACQUISITION_DELAY_TIME_SECOND` int(11) DEFAULT NULL COMMENT '采集延时时间转换为秒',
  `FIRST_LEVEL_ALARM_MIN` int(11) DEFAULT NULL COMMENT '一级报警等级阈值(最小值)',
  `FIRST_LEVEL_ALARM_MAX` int(11) DEFAULT NULL COMMENT '一级报警等级阈值(最大值)',
  `TWO_LEVEL_ALARM_MIN` int(11) DEFAULT NULL COMMENT '二级报警等级阈值(最小值)',
  `TWO_LEVEL_ALARM_MAX` int(11) DEFAULT NULL COMMENT '二级报警等级阈值(最大值)',
  `MAINTENANCE_CYCLE_UNIT` char(1) DEFAULT NULL COMMENT '维保周期单位[0=天/1=月/2=年](单位暂定，待产品规定)',
  `MAINTENANCE_CYCLE_VALUE` int(11) DEFAULT NULL COMMENT '维保周期数值',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  `MANUFACTURER_ID` int(11) DEFAULT NULL COMMENT '厂商ID',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=210 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_tenant_setup
-- ----------------------------
DROP TABLE IF EXISTS `device_tenant_setup`;
CREATE TABLE `device_tenant_setup` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `PLAN_SETUP` char(1) DEFAULT NULL COMMENT '是否启用平面图模式 [0/未启用，1/启用]',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_test_plan
-- ----------------------------
DROP TABLE IF EXISTS `device_test_plan`;
CREATE TABLE `device_test_plan` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `TEST_ROUTE_ID` int(11) DEFAULT NULL COMMENT '检测路线id',
  `START_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始日期',
  `END_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '结束日期',
  `PATROL_CYCLE` int(3) DEFAULT NULL COMMENT '巡检周期（单位：天）',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='检测计划表';

-- ----------------------------
-- Table structure for device_test_route
-- ----------------------------
DROP TABLE IF EXISTS `device_test_route`;
CREATE TABLE `device_test_route` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ROUTE_NAME` varchar(200) DEFAULT NULL COMMENT '路线名',
  `LABEL_COUNT` varchar(200) DEFAULT NULL COMMENT '巡检设施个数',
  `ROUTE_FLAG` char(1) DEFAULT NULL COMMENT '0=室内路线,1=室外路线',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL,
  `TENANT_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='检测路线表';

-- ----------------------------
-- Table structure for device_upload_information
-- ----------------------------
DROP TABLE IF EXISTS `device_upload_information`;
CREATE TABLE `device_upload_information` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `FILE_NAME` varchar(255) DEFAULT NULL,
  `FILE_PATH` varchar(255) DEFAULT NULL,
  `FILE_TYPE` varchar(255) DEFAULT NULL,
  `FILE_SIZE` double DEFAULT NULL,
  `SYSTEM` varchar(255) DEFAULT NULL,
  `DEL_FLAG` varchar(255) DEFAULT NULL,
  `CRT_USER_NAME` varchar(255) DEFAULT NULL,
  `CRT_USER_ID` int(11) DEFAULT NULL,
  `CRT_TIME` datetime DEFAULT NULL,
  `UPD_USER_NAME` varchar(255) DEFAULT NULL,
  `UPD_USER_ID` int(11) DEFAULT NULL,
  `UPD_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2648 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device_video_analysis_solution
-- ----------------------------
DROP TABLE IF EXISTS `device_video_analysis_solution`;
CREATE TABLE `device_video_analysis_solution` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ANALYSIS_SOLUTION_NAME` varchar(255) DEFAULT NULL COMMENT '分析方案名称',
  `ANALYSIS_SOLUTION_CODE` varchar(255) DEFAULT NULL COMMENT '分析方案代号',
  `ANALYSIS_SOLUTION_IMAGE` varchar(255) DEFAULT NULL,
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL COMMENT '创建者ID',
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL COMMENT '更新者ID',
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL COMMENT '部门ID',
  `TENANT_ID` varchar(32) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='视频分析方案';

-- ----------------------------
-- Table structure for device_video_ext
-- ----------------------------
DROP TABLE IF EXISTS `device_video_ext`;
CREATE TABLE `device_video_ext` (
  `ID` bigint(11) NOT NULL COMMENT '主键id',
  `DEVICE_GROUP_ID` int(11) DEFAULT NULL COMMENT '设备组ID',
  `DEVICE_VIDEO_NAME` varchar(255) DEFAULT NULL COMMENT '设备名称',
  `SENSOR_NO` varchar(255) DEFAULT NULL COMMENT '设备序列号',
  `DEVICE_VALIDATE_CODE` varchar(255) DEFAULT NULL COMMENT '设备验证码',
  `SHOW_FLAG` char(1) DEFAULT '0' COMMENT '显示标记[1=是/0=否（default）]',
  `ANALYSIS_SOLUTION_ID` int(11) DEFAULT NULL COMMENT '分析方案ID',
  `VIDEO_LIVE_ADDRESS` longtext COMMENT '设备视频播放地址JSON',
  `ALARM_MSG` varchar(255) DEFAULT NULL COMMENT '告警状态信息JSON',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='视频设备扩展信息';

-- ----------------------------
-- Table structure for device_video_group
-- ----------------------------
DROP TABLE IF EXISTS `device_video_group`;
CREATE TABLE `device_video_group` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `DEVICE_GROUP_NAME` varchar(255) DEFAULT NULL COMMENT '设备组名称',
  `DEVICE_GROUP_IMAGE` varchar(255) DEFAULT NULL COMMENT '设备组平面图URL',
  `DEL_FLAG` char(1) DEFAULT '0' COMMENT '删除标记[1=是/0=否（default）]',
  `CRT_USER_NAME` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL COMMENT '创建者ID',
  `CRT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPD_USER_NAME` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL COMMENT '更新者ID',
  `UPD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL COMMENT '部门ID',
  `TENANT_ID` varchar(32) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 COMMENT='视频设备组';

-- 初始化通知方式
INSERT INTO `device_notice` VALUES ('1', 'APP推送', '0', null, null, '2018-08-09 06:00:54', null, null, '2018-08-09 06:00:54', null, null);
INSERT INTO `device_notice` VALUES ('2', '短信推送', '0', null, null, '2018-08-09 06:11:50', null, null, '2018-08-09 06:11:50', null, null);
INSERT INTO `device_notice` VALUES ('3', '语音电话', '0', null, null, '2018-09-30 15:25:08', null, null, '2018-09-30 15:25:08', null, null);

-- 初始化分析方案
INSERT INTO `turing_device`.`device_video_analysis_solution` (`ID`, `ANALYSIS_SOLUTION_NAME`, `ANALYSIS_SOLUTION_CODE`, `ANALYSIS_SOLUTION_IMAGE`, `CRT_USER_NAME`, `CRT_USER_ID`, `CRT_TIME`, `UPD_USER_NAME`, `UPD_USER_ID`, `UPD_TIME`, `DEPART_ID`, `TENANT_ID`) VALUES ('1', '烟雾/火焰识别', 'FIRE', 'http://file.tmc.turing.ac.cn/solution_image_fire.png', NULL, NULL, '2019-02-21 09:05:21', NULL, NULL, '2019-02-21 09:05:21', NULL, NULL);
INSERT INTO `turing_device`.`device_video_analysis_solution` (`ID`, `ANALYSIS_SOLUTION_NAME`, `ANALYSIS_SOLUTION_CODE`, `ANALYSIS_SOLUTION_IMAGE`, `CRT_USER_NAME`, `CRT_USER_ID`, `CRT_TIME`, `UPD_USER_NAME`, `UPD_USER_ID`, `UPD_TIME`, `DEPART_ID`, `TENANT_ID`) VALUES ('2', '仪表识别', 'METER', '仪表图片', NULL, NULL, '2019-03-01 15:33:41', NULL, NULL, '2019-03-01 15:33:41', NULL, NULL);
