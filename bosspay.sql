/*
Navicat MySQL Data Transfer

Source Server         : 189rcmp
Source Server Version : 50624
Source Host           : 192.168.1.189:3306
Source Database       : bosspay

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2016-12-16 10:01:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mp_agent
-- ----------------------------
DROP TABLE IF EXISTS `mp_agent`;
CREATE TABLE `mp_agent` (
  `id` varchar(32) NOT NULL,
  `bill_group_id` varchar(32) DEFAULT NULL COMMENT '话费运营商通道组',
  `data_group_id` varchar(32) DEFAULT NULL COMMENT '数据运营商通道组',
  `bill_model_id` varchar(32) DEFAULT NULL,
  `data_model_id` varchar(32) DEFAULT NULL,
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称简称',
  `phone` varchar(100) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `corp_name` varchar(100) DEFAULT NULL COMMENT '公司名',
  `address` varchar(1000) DEFAULT NULL COMMENT '地址',
  `hotline1` varchar(30) DEFAULT NULL COMMENT '热线1',
  `hotline2` varchar(30) DEFAULT NULL COMMENT '热线2',
  `serviceqq1` varchar(30) DEFAULT NULL COMMENT '客服qq1',
  `serviceqq2` varchar(30) DEFAULT NULL COMMENT '客服qq2',
  `app_key` varchar(1000) DEFAULT NULL COMMENT '密钥',
  `parent_id` varchar(32) DEFAULT NULL COMMENT '上级代理商',
  `status` char(1) DEFAULT NULL COMMENT '状态 0 禁用， 1 启用',
  `start_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '结束时间',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商';

-- ----------------------------
-- Table structure for mp_agent_account
-- ----------------------------
DROP TABLE IF EXISTS `mp_agent_account`;
CREATE TABLE `mp_agent_account` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `agent_id` varchar(32) DEFAULT NULL COMMENT '代理商ID',
  `balance` decimal(10,3) DEFAULT NULL COMMENT '余额',
  `credit` decimal(10,3) DEFAULT NULL,
  `update_date` timestamp NULL DEFAULT NULL,
  `update_user` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商账户';

-- ----------------------------
-- Table structure for mp_agent_account_charge
-- ----------------------------
DROP TABLE IF EXISTS `mp_agent_account_charge`;
CREATE TABLE `mp_agent_account_charge` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `agent_id` varchar(32) DEFAULT NULL COMMENT '代理商ID',
  `order_id` varchar(32) DEFAULT NULL COMMENT '订单ID',
  `fee` decimal(10,3) DEFAULT NULL COMMENT '费用',
  `balance_before` decimal(10,3) DEFAULT NULL COMMENT '操作前余额',
  `balance_after` decimal(10,3) DEFAULT NULL COMMENT '操作后余额',
  `apply_date` timestamp NULL DEFAULT NULL COMMENT '请求时间',
  `end_date` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商账户更新明细历史';

-- ----------------------------
-- Table structure for mp_agent_bill_discount
-- ----------------------------
DROP TABLE IF EXISTS `mp_agent_bill_discount`;
CREATE TABLE `mp_agent_bill_discount` (
  `id` varchar(32) NOT NULL,
  `agent_id` varchar(32) DEFAULT NULL COMMENT '代理商ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `name` varchar(30) DEFAULT NULL COMMENT '名称',
  `discount` decimal(10,3) DEFAULT NULL COMMENT '折扣',
  `bill_pkg_id` varchar(32) DEFAULT NULL COMMENT '话费包ID',
  `province_code` varchar(32) DEFAULT NULL COMMENT '省份代码',
  `city_code` varchar(32) DEFAULT NULL COMMENT '城市代码',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商话费折扣';

-- ----------------------------
-- Table structure for mp_agent_bill_provider
-- ----------------------------
DROP TABLE IF EXISTS `mp_agent_bill_provider`;
CREATE TABLE `mp_agent_bill_provider` (
  `id` varchar(32) NOT NULL,
  `agent_id` varchar(32) DEFAULT NULL COMMENT '代理商ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商可用话费运营商';

-- ----------------------------
-- Table structure for mp_agent_charge_record
-- ----------------------------
DROP TABLE IF EXISTS `mp_agent_charge_record`;
CREATE TABLE `mp_agent_charge_record` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `agent_id` varchar(32) DEFAULT NULL COMMENT '代理商ID',
  `order_id` varchar(32) DEFAULT NULL COMMENT '订单ID',
  `phone` varchar(30) DEFAULT NULL COMMENT '手机号',
  `biz_type` char(2) DEFAULT NULL COMMENT '业务类型',
  `fee` decimal(10,3) DEFAULT NULL COMMENT '费用',
  `value` decimal(10,3) DEFAULT NULL COMMENT '数量',
  `discount` decimal(10,3) DEFAULT NULL COMMENT '折扣',
  `apply_date` timestamp NULL DEFAULT NULL COMMENT '请求时间',
  `end_date` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商抵扣记录';

-- ----------------------------
-- Table structure for mp_agent_data_discount
-- ----------------------------
DROP TABLE IF EXISTS `mp_agent_data_discount`;
CREATE TABLE `mp_agent_data_discount` (
  `id` varchar(32) NOT NULL,
  `agent_id` varchar(32) DEFAULT NULL COMMENT '代理商ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `name` varchar(30) DEFAULT NULL COMMENT '名称',
  `discount` decimal(10,3) DEFAULT NULL COMMENT '折扣',
  `data_pkg_id` varchar(32) DEFAULT NULL COMMENT '流量包ID',
  `province_code` varchar(32) DEFAULT NULL COMMENT '省份代码',
  `city_code` varchar(32) DEFAULT NULL COMMENT '城市代码',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商流量折扣';

-- ----------------------------
-- Table structure for mp_agent_data_provider
-- ----------------------------
DROP TABLE IF EXISTS `mp_agent_data_provider`;
CREATE TABLE `mp_agent_data_provider` (
  `id` varchar(32) NOT NULL,
  `agent_id` varchar(32) DEFAULT NULL COMMENT '代理商ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商可用数据运营商';

-- ----------------------------
-- Table structure for mp_base_bill_discount_detail
-- ----------------------------
DROP TABLE IF EXISTS `mp_base_bill_discount_detail`;
CREATE TABLE `mp_base_bill_discount_detail` (
  `id` varchar(32) NOT NULL,
  `model_id` varchar(32) DEFAULT NULL,
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `name` varchar(30) DEFAULT NULL COMMENT '名称',
  `discount` decimal(10,3) DEFAULT NULL COMMENT '折扣',
  `bill_pkg_id` varchar(32) DEFAULT NULL COMMENT '话费包ID',
  `province_code` varchar(32) DEFAULT NULL COMMENT '省份代码',
  `city_code` varchar(32) DEFAULT NULL COMMENT '城市代码',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商话费折扣';

-- ----------------------------
-- Table structure for mp_base_data_discount_detail
-- ----------------------------
DROP TABLE IF EXISTS `mp_base_data_discount_detail`;
CREATE TABLE `mp_base_data_discount_detail` (
  `id` varchar(32) NOT NULL,
  `model_id` varchar(32) DEFAULT NULL,
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `name` varchar(30) DEFAULT NULL COMMENT '名称',
  `discount` decimal(10,3) DEFAULT NULL COMMENT '折扣',
  `data_pkg_id` varchar(32) DEFAULT NULL COMMENT '话费包ID',
  `province_code` varchar(32) DEFAULT NULL COMMENT '省份代码',
  `city_code` varchar(32) DEFAULT NULL COMMENT '城市代码',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商话费折扣';

-- ----------------------------
-- Table structure for mp_bill_discount_model
-- ----------------------------
DROP TABLE IF EXISTS `mp_bill_discount_model`;
CREATE TABLE `mp_bill_discount_model` (
  `id` varchar(32) NOT NULL,
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='基础话费折扣';

-- ----------------------------
-- Table structure for mp_bill_pkg
-- ----------------------------
DROP TABLE IF EXISTS `mp_bill_pkg`;
CREATE TABLE `mp_bill_pkg` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `carrier_id` varchar(32) DEFAULT NULL,
  `provider_id` varchar(32) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `value` decimal(10,3) DEFAULT NULL COMMENT '数量',
  `price` decimal(10,3) DEFAULT NULL COMMENT '价格（分）',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='话费包';

-- ----------------------------
-- Table structure for mp_data_discount_model
-- ----------------------------
DROP TABLE IF EXISTS `mp_data_discount_model`;
CREATE TABLE `mp_data_discount_model` (
  `id` varchar(32) NOT NULL,
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='基础流量折扣';

-- ----------------------------
-- Table structure for mp_data_pkg
-- ----------------------------
DROP TABLE IF EXISTS `mp_data_pkg`;
CREATE TABLE `mp_data_pkg` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `carrier_id` varchar(32) DEFAULT NULL,
  `provider_id` varchar(32) DEFAULT NULL,
  `data_type` varchar(32) DEFAULT NULL COMMENT '数据类型 1 省内 2 国内',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `value` decimal(10,3) DEFAULT NULL COMMENT '数量',
  `price` decimal(10,3) DEFAULT NULL COMMENT '价格',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据包';

-- ----------------------------
-- Table structure for mp_exception_order
-- ----------------------------
DROP TABLE IF EXISTS `mp_exception_order`;
CREATE TABLE `mp_exception_order` (
  `id` varchar(32) NOT NULL COMMENT '平台订单号',
  `agent_order_id` varchar(32) DEFAULT NULL COMMENT '代理商订单号',
  `order_id` varchar(32) DEFAULT NULL COMMENT '平台订单号',
  `provider_order_id` varchar(32) DEFAULT NULL COMMENT '上家订单号',
  `agent_id` varchar(10) DEFAULT NULL COMMENT '代理商ID',
  `dispatcher_provider_id` varchar(10) DEFAULT NULL COMMENT '分销运营商ID',
  `provider_id` varchar(10) DEFAULT NULL COMMENT '运营商ID',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `result_code` varchar(10000) DEFAULT NULL COMMENT '结果代码',
  `phone` varchar(30) DEFAULT NULL COMMENT '手机号',
  `biz_type` char(2) DEFAULT NULL COMMENT '业务类型：2 话费 1 流量',
  `fee` decimal(10,3) DEFAULT NULL COMMENT '费用',
  `value` decimal(10,3) DEFAULT NULL COMMENT '数量，当biz_type为2流量时，填写流量值',
  `deal_count` int(5) DEFAULT NULL COMMENT '处理次数',
  `apply_date` timestamp NULL DEFAULT NULL COMMENT '请求时间',
  `end_date` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='异常订单表';

-- ----------------------------
-- Table structure for mp_mq
-- ----------------------------
DROP TABLE IF EXISTS `mp_mq`;
CREATE TABLE `mp_mq` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `server` varchar(100) DEFAULT NULL COMMENT '服务',
  `host` varchar(100) DEFAULT NULL COMMENT '主机',
  `username` varchar(100) DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='队列';

-- ----------------------------
-- Table structure for mp_order
-- ----------------------------
DROP TABLE IF EXISTS `mp_order`;
CREATE TABLE `mp_order` (
  `id` varchar(32) NOT NULL COMMENT '平台订单号',
  `order_id` varchar(32) DEFAULT NULL COMMENT '平台的订单号',
  `agent_order_id` varchar(32) DEFAULT NULL COMMENT '代理商订单号',
  `provider_order_id` varchar(32) DEFAULT NULL COMMENT '上家订单号',
  `agent_id` varchar(32) DEFAULT NULL COMMENT '代理商ID',
  `dispatcher_provider_id` varchar(32) DEFAULT NULL COMMENT '分销运营商ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `result_code` varchar(100) DEFAULT NULL COMMENT '结果代码',
  `phone` varchar(30) DEFAULT NULL COMMENT '手机号',
  `biz_type` char(2) DEFAULT NULL COMMENT '业务类型：2 话费 1 流量',
  `fee` decimal(10,3) DEFAULT NULL COMMENT '费用',
  `value` decimal(10,3) DEFAULT NULL COMMENT '数量，当biz_type为2流量时，填写流量值',
  `deal_count` int(5) DEFAULT NULL COMMENT '处理次数',
  `callback_url` varchar(1000) DEFAULT NULL COMMENT '回调地址',
  `callback_status` char(2) DEFAULT NULL COMMENT '回调状态（0是已回调，1是未回调）',
  `apply_date` timestamp NULL DEFAULT NULL COMMENT '请求时间',
  `end_date` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

-- ----------------------------
-- Table structure for mp_phone_section
-- ----------------------------
DROP TABLE IF EXISTS `mp_phone_section`;
CREATE TABLE `mp_phone_section` (
  `id` varchar(32) NOT NULL,
  `section` varchar(32) DEFAULT NULL COMMENT '号段',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `province_code` varchar(64) DEFAULT NULL COMMENT '省份代码',
  `city_code` varchar(64) DEFAULT NULL COMMENT '城市代码',
  `providerType`  varchar(32) DEFAULT NULL COMMENT '运营商类型 1移动 2联通 3电信 4虚拟移动 5虚拟联通 6虚拟电信 7物联网' ,
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='手机号段表';

-- ----------------------------
-- Table structure for mp_provider
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider`;
CREATE TABLE `mp_provider` (
  `id` varchar(32) NOT NULL,
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `short_name` varchar(100) DEFAULT NULL COMMENT '简称',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运营商';

-- ----------------------------
-- Table structure for mp_provider_account
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_account`;
CREATE TABLE `mp_provider_account` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `balance` decimal(10,3) DEFAULT NULL COMMENT '余额',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `create_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='我公司在各运营商平台的账户信息';

-- ----------------------------
-- Table structure for mp_provider_bill_discount
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_bill_discount`;
CREATE TABLE `mp_provider_bill_discount` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `name` varchar(30) DEFAULT NULL COMMENT '名字',
  `discount` decimal(10,3) DEFAULT NULL COMMENT '折扣',
  `bill_pkg_id` varchar(32) DEFAULT NULL COMMENT '话费包ID',
  `province_code` varchar(32) DEFAULT NULL COMMENT '省份代码',
  `city_code` varchar(32) DEFAULT NULL COMMENT '城市代码',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运营商给我公司的话费折扣';

-- ----------------------------
-- Table structure for mp_provider_bill_dispatcher
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_bill_dispatcher`;
CREATE TABLE `mp_provider_bill_dispatcher` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `dispatcher_provider_id` varchar(30) DEFAULT NULL COMMENT '分销运营商ID',
  `weight` varchar(32) DEFAULT NULL COMMENT '权重',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运营商对应话费分销商及权重比例';

-- ----------------------------
-- Table structure for mp_provider_bill_group
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_bill_group`;
CREATE TABLE `mp_provider_bill_group` (
  `id` varchar(32) NOT NULL,
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商对应话费运营商组';

-- ----------------------------
-- Table structure for mp_provider_charge_record
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_charge_record`;
CREATE TABLE `mp_provider_charge_record` (

	`id` varchar(32) NOT NULL COMMENT 'ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `order_id` varchar(32) DEFAULT NULL COMMENT '订单ID',
  `fee` decimal(10,3) DEFAULT NULL COMMENT '费用',
  `balance_before` decimal(10,3) DEFAULT NULL COMMENT '操作前余额',
  `balance_after` decimal(10,3) DEFAULT NULL COMMENT '操作后余额',
  `apply_date` timestamp NULL DEFAULT NULL COMMENT '请求时间',
  `end_date` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='我公司在运营商的抵扣记录';

-- ----------------------------
-- Table structure for mp_provider_data_discount
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_data_discount`;
CREATE TABLE `mp_provider_data_discount` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `name` varchar(30) DEFAULT NULL COMMENT '名字',
  `discount` decimal(10,3) DEFAULT NULL COMMENT '折扣',
  `data_pkg_id` varchar(32) DEFAULT NULL COMMENT '话费包ID',
  `province_code` varchar(32) DEFAULT NULL COMMENT '省份代码',
  `city_code` varchar(32) DEFAULT NULL COMMENT '城市代码',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运营商给我公司的数据折扣';

-- ----------------------------
-- Table structure for mp_provider_data_dispatcher
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_data_dispatcher`;
CREATE TABLE `mp_provider_data_dispatcher` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `dispatcher_provider_id` varchar(30) DEFAULT NULL COMMENT '分销运营商ID',
  `weight` varchar(32) DEFAULT NULL COMMENT '权重',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运营商对应流量分销商及权重比例';

-- ----------------------------
-- Table structure for mp_provider_data_group
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_data_group`;
CREATE TABLE `mp_provider_data_group` (
  `id` varchar(32) NOT NULL,
  `name` varchar(30) DEFAULT NULL COMMENT '名称',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商对应流量运营商组';

-- ----------------------------
-- Table structure for mp_provider_group_bill_rel
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_group_bill_rel`;
CREATE TABLE `mp_provider_group_bill_rel` (
  `id` varchar(32) NOT NULL,
  `group_id` varchar(32) DEFAULT NULL COMMENT '话费运营商组ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商对应话费运营商组';

-- ----------------------------
-- Table structure for mp_provider_group_data_rel
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_group_data_rel`;
CREATE TABLE `mp_provider_group_data_rel` (
  `id` varchar(32) NOT NULL,
  `group_id` varchar(32) DEFAULT NULL COMMENT '流量运营商组ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商对应话费运营商组';

-- ----------------------------
-- Table structure for mp_provider_physical_channel
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_physical_channel`;
CREATE TABLE `mp_provider_physical_channel` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `link_url` varchar(1024) DEFAULT NULL COMMENT '链接URL',
  `callback_url` varchar(1024) DEFAULT NULL COMMENT '回调URL',
  `queue_name` varchar(255) DEFAULT NULL COMMENT '队列名',
  `provider_mark` varchar(255) DEFAULT NULL COMMENT '通道标识',
  `channel_type` char(2) DEFAULT NULL COMMENT '通道类型：1话费 2流量',
  `operator` varchar(32) DEFAULT NULL COMMENT '操作类型：POST PUT GET DELETE',
  `parameter_list` varchar(1024) DEFAULT NULL COMMENT '参数列表',
  `default_parameter` varchar(1024) DEFAULT NULL COMMENT '默认参数列表',
  `priority` decimal(10,0) DEFAULT NULL COMMENT '优先级',
  `update_date` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运营商物理通道';

-- ----------------------------
-- Table structure for mp_provider_bill_recharge_group
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_bill_recharge_group`;
CREATE TABLE `mp_provider_bill_recharge_group` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `dispatcher_provider_id` varchar(32) DEFAULT NULL COMMENT '分销商ID',
  `priority` int(1) DEFAULT NULL COMMENT '优先级',
  `update_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` varchar(10) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(10) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='复充通道优先级表';

-- ----------------------------
-- Table structure for mp_provider_data_recharge_group
-- ----------------------------
DROP TABLE IF EXISTS `mp_provider_data_recharge_group`;
CREATE TABLE `mp_provider_data_recharge_group` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `provider_id` varchar(32) DEFAULT NULL COMMENT '运营商ID',
  `dispatcher_provider_id` varchar(32) DEFAULT NULL COMMENT '分销商ID',
  `priority` int(1) DEFAULT NULL COMMENT '优先级',
  `update_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` varchar(10) DEFAULT NULL COMMENT '更新人',
  `create_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(10) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='复充通道优先级表';
-- ----------------------------
-- Table structure for sysfunctiont
-- ----------------------------
DROP TABLE IF EXISTS `sysfunctiont`;
CREATE TABLE `sysfunctiont` (
  `sfId` varchar(64) NOT NULL COMMENT '权限过滤编号',
  `sfValue` varchar(640) DEFAULT NULL COMMENT '权限过滤路径',
  `spId` varchar(64) DEFAULT NULL COMMENT '权限编号',
  `srId` varchar(64) DEFAULT NULL COMMENT '角色编号',
  `sfType` varchar(64) DEFAULT NULL COMMENT '过滤类型',
  `sfAddTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`sfId`),
  KEY `fk_sysFunctionT_spId` (`spId`),
  KEY `fk_sysFunctionT_srId` (`srId`),
  CONSTRAINT `fk_sysFunctionT_spId` FOREIGN KEY (`spId`) REFERENCES `syspermissiont` (`spId`),
  CONSTRAINT `fk_sysFunctionT_srId` FOREIGN KEY (`srId`) REFERENCES `sysrolet` (`srId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for syspermissiont
-- ----------------------------
DROP TABLE IF EXISTS `syspermissiont`;
CREATE TABLE `syspermissiont` (
  `spId` varchar(64) NOT NULL COMMENT '权限编号',
  `spName` varchar(64) NOT NULL COMMENT '权限名',
  `spNick` varchar(64) NOT NULL COMMENT '权限中文名',
  `spAddTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`spId`),
  UNIQUE KEY `spName` (`spName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sysrolepermissiont
-- ----------------------------
DROP TABLE IF EXISTS `sysrolepermissiont`;
CREATE TABLE `sysrolepermissiont` (
  `srId` varchar(64) NOT NULL COMMENT '角色编号',
  `spId` varchar(64) NOT NULL COMMENT '权限编号',
  `srpAddTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`srId`,`spId`),
  KEY `fk_sysRolePermissionT_spId` (`spId`),
  CONSTRAINT `fk_sysRolePermissionT_spId` FOREIGN KEY (`spId`) REFERENCES `syspermissiont` (`spId`),
  CONSTRAINT `fk_sysRolePermissionT_srId` FOREIGN KEY (`srId`) REFERENCES `sysrolet` (`srId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sysrolet
-- ----------------------------
DROP TABLE IF EXISTS `sysrolet`;
CREATE TABLE `sysrolet` (
  `srId` varchar(64) NOT NULL COMMENT '角色编号',
  `srName` varchar(64) NOT NULL COMMENT '角色名',
  `srAddTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`srId`),
  UNIQUE KEY `srName` (`srName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sysuserrolet
-- ----------------------------
DROP TABLE IF EXISTS `sysuserrolet`;
CREATE TABLE `sysuserrolet` (
  `suId` varchar(64) NOT NULL COMMENT '用户编号',
  `srId` varchar(64) NOT NULL COMMENT '角色编号',
  `surAddTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`suId`,`srId`),
  KEY `fk_sysUserRoleT_srId` (`srId`),
  CONSTRAINT `fk_sysUserRoleT_srId` FOREIGN KEY (`srId`) REFERENCES `sysrolet` (`srId`),
  CONSTRAINT `fk_sysUserRoleT_suId` FOREIGN KEY (`suId`) REFERENCES `sysusert` (`suId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sysusert
-- ----------------------------
DROP TABLE IF EXISTS `sysusert`;
CREATE TABLE `sysusert` (
  `suId` varchar(64) NOT NULL COMMENT '用户编号',
  `suName` varchar(64) DEFAULT NULL COMMENT '用户名',
  `suPass` varchar(64) DEFAULT NULL COMMENT '用户密码',
  `suCredits` int(8) DEFAULT NULL COMMENT '用户授权暂不用',
  `suMobile` varchar(64) DEFAULT NULL COMMENT '用户手机',
  `suNick` varchar(64) DEFAULT NULL COMMENT '用户别名',
  `suQq` varchar(64) DEFAULT NULL COMMENT '用户QQ',
  `suEmail` varchar(64) DEFAULT NULL COMMENT '用户邮箱',
  `suRegTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
  `suLastIp` varchar(64) DEFAULT NULL COMMENT '用户最后IP',
  `suLastVisit` timestamp NULL DEFAULT NULL COMMENT '最后访问时间',
  PRIMARY KEY (`suId`),
  UNIQUE KEY `suName` (`suName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: mp_data_discount_model_detail                         */
