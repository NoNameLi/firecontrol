/*
Navicat MySQL Data Transfer

Source Server         : TMC图灵线
Source Server Version : 50721
Source Host           : mysql-cn-south-1-cba0ae12d7a54c5b.public.jcloud.com:3306
Source Database       : turing_admin

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-07-05 16:06:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for base_channel_tenant
-- ----------------------------
DROP TABLE IF EXISTS `base_channel_tenant`;
CREATE TABLE `base_channel_tenant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `channel_id` varchar(11) DEFAULT NULL,
  `tenant_id` varchar(255) DEFAULT NULL,
  `crt_user_name` varchar(255) DEFAULT NULL,
  `crt_user_id` varchar(255) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `upd_user_name` varchar(255) DEFAULT NULL,
  `upd_user_id` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `depart_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2283 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for base_depart
-- ----------------------------
DROP TABLE IF EXISTS `base_depart`;
CREATE TABLE `base_depart` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '组织名称',
  `parent_id` varchar(36) DEFAULT NULL COMMENT '上级节点',
  `code` varchar(100) DEFAULT NULL COMMENT '编码',
  `path` varchar(4000) DEFAULT NULL COMMENT '路劲',
  `type` varchar(36) DEFAULT NULL COMMENT '部门类型',
  `crt_user_name` varchar(255) DEFAULT NULL COMMENT '创建人',
  `crt_user_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',
  `crt_time` datetime DEFAULT NULL COMMENT '创建时间',
  `upd_user_name` varchar(255) DEFAULT NULL COMMENT '最后更新人',
  `upd_user_id` varchar(36) DEFAULT NULL COMMENT '最后更新人ID',
  `upd_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_code` (`code`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_depart_user
-- ----------------------------
DROP TABLE IF EXISTS `base_depart_user`;
CREATE TABLE `base_depart_user` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `depart_id` varchar(36) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_element
-- ----------------------------
DROP TABLE IF EXISTS `base_element`;
CREATE TABLE `base_element` (
  `id` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '资源编码',
  `type` varchar(255) DEFAULT NULL COMMENT '资源类型',
  `name` varchar(255) DEFAULT NULL COMMENT '资源名称',
  `uri` varchar(255) DEFAULT NULL COMMENT '资源路径',
  `menu_id` varchar(255) DEFAULT NULL COMMENT '资源关联菜单',
  `parent_id` varchar(255) DEFAULT NULL,
  `path` varchar(2000) DEFAULT NULL COMMENT '资源树状检索路径',
  `method` varchar(10) DEFAULT NULL COMMENT '资源请求类型',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `crt_time` datetime DEFAULT NULL,
  `crt_user` varchar(255) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `crt_host` varchar(255) DEFAULT NULL,
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_group
-- ----------------------------
DROP TABLE IF EXISTS `base_group`;
CREATE TABLE `base_group` (
  `id` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '角色编码',
  `name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `parent_id` varchar(36) NOT NULL COMMENT '上级节点',
  `path` varchar(2000) DEFAULT NULL COMMENT '树状关系',
  `type` char(1) DEFAULT NULL COMMENT '类型',
  `group_type` varchar(36) NOT NULL COMMENT '角色组类型',
  `description` varchar(255) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_user_id` varchar(255) DEFAULT NULL,
  `crt_user_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_user_id` varchar(255) DEFAULT NULL,
  `upd_user_name` varchar(255) DEFAULT NULL,
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_group_leader
-- ----------------------------
DROP TABLE IF EXISTS `base_group_leader`;
CREATE TABLE `base_group_leader` (
  `id` varchar(36) NOT NULL,
  `group_id` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_user` varchar(255) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `crt_host` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_user` varchar(255) DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `upd_host` varchar(255) DEFAULT NULL,
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_group_member
-- ----------------------------
DROP TABLE IF EXISTS `base_group_member`;
CREATE TABLE `base_group_member` (
  `id` varchar(36) NOT NULL,
  `group_id` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_user` varchar(255) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `crt_host` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_user` varchar(255) DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `upd_host` varchar(255) DEFAULT NULL,
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_group_type
-- ----------------------------
DROP TABLE IF EXISTS `base_group_type`;
CREATE TABLE `base_group_type` (
  `id` varchar(32) NOT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '编码',
  `name` varchar(255) DEFAULT NULL COMMENT '类型名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `crt_time` datetime DEFAULT NULL COMMENT '创建时间',
  `crt_user` varchar(255) DEFAULT NULL COMMENT '创建人ID',
  `crt_name` varchar(255) DEFAULT NULL COMMENT '创建人',
  `crt_host` varchar(255) DEFAULT NULL COMMENT '创建主机',
  `upd_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `upd_user` varchar(255) DEFAULT NULL COMMENT '最后更新人ID',
  `upd_name` varchar(255) DEFAULT NULL COMMENT '最后更新人',
  `upd_host` varchar(255) DEFAULT NULL COMMENT '最后更新主机',
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_menu
-- ----------------------------
DROP TABLE IF EXISTS `base_menu`;
CREATE TABLE `base_menu` (
  `id` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '路径编码',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `parent_id` varchar(36) NOT NULL COMMENT '父级节点',
  `href` varchar(255) DEFAULT NULL COMMENT '资源路径',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `type` char(10) DEFAULT NULL,
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `path` varchar(500) DEFAULT NULL COMMENT '菜单上下级关系',
  `enabled` char(1) DEFAULT NULL COMMENT '启用禁用',
  `crt_time` datetime DEFAULT NULL,
  `crt_user` varchar(255) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `crt_host` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_user` varchar(255) DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `upd_host` varchar(255) DEFAULT NULL,
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL COMMENT 'Channer表id，所属栏目，如果为空，则不属于栏目菜单',
  `attr3` varchar(255) DEFAULT NULL COMMENT '是否为超级管理员菜单：1是，0否',
  `attr4` varchar(255) DEFAULT NULL COMMENT '是否为站点菜单：1是，0否',
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_position
-- ----------------------------
DROP TABLE IF EXISTS `base_position`;
CREATE TABLE `base_position` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '职位',
  `code` varchar(100) DEFAULT NULL COMMENT '编码',
  `depart_id` varchar(36) DEFAULT NULL COMMENT '部门ID',
  `type` varchar(36) DEFAULT NULL COMMENT '类型',
  `crt_user_name` varchar(255) DEFAULT NULL COMMENT '创建人',
  `crt_user_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',
  `crt_time` datetime DEFAULT NULL COMMENT '创建时间',
  `upd_user_name` varchar(255) DEFAULT NULL COMMENT '最后更新人',
  `upd_user_id` varchar(36) DEFAULT NULL COMMENT '最后更新人ID',
  `upd_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_code` (`code`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_position_depart
-- ----------------------------
DROP TABLE IF EXISTS `base_position_depart`;
CREATE TABLE `base_position_depart` (
  `id` varchar(36) NOT NULL,
  `position_id` varchar(36) DEFAULT NULL,
  `depart_id` varchar(36) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_position_group
-- ----------------------------
DROP TABLE IF EXISTS `base_position_group`;
CREATE TABLE `base_position_group` (
  `id` varchar(36) NOT NULL,
  `position_id` varchar(36) DEFAULT NULL,
  `group_id` varchar(36) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_position_user
-- ----------------------------
DROP TABLE IF EXISTS `base_position_user`;
CREATE TABLE `base_position_user` (
  `id` varchar(36) NOT NULL,
  `position_id` varchar(36) DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_resource_authority
-- ----------------------------
DROP TABLE IF EXISTS `base_resource_authority`;
CREATE TABLE `base_resource_authority` (
  `id` varchar(36) NOT NULL,
  `authority_id` varchar(255) DEFAULT NULL COMMENT '角色ID',
  `authority_type` varchar(255) DEFAULT NULL COMMENT '角色类型',
  `resource_id` varchar(255) DEFAULT NULL COMMENT '资源ID',
  `resource_type` varchar(255) DEFAULT NULL COMMENT '资源类型',
  `parent_id` varchar(255) DEFAULT NULL,
  `path` varchar(2000) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_user` varchar(255) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `crt_host` varchar(255) DEFAULT NULL,
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  `type` varchar(1) DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for base_tenant
-- ----------------------------
DROP TABLE IF EXISTS `base_tenant`;
CREATE TABLE `base_tenant` (
  `id` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '编码',
  `name` varchar(255) DEFAULT NULL COMMENT '最后更新时间',
  `crt_user_name` varchar(255) DEFAULT NULL COMMENT '创建人',
  `crt_user_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',
  `crt_time` datetime DEFAULT NULL COMMENT '创建时间',
  `upd_user_name` varchar(255) DEFAULT NULL COMMENT '最后更新人',
  `upd_user_id` varchar(36) DEFAULT NULL COMMENT '最后更新人ID',
  `upd_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `attr1` varchar(255) DEFAULT NULL COMMENT '域名',
  `attr2` varchar(255) DEFAULT NULL COMMENT 'Logo图片路径',
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `is_super_tenant` varchar(1) DEFAULT NULL COMMENT '是否超级租户',
  `tenant_id` varchar(36) DEFAULT NULL,
  `owner` varchar(36) DEFAULT NULL COMMENT '拥有者',
  `is_deleted` char(1) DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ----------------------------
-- Table structure for base_user
-- ----------------------------
DROP TABLE IF EXISTS `base_user`;
CREATE TABLE `base_user` (
  `id` varchar(36) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `birthday` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `mobile_phone` varchar(255) DEFAULT NULL,
  `tel_phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `sex` varchar(16) DEFAULT NULL,
  `type` char(1) DEFAULT NULL,
  `status` char(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_user_id` varchar(255) DEFAULT NULL,
  `crt_user_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_user_id` varchar(255) DEFAULT NULL,
  `upd_user_name` varchar(255) DEFAULT NULL,
  `attr1` varchar(255) DEFAULT NULL COMMENT '是否为租户管理员',
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  `is_deleted` char(1) DEFAULT NULL COMMENT '是否删除',
  `is_disabled` char(1) DEFAULT NULL COMMENT '是否作废',
  `depart_id` varchar(36) DEFAULT NULL COMMENT '默认部门',
  `is_super_admin` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_usernane` (`username`) USING BTREE,
  KEY `findByUsername` (`username`,`is_deleted`,`is_disabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for channel
-- ----------------------------
DROP TABLE IF EXISTS `channel`;
CREATE TABLE `channel` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父栏目的ID，如果为顶级栏目，则为-1',
  `CHANNEL_TYPE` varchar(100) DEFAULT NULL COMMENT '栏目类型(0:消防系统 1：其他)',
  `CHANNEL_NAME` varchar(50) DEFAULT NULL COMMENT '系统名',
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for gate_log
-- ----------------------------
DROP TABLE IF EXISTS `gate_log`;
CREATE TABLE `gate_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户Id',
  `menu` varchar(255) DEFAULT NULL COMMENT '菜单',
  `opt` varchar(255) DEFAULT NULL COMMENT '操作',
  `uri` varchar(255) DEFAULT NULL COMMENT '资源路径',
  `crt_time` datetime DEFAULT NULL COMMENT '操作时间',
  `crt_user` varchar(255) DEFAULT NULL COMMENT '操作人ID',
  `crt_name` varchar(255) DEFAULT NULL COMMENT '操作人',
  `crt_host` varchar(255) DEFAULT NULL COMMENT '操作主机',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=812 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `LOGIN_USER_ID` varchar(32) DEFAULT NULL,
  `LOGIN_USER_NAME` varchar(50) DEFAULT NULL COMMENT '登陆用户名',
  `LOGIN_REGION` varchar(255) DEFAULT NULL COMMENT '登陆地区',
  `LOGIN_IP` varchar(50) DEFAULT NULL COMMENT '登陆ip',
  `LOGIN_TIME` datetime DEFAULT NULL COMMENT '登陆时间',
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
) ENGINE=InnoDB AUTO_INCREMENT=1504571 DEFAULT CHARSET=utf8;





-- ----------------------------
-- Records of base_menu 初始化菜单
-- ----------------------------
INSERT INTO `base_menu` VALUES ('0b6c33ef26c848e7937df8d13d56024b', 'videoDeviceManger', '设备管理', '6bca1b6478b94c1eb508bcd6d5666185', '/device/videoDeviceManger', 'erjicaidan', 'menu', '3', null, '/videoSurveillance/fireDeviceManger', null, '2018-09-01 17:08:36', '1', 'Mr.AG', null, '2019-04-19 18:33:00', '1', 'Mr.AG', null, '_import(\'device/videoDeviceManger/index\')', '9', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('1', 'userManager', '用户管理', '13', '/admin/user', 'erjicaidan', 'menu', '1', '', '/adminSys/baseManager/userManager', null, null, null, null, null, '2018-08-02 16:44:40', '1', 'Mr.AG', '127.0.0.1', '_import(\'admin/user/index\')', null, '1', '1', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('12d77d4d79604ef9945b5d8ff10b6891', 'realtimeMonit', '实时监测', 'a7a931a5e33b4b08bab45a746582f849', '/device/realtimeMonit', 'erjicaidan', 'menu', '0', null, '/evacuation/realtimeMonit', null, '2018-09-01 17:09:22', '1', 'Mr.AG', null, '2018-09-01 17:09:22', '1', 'Mr.AG', null, '_import(\'device/realtimeMonit/index\')', '8', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('13', 'adminSys', '系统管理', '-1', '/base', 'xitongshezhi', 'dirt', '10', '', '/adminSys', null, null, null, null, null, '2018-09-01 16:54:12', '1', 'Mr.AG', '127.0.0.1', 'Layout', null, '1', '1', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('1481ba40bf174b3482c1adbb32a108ea', 'deviceManger', '网关设备', '13', '/device/deviceManger', 'erjicaidan', 'menu', '0', null, '/adminSys/deviceManger', null, '2018-08-09 16:33:12', '1', 'Mr.AG', null, '2018-10-31 14:21:26', '1', 'Mr.AG', null, '_import(\'device/deviceManger/index\')', null, '0', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('189e319c045241ac930191eca715b1e3', 'facilityTypeManger', '设施类型管理', '13', '/device/facilityTypeManger', 'erjicaidan', 'menu', '0', null, '/adminSys/facilityTypeManger', null, '2018-11-26 11:58:45', '1', 'Mr.AG', null, '2018-11-26 12:07:16', '1', 'Mr.AG', null, '_import(\'device/facilityTypeManger/index\')', null, '1', '0', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('202f4330ddcd4c1fb19541250bd1d3cf', 'statisticAnly', '统计分析', '4436ea9172fa42a3895b0986ffe93469', '/device/statisticAnly', 'erjicaidan', 'menu', '1', null, '/adminSys/gailan/hydrant/statisticAnly', null, '2018-08-27 20:59:38', '1', 'Mr.AG', null, '2018-08-27 21:03:15', '1', 'Mr.AG', null, '_import(\'device/statisticAnly/index\')', '5', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('2096da25e3fa421384ec9768eb5d6072', 'realtimeMonit', '实时监测', '4436ea9172fa42a3895b0986ffe93469', '/device/realtimeMonit', 'erjicaidan', 'menu', '0', null, '/adminSys/gailan/hydrant/realtimeMonit', null, '2018-08-27 20:57:28', '1', 'Mr.AG', null, '2018-08-27 20:57:28', '1', 'Mr.AG', null, '_import(\'device/realtimeMonit/index\')', '5', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('27', 'gateLogManager', '登录日志', '13', '/admin/gateLog', 'erjicaidan', 'menu', '6', '', '/adminSys/baseManager/gateLogManager', null, '2017-07-01 00:00:00', '1', 'admin', '0:0:0:0:0:0:0:1', '2018-08-15 13:38:26', '1', 'Mr.AG', '127.0.0.1', '_import(\'admin/gateLog/index\')', null, '1', '1', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('2c1ead6b918146fba9de0ca5d1137969', 'overView', '概览', 'd9e82ba6c8cb4ec8b0b73f928c1c3c36', '/device/overView/index', 'erjicaidan', 'menu', '0', null, '/adminSys/gailan/realtimeMonit', null, '2018-08-16 10:40:58', '1', 'Mr.AG', null, '2018-11-01 16:37:38', '1', 'Mr.AG', null, '_import(\'device/overView/index\')', null, '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('2e1e2e5001034c1f9a792531378e9970', 'exceptionRecord', '异常记录', '3fd4129de7794716a549b15b368efd8a', '/device/exceptionRecord', 'erjicaidan', 'menu', '1', null, '/firedoor/exceptionRecord', null, '2018-09-01 17:01:10', '1', 'Mr.AG', null, '2018-09-01 17:01:10', '1', 'Mr.AG', null, '_import(\'device/exceptionRecord/index\')', '7', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('2efea8c77b6a4fdbaf10bc55fe2a7ce4', 'firmManager', '厂商管理', '13', '/admin/firmManager', 'erjicaidan', 'menu', '7', null, '/adminSys/firmManager', null, '2018-09-14 11:38:11', '1', 'Mr.AG', null, '2018-09-14 11:38:11', '1', 'Mr.AG', null, '_import(\'admin/firm/index\')', null, '1', '0', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('32ae9e1e019043b9b4a763b616fddd91', 'dianqihuozai', '电气火灾', '-1', '/dianqihuozai', 'dianqihuozai', 'menu', '1', null, '/adminSys/dianqihuozai', null, '2018-08-02 05:41:10', '1', 'Mr.AG', null, '2018-08-03 04:47:07', '1', 'Mr.AG', null, '_import(\'device/deviceMeasuringPoint/index\')', '2', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('343d5793a4b8445abf5b1bae064a66fd', 'statisticAnly', '统计分析', '394aea58ee1b4b75a14b1f1e72ecbfe3', '/device/statisticAnly', 'erjicaidan', null, '2', null, '/fireMainframe/statisticAnly', null, '2018-11-08 14:39:16', '1', 'Mr.AG', null, '2018-11-08 14:39:16', '1', 'Mr.AG', null, '_import(\'device/statisticAnly/index\')', '11', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('34d08e936864418492a0d32d2780227f', 'huozaibaojing', '无线感烟', '-1', null, 'huozaibaojing', null, '2', null, '/adminSys/huozaibaojing', null, '2018-08-02 05:41:31', '1', 'Mr.AG', null, '2018-11-29 09:32:54', '1', 'Mr.AG', null, null, '1', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('35d86f753f1049ebb6ee68d42bcd5e50', 'exceptionRecord', '异常记录', '4436ea9172fa42a3895b0986ffe93469', '/device/exceptionRecord', 'erjicaidan', 'menu', '0', null, '/adminSys/gailan/hydrant/exceptionRecord', null, '2018-08-27 20:58:41', '1', 'Mr.AG', null, '2018-08-27 20:58:41', '1', 'Mr.AG', null, '_import(\'device/exceptionRecord/index\')', '5', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('394aea58ee1b4b75a14b1f1e72ecbfe3', 'fireMainframe', '消防主机', '-1', null, 'zhuji', null, '5', null, '/fireMainframe', null, '2018-11-08 09:48:55', '1', 'Mr.AG', null, '2018-11-08 09:48:55', '1', 'Mr.AG', null, null, '11', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('3fd4129de7794716a549b15b368efd8a', 'firedoor', '防火门', '-1', '/firedoor', 'fanghuomen', 'menu', '6', null, '/firedoor', null, '2018-09-01 16:39:38', '1', 'Mr.AG', null, '2018-09-01 17:01:00', '1', 'Mr.AG', null, null, '7', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('4436ea9172fa42a3895b0986ffe93469', 'hydrant', '消防用水', '-1', null, 'xiaofangshuan', 'menu', '3', null, '/adminSys/gailan/hydrant', null, '2018-08-27 20:45:10', '1', 'Mr.AG', null, '2018-11-22 11:14:30', '1', 'Mr.AG', null, null, '5', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('47ae547878264d2db6944ee1000f88a6', 'fireDeviceManger', '设备管理', '3fd4129de7794716a549b15b368efd8a', '/device/fireDeviceManger', 'erjicaidan', 'menu', '3', null, '/firedoor/fireDeviceManger', null, '2018-09-01 17:02:33', '1', 'Mr.AG', null, '2018-09-01 17:02:33', '1', 'Mr.AG', null, '_import(\'device/fireDeviceManger/index\')', '7', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('59b2bb256c9546d1bd3fe84dd771b8e5', 'inspectionManagement', '设备巡检', '-1', null, 'xunjian', 'menu', '5', null, '/inspectionManagement', null, '2018-09-01 16:52:58', '1', 'Mr.AG', null, '2018-11-29 11:43:35', '1', 'Mr.AG', null, null, '10', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('6', 'menuManager', '菜单管理', '13', '/admin/menu', 'erjicaidan', 'menu', '2', '', '/adminSys/baseManager/menuManager', null, null, null, null, null, '2018-08-16 22:33:45', '1', 'Mr.AG', '127.0.0.1', '_import(\'admin/menu/index\')', null, '1', '0', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('62ce0ec9ecd347b8985e7fb41a2c8351', 'videoAnalyze', '视频分析', '6bca1b6478b94c1eb508bcd6d5666185', '/device/videoAnalyze', 'erjicaidan', 'menu', '2', null, '/videoSurveillance/statisticAnly', null, '2018-09-01 17:07:55', '1', 'Mr.AG', null, '2019-04-19 18:32:44', '1', 'Mr.AG', null, '_import(\'device/videoAnalyze/index\')', '9', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('686b4b901d814915a3ef6d2a6f332bcb', 'gasFirecontrol', '消防电源', '-1', null, 'qitimiehuo', 'menu', '4', null, '/adminSys/gailan/gasFirecontrol', null, '2018-08-27 20:47:00', '1', 'Mr.AG', null, '2018-11-29 09:33:29', '1', 'Mr.AG', null, null, '6', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('6bca1b6478b94c1eb508bcd6d5666185', 'videoSurveillance', '危险预警', '-1', null, 'jiankong', 'menu', '8', null, '/videoSurveillance', null, '2018-09-01 16:50:55', '1', 'Mr.AG', null, '2018-11-29 09:28:39', '1', 'Mr.AG', null, null, '9', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('6d479f20c8e24046be5e1b73ce2182d7', 'videoWatch', '实时视频', '6bca1b6478b94c1eb508bcd6d5666185', '/device/videoWatch', 'erjicaidan', 'menu', '0', null, '/videoSurveillance/realtimeMonit', null, '2018-09-01 17:06:17', '1', 'Mr.AG', null, '2019-04-19 18:30:06', '1', 'Mr.AG', null, '_import(\'device/videoWatch/index\')', '9', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('6f2bcb2b58694fbdb4fc1c781e9d8ad3', 'sysConfig', '系统设置', '13', '/device/devicesysConfig', 'erjicaidan', 'menu', '0', null, '/adminSys/sysConfig', null, '2018-08-08 05:36:35', '1', 'Mr.AG', null, '2018-08-08 05:36:35', '1', 'Mr.AG', null, '_import(\'device/devicesysConfig/index\')', null, '0', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('7', 'groupManager', '角色权限管理', '13', '/admin/group', 'erjicaidan', 'menu', '3', '', '/adminSys/baseManager/groupManager', null, null, null, null, null, '2018-08-02 16:44:48', '1', 'Mr.AG', '127.0.0.1', 'import(\'admin/group/index\')', null, '0', '1', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('7574b969c9fa4e5895d6cc9c2b8a9a62', 'tenantManager', '站点管理', '13', '/admin/tenantManager', 'erjicaidan', 'menu', '7', null, '/adminSys/baseManager/tenantManager', null, '2018-02-09 08:56:43', '1', 'Mr.AG', null, '2018-08-03 10:03:02', '1', 'Mr.AG', null, null, null, '1', '0', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('79c1f75121c745ceace4874e8a1186d4', 'exceptionRecord', '异常记录', 'a7a931a5e33b4b08bab45a746582f849', '/device/exceptionRecord', 'erjicaidan', 'menu', '1', null, '/evacuation/exceptionRecord', null, '2018-09-01 17:10:15', '1', 'Mr.AG', null, '2018-09-01 17:10:15', '1', 'Mr.AG', null, '_import(\'device/exceptionRecord/index\')', '8', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('7ba6a35eddc34f4c8cf89bfb75a96aea', 'statisticAnly', '统计分析', '3fd4129de7794716a549b15b368efd8a', '/device/statisticAnly', 'erjicaidan', 'menu', '2', null, '/firedoor/statisticAnly', null, '2018-09-01 17:01:52', '1', 'Mr.AG', null, '2018-09-01 17:01:52', '1', 'Mr.AG', null, '_import(\'device/statisticAnly/index\')', '7', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('7bacde5964564eecac09871642988de7', 'exceptionRecord', '异常记录', '34d08e936864418492a0d32d2780227f', '/device/exceptionRecord', 'erjicaidan', 'menu', '1', null, '/adminSys/huozaibaojing/exceptionRecord', null, '2018-08-16 10:35:40', '1', 'Mr.AG', null, '2018-08-16 10:35:40', '1', 'Mr.AG', null, '_import(\'device/exceptionRecord/index\')', '1', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('83ffd3eeffa54e71ad15b1f92ddf602d', 'fireDeviceManger', '设备管理', '32ae9e1e019043b9b4a763b616fddd91', '/device/fireDeviceManger', 'erjicaidan', 'menu', '3', null, '/adminSys/dianqihuozai/fireDeviceManger', null, '2018-08-13 09:26:25', '1', 'Mr.AG', null, '2018-08-16 11:29:40', '1', 'Mr.AG', null, '_import(\'device/fireDeviceManger/index\')', '2', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('850b21937e7941a78161b90e9df8a6d0', 'combustible', '可燃气体', '-1', null, 'keranqiti', 'menu', '5', null, '/adminSys/gailan/combustible', null, '2018-08-27 20:44:20', '1', 'Mr.AG', null, '2018-08-27 21:08:21', '1', 'Mr.AG', null, null, '4', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('867f8df31dc949a6a619ee150b61d88a', 'exceptionRecord', '异常记录', '394aea58ee1b4b75a14b1f1e72ecbfe3', '/device/exceptionRecord', 'erjicaidan', null, '1', null, '/fireMainframe/exceptionRecord', null, '2018-11-08 14:38:27', '1', 'Mr.AG', null, '2018-11-08 14:38:27', '1', 'Mr.AG', null, '_import(\'device/exceptionRecord/index\')', '11', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('891ad499263041a3b4b0c6db2910e922', 'realtimeMonit', '实时监测', '686b4b901d814915a3ef6d2a6f332bcb', '/device/realtimeMonit', 'erjicaidan', 'menu', '0', null, '/adminSys/gailan/gasFirecontrol/realtimeMonit', null, '2018-08-27 20:58:43', '1', 'Mr.AG', null, '2018-08-27 20:58:43', '1', 'Mr.AG', null, '_import(\'device/realtimeMonit/index\')', '6', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('897ed54d9e9d4e7a8ee8598a6d01b485', 'fireDeviceManger', '设备管理', '850b21937e7941a78161b90e9df8a6d0', '/device/fireDeviceManger', 'erjicaidan', 'menu', '3', null, '/adminSys/gailan/combustible/fireDeviceManger', null, '2018-08-27 21:04:19', '1', 'Mr.AG', null, '2018-08-27 21:05:42', '1', 'Mr.AG', null, '_import(\'device/fireDeviceManger/index\')', '4', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('8f972645d58f423fb69efdaf06ce6d02', 'statisticAnly', '统计分析', '850b21937e7941a78161b90e9df8a6d0', '/device/statisticAnly', 'erjicaidan', 'menu', '2', null, '/adminSys/gailan/combustible/statisticAnly', null, '2018-08-27 21:03:39', '1', 'Mr.AG', null, '2018-08-27 21:06:10', '1', 'Mr.AG', null, '_import(\'device/statisticAnly/index\')', '4', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('92772ca6b5784a4fb1e89043e9513422', 'facilityManager', '设施管理', '59b2bb256c9546d1bd3fe84dd771b8e5', null, 'erjicaidan', 'menu', '0', null, '/inspectionManagement/facilityManager', null, '2018-11-27 18:59:12', '1', 'Mr.AG', null, '2018-11-27 18:59:12', '1', 'Mr.AG', null, '_import(\'device/facilityManager/index\')', '10', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('932f86e394804607b71dd4d74573c821', 'statisticAnly', '统计分析', '686b4b901d814915a3ef6d2a6f332bcb', '/device/statisticAnly', 'erjicaidan', 'menu', '3', null, '/adminSys/gailan/gasFirecontrol/statisticAnly', null, '2018-08-27 21:00:24', '1', 'Mr.AG', null, '2018-08-27 21:05:26', '1', 'Mr.AG', null, '_import(\'device/statisticAnly/index\')', '6', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('9b7b9406656644738f85db8add55a400', 'fireDeviceManger', '设备管理', '686b4b901d814915a3ef6d2a6f332bcb', '/device/fireDeviceManger', 'erjicaidan', 'menu', '3', null, '/adminSys/gailan/gasFirecontrol/fireDeviceManger', null, '2018-08-27 21:01:30', '1', 'Mr.AG', null, '2018-08-27 21:05:05', '1', 'Mr.AG', null, '_import(\'device/fireDeviceManger/index\')', '6', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('9d80dd89805948a1a98f52223c017cb9', 'deviceSeries', '设备类型', '13', '/device/deviceSensorSeries', 'erjicaidan', 'menu', '0', null, '/adminSys/deviceSeries', null, '2018-08-06 04:13:18', '25714173581197312', 'Mr.AG', null, '2018-11-26 12:07:05', '1', 'Mr.AG', null, '_import(\'device/deviceSensorSeries/index\')', null, '1', '0', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('a3c1eec206ec49ffbd555dee51e28c22', 'inspectionRoute', '巡检路线', '59b2bb256c9546d1bd3fe84dd771b8e5', null, 'erjicaidan', null, '2', null, '/inspectionManagement/inspectionRoute', null, '2018-12-15 20:16:07', '1', 'Mr.AG', null, '2018-12-15 20:16:07', '1', 'Mr.AG', null, '_import(\'device/inspectionManager/inspectroute.vue\')', '10', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('a5f6912494b543ce8ba15ae1c23880bb', 'networkManager', '联网单位管理', '13', 'admin/network', 'erjicaidan', 'menu', '10', null, '/adminSys/networkManager', null, '2018-08-12 03:46:08', '1', 'Mr.AG', null, '2018-08-13 03:28:57', '1', 'Mr.AG', null, '_import(\'admin/network/index\')', null, '0', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('a7a931a5e33b4b08bab45a746582f849', 'evacuation', '物联设备', '-1', null, 'yingjideng', 'menu', '7', null, '/evacuation', null, '2018-09-01 16:49:34', '1', 'Mr.AG', null, '2018-11-29 11:43:55', '1', 'Mr.AG', null, null, '8', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('ab57e525dacd4868aeca0592bb78cda3', 'realtimeMonit', '实时监测', '34d08e936864418492a0d32d2780227f', '/device/realtimeMonit', 'erjicaidan', 'menu', '0', null, '/adminSys/huozaibaojing/realtimeMonit', null, '2018-08-16 10:36:28', '1', 'Mr.AG', null, '2018-08-16 10:36:28', '1', 'Mr.AG', null, '_import(\'device/realtimeMonit/index\')', '1', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('b7787c7259bf4df49e505e9be89db5c6', 'alarmLevel', '报警等级', '13', '/device/deviceAlarmLevel', 'erjicaidan', 'menu', '0', null, '/adminSys/alarmLevel', null, '2018-08-06 03:34:16', '25714173581197312', 'Mr.AG', null, '2018-08-06 03:34:16', '25714173581197312', 'Mr.AG', null, '_import(\'device/deviceAlarmLevel/index\')', null, '1', '0', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('be7f13cf46d84a778eb06303e2f6eee7', 'videoAbnormal', '异常记录', '6bca1b6478b94c1eb508bcd6d5666185', '/device/videoAbnormal', 'erjicaidan', 'menu', '1', null, '/videoSurveillance/exceptionRecord', null, '2018-09-01 17:07:04', '1', 'Mr.AG', null, '2019-04-19 18:31:14', '1', 'Mr.AG', null, '_import(\'device/videoAbnormal/index\')', '9', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('bf60d45c837a43f48455f65f059741dd', 'fireDeviceManger', '设备管理', '4436ea9172fa42a3895b0986ffe93469', '/device/fireDeviceManger', 'erjicaidan', 'menu', '3', null, '/adminSys/gailan/hydrant/fireDeviceManger', null, '2018-08-27 21:00:35', '1', 'Mr.AG', null, '2018-08-27 21:04:20', '1', 'Mr.AG', null, '_import(\'device/fireDeviceManger/index\')', '5', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('c1258699fd0a4a4f9a2d2034bc7e59b6', 'facilitiesType', '设施类型', '59b2bb256c9546d1bd3fe84dd771b8e5', '', 'erjicaidan', 'menu', '0', null, '/inspectionManagement/realtimeMonit', null, '2018-09-01 17:03:21', '1', 'Mr.AG', null, '2018-11-27 17:33:15', '1', 'Mr.AG', null, '_import(\'device/facilityTypeManger/cindex\')', '10', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('c5231a4ca0564adb902d338d09a9a8de', 'statisticAnly', '统计分析', 'a7a931a5e33b4b08bab45a746582f849', '/device/statisticAnly', 'erjicaidan', 'menu', '2', null, '/evacuation/statisticAnly', null, '2018-09-01 17:10:56', '1', 'Mr.AG', null, '2018-09-01 17:10:56', '1', 'Mr.AG', null, '_import(\'device/statisticAnly/index\')', '8', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('c806d659034242dca083460956b484e0', 'realtimeMonit', '实时监测', '394aea58ee1b4b75a14b1f1e72ecbfe3', '/device/realtimeMonit', 'erjicaidan', null, '0', null, '/fireMainframe/realtimeMonit', null, '2018-11-08 14:37:24', '1', 'Mr.AG', null, '2018-11-08 14:37:24', '1', 'Mr.AG', null, '_import(\'device/realtimeMonit/index\')', '11', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('c856fa9f81aa472fa0da389537669695', 'fireDeviceManger', '设备管理', '34d08e936864418492a0d32d2780227f', '/device/fireDeviceManger', 'erjicaidan', 'menu', '3', null, '/adminSys/huozaibaojing/fireDeviceManger', null, '2018-08-16 10:34:29', '1', 'Mr.AG', null, '2018-08-16 10:34:29', '1', 'Mr.AG', null, '_import(\'device/fireDeviceManger/index\')', '1', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('ca3c5369a1f2415da7c49b257de7d9e9', 'buildManager', '建筑管理', '13', '/admin/build', 'erjicaidan', 'menu', '9', null, '/adminSys/buildManager', null, '2018-08-08 22:34:50', '1', 'Mr.AG', null, '2018-08-08 22:34:50', '1', 'Mr.AG', null, '_import(\'admin/build/index\')', null, '0', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('cbbb29c26c2f4b608f9dd610716e0f91', 'realtimeMonit', '实时监测', '850b21937e7941a78161b90e9df8a6d0', '/device/realtimeMonit', 'erjicaidan', 'menu', '0', null, '/adminSys/gailan/combustible/realtimeMonit', null, '2018-08-27 21:02:18', '1', 'Mr.AG', null, '2018-08-27 21:06:24', '1', 'Mr.AG', null, '_import(\'device/realtimeMonit/index\')', '4', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('d05eb349b2cb4c8384fcde7aa3940d91', 'exceptionRecord', '异常记录', '850b21937e7941a78161b90e9df8a6d0', '/device/exceptionRecord', 'erjicaidan', 'menu', '1', null, '/adminSys/gailan/combustible/exceptionRecord', null, '2018-08-27 21:02:58', '1', 'Mr.AG', null, '2018-08-27 21:05:51', '1', 'Mr.AG', null, '_import(\'device/exceptionRecord/index\')', '4', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('d249e270aa7b42b3854826c87664cb27', 'statisticAnly', '统计分析', '34d08e936864418492a0d32d2780227f', '/device/statisticAnly', 'erjicaidan', 'dirt', '2', null, '/adminSys/huozaibaojing/statisticAnly', null, '2018-08-16 10:37:29', '1', 'Mr.AG', null, '2018-08-16 10:37:29', '1', 'Mr.AG', null, '_import(\'device/statisticAnly/index\')', '1', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('d6577a1dc4204adbaa5d116b6fc7b285', 'exceptionRecord', '异常记录', '32ae9e1e019043b9b4a763b616fddd91', '/device/exceptionRecord', 'erjicaidan', 'menu', '1', null, '/adminSys/dianqihuozai/exceptionRecord', null, '2018-08-11 09:18:32', '1', 'Mr.AG', null, '2018-08-11 09:18:32', '1', 'Mr.AG', null, '_import(\'device/exceptionRecord/index\')', '2', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('d7658873de1e453f9afc2b709e1c2129', 'realtimeMonit', '实时监测', '32ae9e1e019043b9b4a763b616fddd91', '/device/realtimeMonit', 'erjicaidan', 'menu', '0', null, '/adminSys/dianqihuozai/realtimeMonit', null, '2018-08-10 21:57:30', '1', 'Mr.AG', null, '2018-08-10 21:57:30', '1', 'Mr.AG', null, '_import(\'device/realtimeMonit/index\')', '2', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('d8eecc615c734ccbac1eb115367d9500', 'patrolSchemeManager', '巡检计划', '59b2bb256c9546d1bd3fe84dd771b8e5', null, 'erjicaidan', null, '3', null, '/inspectionManagement/patrolSchemeManager', null, '2018-12-15 20:17:33', '1', 'Mr.AG', null, '2018-12-15 20:17:33', '1', 'Mr.AG', null, '_import(\'device/patrolScheme/index\')', '10', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('d980ef8536f44c9884f57fe2fc5d6ec5', 'hardwareSet', '消火栓管理', '4436ea9172fa42a3895b0986ffe93469', '/device/hardwareSet', 'erjicaidan', null, '4', null, '/adminSys/gailan/hydrant/hardwareSet', null, '2018-11-01 16:18:09', '1', 'Mr.AG', null, '2018-11-02 16:11:42', '1', 'Mr.AG', null, '_import(\'device/hardwareSet/index\')', '5', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('d9e82ba6c8cb4ec8b0b73f928c1c3c36', 'gailan', '概览', '-1', '/gailan', 'gailan', 'menu', '0', null, '/adminSys/gailan', null, '2018-08-02 05:40:14', '1', 'Mr.AG', null, '2018-08-03 04:29:01', '1', 'Mr.AG', null, 'Layout', null, '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('dbb0aef1de5543de94cb7c4c1e4af569', 'fireDeviceManger', '设备管理', 'a7a931a5e33b4b08bab45a746582f849', '/device/fireDeviceManger', 'erjicaidan', 'menu', '3', null, '/evacuation/fireDeviceManger', null, '2018-09-01 17:11:36', '1', 'Mr.AG', null, '2018-09-01 17:11:36', '1', 'Mr.AG', null, '_import(\'device/fireDeviceManger/index\')', '8', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('e89770221d72480db888c2256eda01c6', 'statisticAnly', '统计分析', '32ae9e1e019043b9b4a763b616fddd91', '/device/statisticAnly', 'erjicaidan', 'dirt', '2', null, '/adminSys/dianqihuozai/statisticAnly', null, '2018-08-11 09:19:34', '1', 'Mr.AG', null, '2018-08-16 22:32:19', '1', 'Mr.AG', null, '_import(\'device/statisticAnly/index\')', '2', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('ee9383850d164e06bdfeea6d3eb8ca82', 'exceptionRecord', '异常记录', '686b4b901d814915a3ef6d2a6f332bcb', '/device/exceptionRecord', 'erjicaidan', 'menu', '1', null, '/adminSys/gailan/gasFirecontrol/exceptionRecord', null, '2018-08-27 20:59:38', '1', 'Mr.AG', null, '2018-08-27 21:05:15', '1', 'Mr.AG', null, '_import(\'device/exceptionRecord/index\')', '6', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('f57c87605fd64a70876696a497c3f6e0', 'stationConfig', '测点配置', '13', '/device/deviceMeasuringPoint', 'erjicaidan', 'menu', '0', null, '/adminSys/stationConfig', null, '2018-08-02 16:21:40', '1', 'Mr.AG', null, '2018-08-02 17:24:21', '1', 'Mr.AG', null, '_import(\'device/deviceMeasuringPoint/index\')', null, '1', '0', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('f6aba564f8ca401d9f569755d9d9f7be', 'inspectionRecord', '巡检记录', '59b2bb256c9546d1bd3fe84dd771b8e5', null, 'erjicaidan', null, '4', null, '/inspectionManagement/inspectionRecord', null, '2018-12-15 20:18:33', '1', 'Mr.AG', null, '2018-12-15 20:18:33', '1', 'Mr.AG', null, '_import(\'device/inspectionRecord/index\')', '10', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('f761adf27f304f45851ee1eebfd73c1e', 'realtimeMonit', '实时监测', '3fd4129de7794716a549b15b368efd8a', '/device/realtimeMonit', 'erjicaidan', 'menu', '0', null, '/firedoor/realtimeMonit', null, '2018-09-01 16:58:54', '1', 'Mr.AG', null, '2018-09-01 16:58:54', '1', 'Mr.AG', null, '_import(\'device/realtimeMonit/index\')', '7', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');
INSERT INTO `base_menu` VALUES ('fd64ee1985714e46b771709766a45bd0', 'fireFrameManger', '主机管理', '394aea58ee1b4b75a14b1f1e72ecbfe3', '/device/fireMainframe', 'erjicaidan', null, '3', null, '/fireMainframe/fireFrameManger', null, '2018-11-08 14:41:13', '1', 'Mr.AG', null, '2018-11-08 14:41:13', '1', 'Mr.AG', null, '_import(\'device/fireMainframe/mangerindex\')', '11', '1', '1', null, null, null, null, 'ac88ceb386aa4231b09bf472cb937c24');



-- ----------------------------
-- Records of channel 初始化栏目
-- ----------------------------
INSERT INTO `channel` VALUES ('1', '-1', '消防系统', '无线感烟', '0', null, null, '2018-08-09 04:02:30', null, null, '2018-08-09 04:02:30', null, null);
INSERT INTO `channel` VALUES ('2', '-1', '消防系统', '电气火灾', '0', null, null, '2018-08-09 04:02:57', null, null, '2018-08-09 04:02:57', null, null);
INSERT INTO `channel` VALUES ('4', '-1', '消防系统', '可燃气体', '0', null, null, '2018-08-28 04:52:44', null, null, '2018-08-28 04:52:44', null, null);
INSERT INTO `channel` VALUES ('5', '-1', '消防系统', '消防用水', '0', null, null, '2018-08-28 04:53:31', null, null, '2018-08-28 04:53:31', null, null);
INSERT INTO `channel` VALUES ('6', '-1', '消防系统', '消防电源', '0', null, null, '2018-08-28 04:53:54', null, null, '2018-08-28 04:53:54', null, null);
INSERT INTO `channel` VALUES ('7', '-1', '消防系统', '防火门', '0', null, null, '2018-09-01 15:58:04', null, null, '2018-09-01 15:58:04', null, null);
INSERT INTO `channel` VALUES ('8', '-1', '消防系统', '物联设备', '0', null, null, '2018-09-01 15:58:36', null, null, '2018-09-01 15:58:36', null, null);
INSERT INTO `channel` VALUES ('9', '-1', '其他', '危险预警', '0', null, null, '2018-09-01 15:59:13', null, null, '2018-09-01 15:59:13', null, null);
INSERT INTO `channel` VALUES ('10', '-1', '其他', '设备巡检', '0', null, null, '2018-09-01 15:59:31', null, null, '2018-09-01 15:59:31', null, null);
INSERT INTO `channel` VALUES ('11', '-1', '消防系统', '消防主机', '0', null, null, '2018-11-07 18:14:42', null, null, '2018-11-07 18:14:42', null, null);

-- 初始化系统管理员
-- 超级管理员用户
INSERT INTO `turing_admin`.`base_user` (`id`, `username`, `password`, `name`, `birthday`, `address`, `mobile_phone`, `tel_phone`, `email`, `sex`, `type`, `status`, `description`, `crt_time`, `crt_user_id`, `crt_user_name`, `upd_time`, `upd_user_id`, `upd_user_name`, `attr1`, `attr2`, `attr3`, `attr4`, `attr5`, `attr6`, `attr7`, `attr8`, `tenant_id`, `is_deleted`, `is_disabled`, `depart_id`, `is_super_admin`) VALUES ('1', 'admin', 'VfezIHM5B4YXLz4iu7KeN0CewBjwOwhx5MkfjPRC3sQ=', 'Mr.AG', '', NULL, '13476008951', NULL, '', '男', NULL, NULL, '', NULL, NULL, NULL, '2018-08-20 09:41:03', '1', 'Mr.AG', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ac88ceb386aa4231b09bf472cb937c24', '0', '0', 'd583e7de6d2d48b78fb3c7dcb180cb1f', '1');

