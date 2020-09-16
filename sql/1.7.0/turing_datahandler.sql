/*
Navicat MySQL Data Transfer

Source Server         : TMC图灵线
Source Server Version : 50721
Source Host           : mysql-cn-south-1-cba0ae12d7a54c5b.public.jcloud.com:3306
Source Database       : turing_datahandler

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-07-05 16:38:31
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
  `RESTORE_DATE` datetime DEFAULT NULL COMMENT '恢复时间',
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
  KEY `device_abnormal_alrm_date` (`ALRM_DATE`) USING BTREE,
  KEY `abnormal_index` (`ALRM_CATEGORY`,`HANDLE_FLAG`,`FIRE_FLAG`) USING BTREE,
  KEY `countIndex` (`EQU_ID`,`ALRM_CATEGORY`) USING BTREE,
  KEY `queryPage` (`ALRM_DATE`,`ALRM_CATEGORY`,`LEVEL`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=474441 DEFAULT CHARSET=utf8;

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
  `RESTORE_DATE` datetime DEFAULT NULL COMMENT '恢复时间',
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
  KEY `device_abnormal_alrm_date` (`ALRM_DATE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8 COMMENT='硬件设施异常记录表';

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
  KEY `device_abnormal_alrm_date` (`ALRM_DATE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消防主机异常表';

-- ----------------------------
-- Table structure for notice_log
-- ----------------------------
DROP TABLE IF EXISTS `notice_log`;
CREATE TABLE `notice_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SENSOR_NO` varchar(255) DEFAULT NULL COMMENT '设备编号',
  `NOTICE_TYPE` varchar(255) DEFAULT NULL COMMENT '通知方式：0(APP),1(短信),2(语音电话)',
  `NOTICE_CONTENT` varchar(256) DEFAULT NULL COMMENT '推送内容',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统 关联turing_admin的channel表',
  `USERNAME` varchar(128) DEFAULT NULL COMMENT '接收账号',
  `MOBILE_PHONE` varchar(256) DEFAULT NULL COMMENT '接收手机号',
  `SERVICE_SUPPLY_NAME` varchar(256) NOT NULL COMMENT '服务方',
  `NOTICE_RESULT` varchar(256) DEFAULT NULL COMMENT '通知结果',
  `ALARM_TIME` datetime DEFAULT NULL COMMENT '报警时间',
  `NOTICE_TIME` datetime DEFAULT NULL COMMENT '通知时间',
  `DEL_FLAG` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记 1：删除，0：未删除',
  `CRT_USER_NAME` varchar(128) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL COMMENT '创建者ID',
  `CRT_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPD_USER_NAME` varchar(128) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL COMMENT '修改者ID',
  `UPD_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL COMMENT '部门ID 未使用的字段',
  `TENANT_ID` varchar(32) DEFAULT NULL COMMENT '租户ID',
  `USER_ID` varchar(32) DEFAULT NULL COMMENT '接收用户ID',
  `SENSOR_ID` bigint(20) DEFAULT NULL COMMENT '传感器ID',
  PRIMARY KEY (`ID`,`SERVICE_SUPPLY_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=5109 DEFAULT CHARSET=utf8 COMMENT='通知推送日志 ';

-- ----------------------------
-- Table structure for notice_rule
-- ----------------------------
DROP TABLE IF EXISTS `notice_rule`;
CREATE TABLE `notice_rule` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `RULE_NAME` varchar(128) DEFAULT NULL COMMENT '规则名称',
  `RULE_DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '规则描述',
  `INTERVAL_TIME_TXT` varchar(128) DEFAULT NULL COMMENT '间隔时间文字',
  `INTERVAL_TIME_MINUTES` int(11) DEFAULT NULL COMMENT '间隔时间(分钟)',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统',
  `DEL_FLAG` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记 1：删除，0：未删除',
  `CRT_USER_NAME` varchar(128) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL COMMENT '创建者ID',
  `CRT_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPD_USER_NAME` varchar(128) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL COMMENT '修改者ID',
  `UPD_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL COMMENT '部门ID 未使用的字段',
  `TENANT_ID` varchar(32) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COMMENT='通知推送规则';

-- ----------------------------
-- Table structure for notice_rule_sensor
-- ----------------------------
DROP TABLE IF EXISTS `notice_rule_sensor`;
CREATE TABLE `notice_rule_sensor` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `NOTICE_RULE_ID` bigint(20) DEFAULT NULL COMMENT '通知规则ID',
  `SENSOR_ID` bigint(20) DEFAULT NULL COMMENT '设备ID 设备的ID',
  `CHANNEL_ID` int(11) DEFAULT NULL COMMENT '所属系统ID',
  `DEL_FLAG` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记 1：删除，0：未删除',
  `CRT_USER_NAME` varchar(128) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL COMMENT '创建者ID',
  `CRT_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPD_USER_NAME` varchar(128) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL COMMENT '修改者ID',
  `UPD_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL COMMENT '部门ID 未使用的字段',
  `TENANT_ID` varchar(32) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8 COMMENT='通知规则-设备关联表 规则-设备：1-N关联';

-- ----------------------------
-- Table structure for notice_rule_user
-- ----------------------------
DROP TABLE IF EXISTS `notice_rule_user`;
CREATE TABLE `notice_rule_user` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `NOTICE_RULE_ID` bigint(20) DEFAULT NULL COMMENT '通知规则ID',
  `USER_ID` varchar(128) DEFAULT NULL COMMENT '用户ID',
  `DEL_FLAG` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记 1：删除，0：未删除',
  `CRT_USER_NAME` varchar(128) DEFAULT NULL COMMENT '创建者名称',
  `CRT_USER_ID` varchar(32) DEFAULT NULL COMMENT '创建者ID',
  `CRT_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPD_USER_NAME` varchar(128) DEFAULT NULL COMMENT '修改者名称',
  `UPD_USER_ID` varchar(32) DEFAULT NULL COMMENT '修改者ID',
  `UPD_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `DEPART_ID` varchar(32) DEFAULT NULL COMMENT '部门ID 未使用的字段',
  `TENANT_ID` varchar(32) DEFAULT NULL COMMENT '租户ID',
  `NOTICE_TYPE` char(1) DEFAULT '1' COMMENT '1:报警通知 2:故障通知',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 COMMENT='通知规则-用户关联表 ';