/*==============================================================*/

DROP TABLE IF EXISTS `mp_data_discount_model_detail`;
create table mp_data_discount_model_detail
(
   id                   varchar(32) not null comment 'ID',
   model_id             varchar(32) comment '模版ID',
   provider_id          varchar(32) comment '运营商ID',
   name                 varchar(30) comment '名称',
   discount             numeric(10,3) comment '折扣',
   bill_pkg_id          varchar(32) comment '话费包ID',
   province_code        varchar(32) comment '省份代码',
   city_code            varchar(32) comment '城市代码',
   update_date          timestamp comment '更新时间',
   update_user          varchar(32) comment '更新人',
   create_date          timestamp comment '创建时间',
   create_user          varchar(32) comment '创建人',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table mp_data_discount_model_detail comment '代理商话费折扣';

/*==============================================================*/
/* Table: mp_bill_discount_model_detail                         */
/*==============================================================*/

DROP TABLE IF EXISTS `mp_bill_discount_model_detail`;
create table mp_bill_discount_model_detail
(
   id                   varchar(32) not null comment 'ID',
   model_id             varchar(32) comment '模版ID',
   provider_id          varchar(32) comment '运营商ID',
   name                 varchar(32) comment '名称',
   discount             numeric(10,3) comment '折扣',
   bill_pkg_id          varchar(32) comment '话费包ID',
   province_code        varchar(32) comment '省份代码',
   city_code            varchar(32) comment '城市代码',
   update_date          timestamp comment '更新时间',
   update_user          varchar(32) comment '更新人',
   create_date          timestamp comment '创建时间',
   create_user          varchar(32) comment '创建人',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table mp_bill_discount_model_detail comment '代理商话费折扣模版';
