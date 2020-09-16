/*
Navicat MySQL Data Transfer

Source Server         : TMC图灵线
Source Server Version : 50721
Source Host           : mysql-cn-south-1-cba0ae12d7a54c5b.public.jcloud.com:3306
Source Database       : turing_auth

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-07-05 16:38:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for auth_client
-- ----------------------------
DROP TABLE IF EXISTS `auth_client`;
CREATE TABLE `auth_client` (
  `id` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '服务编码',
  `secret` varchar(255) DEFAULT NULL COMMENT '服务密钥',
  `name` varchar(255) DEFAULT NULL COMMENT '服务名',
  `locked` char(1) DEFAULT NULL COMMENT '是否锁定',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `crt_time` datetime DEFAULT NULL COMMENT '创建时间',
  `crt_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `crt_name` varchar(255) DEFAULT NULL COMMENT '创建人姓名',
  `crt_host` varchar(255) DEFAULT NULL COMMENT '创建主机',
  `upd_time` datetime DEFAULT NULL COMMENT '更新时间',
  `upd_user` varchar(255) DEFAULT NULL COMMENT '更新人',
  `upd_name` varchar(255) DEFAULT NULL COMMENT '更新姓名',
  `upd_host` varchar(255) DEFAULT NULL COMMENT '更新主机',
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of auth_client
-- ----------------------------
INSERT INTO `auth_client` VALUES ('1', 'turing-gate', '123456', 'turing-gate', '0', '服务网关', null, '', '', '', '2017-07-07 21:51:32', '1', '管理员', '0:0:0:0:0:0:0:1', '', '', '', '', '', '', '', '');
INSERT INTO `auth_client` VALUES ('18', 'turing-transaction', '123456', 'turing-transaction', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('20', 'turing-dict', '123566', 'turing-dict', '0', '数据字典服务', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('21', 'turing-demo-depart-data', '123456', 'turing-demo-depart-data', '0', '测试服务', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('22', 'turing-workflow', '123456', 'turing-workflow', '0', '工作流服务', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('23', 'turing-datahandler', '123456', 'turing-datahandler', '0', '数据处理服务', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('24', 'turing-datacollection', '123456', 'turing-datacollection', '0', '数据采集服务', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('3', 'turing-admin', '123456', 'turing-admin', '0', '', null, null, null, null, '2017-07-06 21:42:17', '1', '管理员', '0:0:0:0:0:0:0:1', null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('531ac9cd1f924299a06deb300140c0e4', 'turing-test', '123456', 'turing-test', '0', 'fwfgw', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('6', 'turing-auth', '123456', 'turing-auth', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('7', 'turing-tool', '123456', 'turing-tool', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client` VALUES ('97750946674638848', 'turing-device', '123456', 'turing-device', '0', '设备管理服务', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for auth_client_service
-- ----------------------------
DROP TABLE IF EXISTS `auth_client_service`;
CREATE TABLE `auth_client_service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `service_id` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of auth_client_service
-- ----------------------------
INSERT INTO `auth_client_service` VALUES ('21', '4', '5', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('43', '3', '16', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('45', '12', '16', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('46', '18', '18', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('53', '3', '6', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('61', '3', '1', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('62', '6', '1', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('63', '20', '1', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('65', '3', '21', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('66', '3', '22', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('67', '267d29a2c26743c6bb01bc52232c26f5', '-1', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('69', '531ac9cd1f924299a06deb300140c0e4', '531ac9cd1f924299a06deb300140c0e4', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('70', '267d29a2c26743c6bb01bc52232c26f5', '267d29a2c26743c6bb01bc52232c26f5', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('71', '97750946674638848', '1', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('72', '23', '1', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('73', '3', '97750946674638848', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('74', '97750946674638848', '3', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('75', '24', '1', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('76', '3', '24', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('77', '97750946674638848', '24', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('78', '97750946674638848', '23', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('79', '3', '23', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('80', '23', '97750946674638848', null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `auth_client_service` VALUES ('81', '23', '24', null, null, null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for gateway_route
-- ----------------------------
DROP TABLE IF EXISTS `gateway_route`;
CREATE TABLE `gateway_route` (
  `id` varchar(50) NOT NULL,
  `path` varchar(255) NOT NULL COMMENT '映射路劲',
  `service_id` varchar(50) DEFAULT NULL COMMENT '映射服务',
  `url` varchar(255) DEFAULT NULL COMMENT '映射外连接',
  `retryable` tinyint(1) DEFAULT NULL COMMENT '是否重试',
  `enabled` tinyint(1) NOT NULL COMMENT '是否启用',
  `strip_prefix` tinyint(1) DEFAULT NULL COMMENT '是否忽略前缀',
  `crt_user_name` varchar(255) DEFAULT NULL COMMENT '创建人',
  `crt_user_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',
  `crt_time` datetime DEFAULT NULL COMMENT '创建时间',
  `upd_user_name` varchar(255) DEFAULT NULL COMMENT '最后更新人',
  `upd_user_id` varchar(36) DEFAULT NULL COMMENT '最后更新人ID',
  `upd_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of gateway_route
-- ----------------------------
INSERT INTO `gateway_route` VALUES ('admin', '/admin/**', 'turing-admin', null, '0', '1', '1', 'Mr.AG', '1', '2018-02-25 14:33:30', 'Mr.AG', '1', '2018-02-25 14:38:31');
INSERT INTO `gateway_route` VALUES ('auth', '/auth/**', 'turing-auth', null, '0', '1', '1', null, null, null, 'Mr.AG', '1', '2018-02-25 14:29:51');
INSERT INTO `gateway_route` VALUES ('center', '/center/**', 'turing-center', null, '0', '1', '1', 'Mr.AG', '1', '2018-02-26 12:50:51', 'Mr.AG', '1', '2018-02-26 12:50:51');
INSERT INTO `gateway_route` VALUES ('datacollection', '/datacollection/**', 'turing-datacollection', null, '0', '1', '1', 'hanyong', '1', '2018-09-25 09:31:40', 'hanyong', '1', '2018-09-25 09:31:49');
INSERT INTO `gateway_route` VALUES ('datahandler', '/datahandler/**', 'turing-datahandler', null, '0', '1', '1', 'hanyong', '1', '2018-07-31 19:56:29', 'hanyong', '1', '2018-07-31 19:56:29');
INSERT INTO `gateway_route` VALUES ('device', '/device/**', 'turing-device', null, '0', '1', '1', 'hanyong', '1', '2018-07-19 16:14:03', 'hanyong', '1', '2018-07-19 16:14:03');
INSERT INTO `gateway_route` VALUES ('dict', '/dict/**', 'turing-dict', null, '0', '1', '1', null, null, null, 'Mr.AG', '1', '2018-02-25 14:41:07');
INSERT INTO `gateway_route` VALUES ('tool', '/tool/**', 'turing-tool', null, '0', '1', '1', null, null, '2018-04-02 21:04:47', null, null, '2018-04-02 21:04:52');
INSERT INTO `gateway_route` VALUES ('workflow', '/wf/**', 'turing-workflow', null, '0', '1', '1', null, null, '2018-04-05 13:58:08', null, null, '2018-04-05 13:58:14');

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(256) NOT NULL,
  `resource_ids` varchar(256) DEFAULT NULL,
  `client_secret` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(256) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `crt_user_name` varchar(255) DEFAULT NULL,
  `crt_user_id` varchar(36) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `upd_user_name` varchar(255) DEFAULT NULL,
  `upd_user_id` varchar(36) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `is_deleted` char(1) DEFAULT NULL,
  `is_disabled` char(1) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES ('client', '', 'client', 'read', 'password,refresh_token,authorization_code', 'http://localhost:4040/sso/login', null, '14400', '2592000', '{}', 'true', null, null, null, null, null, null, null, null, null);
INSERT INTO `oauth_client_details` VALUES ('test', '', 'test', 'read', 'password,refresh_token', 'http://localhost:9527/#/', '', '60', '60', '{}', 'true', '', '', '', '2018-10-16 13:59:52', 'Mr.AG', '1', '2018-03-28 20:43:14', '', '');
INSERT INTO `oauth_client_details` VALUES ('vue', null, 'vue', 'read', 'password,refresh_token', 'http://localhost:9527/#/', null, '14400', '2592000', '{}', 'true', '', null, null, null, 'Mr.AG', '1', '2018-03-28 20:43:14', null, null);
