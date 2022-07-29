/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.36 : Database - fentric_002
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`fentric_002` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `fentric_002`;

/*Table structure for table `iot_category` */

DROP TABLE IF EXISTS `iot_category`;

CREATE TABLE `iot_category` (
  `category_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '设备类型id',
  `category_name` varchar(30) DEFAULT NULL COMMENT '设备类型名(设备读取信息)',
  `category_module_name` varchar(10) DEFAULT NULL COMMENT '设备模型型号(10字节)',
  `category_softeware_version` smallint(5) unsigned DEFAULT NULL COMMENT '软件版本(0-999)',
  `category_document_url` varchar(120) DEFAULT NULL COMMENT '文档地址',
  `d2000` smallint(5) unsigned DEFAULT NULL COMMENT '从站地址号',
  `d2001` smallint(5) unsigned DEFAULT NULL COMMENT '波特率 0:2400 1:4800 2:9600',
  `d2002` smallint(5) unsigned DEFAULT NULL COMMENT '工作模式:0本地监测,1报警联动',
  `d2003` smallint(5) unsigned DEFAULT NULL COMMENT '0三相四线1三相三线2单相',
  `d2004` smallint(5) unsigned DEFAULT NULL,
  `d2005` smallint(5) unsigned DEFAULT NULL,
  `d2006` smallint(5) unsigned DEFAULT NULL,
  `d2007` smallint(5) unsigned DEFAULT NULL,
  `d2008` smallint(5) unsigned DEFAULT NULL,
  `d2009` smallint(5) unsigned DEFAULT NULL,
  `d2011` smallint(5) unsigned DEFAULT NULL,
  `d2012` smallint(5) unsigned DEFAULT NULL,
  `d2013` smallint(5) unsigned DEFAULT NULL,
  `d2014` smallint(5) unsigned DEFAULT NULL,
  `d2015` smallint(5) unsigned DEFAULT NULL,
  `d2016` smallint(5) unsigned DEFAULT NULL,
  `d2017` smallint(5) unsigned DEFAULT NULL,
  `d2018` smallint(5) unsigned DEFAULT NULL,
  `d2019` smallint(5) unsigned DEFAULT NULL,
  `d2020` smallint(5) unsigned DEFAULT NULL,
  `d2021` smallint(5) unsigned DEFAULT NULL,
  `d2022` smallint(5) unsigned DEFAULT NULL,
  `d2023` smallint(5) unsigned DEFAULT NULL,
  `d2024` smallint(5) unsigned DEFAULT NULL,
  `d2025` smallint(5) unsigned DEFAULT NULL,
  `d2026` smallint(5) unsigned DEFAULT NULL,
  `d2027` smallint(5) unsigned DEFAULT NULL,
  `d2028` smallint(5) unsigned DEFAULT NULL,
  `d2029` smallint(5) unsigned DEFAULT NULL,
  `d2030` smallint(5) unsigned DEFAULT NULL,
  `d2031` smallint(5) unsigned DEFAULT NULL,
  `d2032` smallint(5) unsigned DEFAULT NULL,
  `d2080` smallint(5) unsigned DEFAULT NULL COMMENT '事件编号(0-65535)',
  `status` char(1) DEFAULT '0',
  `deleted` char(1) DEFAULT '0',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='设备类型表(参数部分为了拓展性,尽量走协议格式)';

/*Data for the table `iot_category` */

insert  into `iot_category`(`category_id`,`category_name`,`category_module_name`,`category_softeware_version`,`category_document_url`,`d2000`,`d2001`,`d2002`,`d2003`,`d2004`,`d2005`,`d2006`,`d2007`,`d2008`,`d2009`,`d2011`,`d2012`,`d2013`,`d2014`,`d2015`,`d2016`,`d2017`,`d2018`,`d2019`,`d2020`,`d2021`,`d2022`,`d2023`,`d2024`,`d2025`,`d2026`,`d2027`,`d2028`,`d2029`,`d2030`,`d2031`,`d2032`,`d2080`,`status`,`deleted`,`comment`,`create_time`,`update_time`) values (1,'spd智能探测器','FCMA4P',12,'E:\\E_Documents\\WeChat Files\\wxid_0zv2td8yanta22\\FileStorage\\File\\2022-07',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0','0',NULL,'2022-07-29 14:24:34','2022-07-29 14:24:36');

/*Table structure for table `iot_control` */

DROP TABLE IF EXISTS `iot_control`;

CREATE TABLE `iot_control` (
  `control_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '控制表id',
  `device_id` bigint(20) DEFAULT NULL COMMENT '设备id',
  `d2100` smallint(5) unsigned DEFAULT NULL,
  `d2101` smallint(5) unsigned DEFAULT NULL,
  `d2102` smallint(5) unsigned DEFAULT NULL,
  `status` char(1) DEFAULT '0',
  `deleted` char(1) DEFAULT '0',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`control_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备控制操作表(记录每个设备对应的人进行的控制操作)';

/*Data for the table `iot_control` */

/*Table structure for table `iot_device` */

DROP TABLE IF EXISTS `iot_device`;

CREATE TABLE `iot_device` (
  `devive_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '设备id',
  `device_name` varchar(60) DEFAULT NULL COMMENT '设备名称',
  `device_image` varchar(120) DEFAULT 'http://i0.hdslb.com/bfs/new_dyn/d388af98088b81ea6e7c5ffec013059b491524566.png' COMMENT '设备图片',
  `device_address` varchar(200) DEFAULT NULL COMMENT '设备地址,自定义或字典',
  `device_active_time` datetime DEFAULT NULL COMMENT '设备激活时间',
  `device_ip` varchar(32) DEFAULT NULL COMMENT '设备ip地址',
  `status` char(1) DEFAULT '0' COMMENT '设备信息状态(0正常,1禁用)禁用设备卡应该为灰色',
  `deleted` char(1) DEFAULT '0' COMMENT '设备信息是否删除(0存在,1删除)',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人id',
  `category_id` bigint(20) DEFAULT NULL COMMENT '设备类型id',
  PRIMARY KEY (`devive_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='设备信息表';

/*Data for the table `iot_device` */

insert  into `iot_device`(`devive_id`,`device_name`,`device_image`,`device_address`,`device_active_time`,`device_ip`,`status`,`deleted`,`comment`,`create_time`,`update_time`,`user_id`,`category_id`) values (1,'spd监测模块1','http://i0.hdslb.com/bfs/new_dyn/d388af98088b81ea6e7c5ffec013059b491524566.png','1107室大厅桌面','2022-07-29 12:55:32','192.168.0.7','0','0','status控制设备卡显示灰色','2022-07-29 12:48:43','2022-07-29 12:48:45',6,1);

/*Table structure for table `iot_event` */

DROP TABLE IF EXISTS `iot_event`;

CREATE TABLE `iot_event` (
  `event_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '异常事件id',
  `device_id` bigint(20) unsigned DEFAULT NULL COMMENT '设备id',
  `d1090` smallint(5) unsigned DEFAULT NULL,
  `d1100` smallint(5) unsigned DEFAULT NULL,
  `d1101` smallint(5) unsigned DEFAULT NULL,
  `d1102` smallint(5) unsigned DEFAULT NULL,
  `d1103` smallint(5) unsigned DEFAULT NULL,
  `d1104` smallint(5) unsigned DEFAULT NULL,
  `d1105` smallint(5) unsigned DEFAULT NULL,
  `d1106` smallint(5) unsigned DEFAULT NULL,
  `d1107` smallint(5) unsigned DEFAULT NULL,
  `d1108` smallint(5) unsigned DEFAULT NULL,
  `d1109` smallint(5) unsigned DEFAULT NULL,
  `d1110` smallint(5) unsigned DEFAULT NULL,
  `d1120-1365` varchar(512) DEFAULT NULL COMMENT '格式为二进制位数',
  `status` char(1) DEFAULT '0',
  `deleted` char(1) DEFAULT '0',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备异常事件表(根据事件编号来确定)';

/*Data for the table `iot_event` */

/*Table structure for table `iot_oper` */

DROP TABLE IF EXISTS `iot_oper`;

CREATE TABLE `iot_oper` (
  `oper_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '设备操作表id',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `obj_name` varchar(30) DEFAULT NULL COMMENT '操作对象名(和下面配套)',
  `obj_id` bigint(20) unsigned DEFAULT NULL COMMENT '操作对象id(如:param_id,device_id,controller_id...)',
  `oper_type` varchar(30) DEFAULT NULL COMMENT '操作类型:控制,主动查询,配置参数',
  `oper_result` varchar(30) DEFAULT NULL COMMENT '操作成功或失败',
  `error_msg` varchar(2000) DEFAULT NULL COMMENT '操作错误消息',
  `status` char(1) DEFAULT NULL,
  `deleted` char(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`oper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备操作表(记录用户对哪个设备进行了什么操作,)';

/*Data for the table `iot_oper` */

/*Table structure for table `iot_param` */

DROP TABLE IF EXISTS `iot_param`;

CREATE TABLE `iot_param` (
  `param_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '设备参数id',
  `device_id` bigint(20) unsigned DEFAULT NULL COMMENT '设备id',
  `d2000` smallint(5) unsigned DEFAULT NULL COMMENT '从站地址号',
  `d2001` smallint(5) unsigned DEFAULT NULL COMMENT '波特率 0:2400 1:4800 2:9600',
  `d2002` smallint(5) unsigned DEFAULT NULL COMMENT '工作模式:0本地监测,1报警联动',
  `d2003` smallint(5) unsigned DEFAULT NULL COMMENT '0三相四线1三相三线2单相',
  `d2004` smallint(5) unsigned DEFAULT NULL,
  `d2005` smallint(5) unsigned DEFAULT NULL,
  `d2006` smallint(5) unsigned DEFAULT NULL,
  `d2007` smallint(5) unsigned DEFAULT NULL,
  `d2008` smallint(5) unsigned DEFAULT NULL,
  `d2009` smallint(5) unsigned DEFAULT NULL,
  `d2011` smallint(5) unsigned DEFAULT NULL,
  `d2012` smallint(5) unsigned DEFAULT NULL,
  `d2013` smallint(5) unsigned DEFAULT NULL,
  `d2014` smallint(5) unsigned DEFAULT NULL,
  `d2015` smallint(5) unsigned DEFAULT NULL,
  `d2016` smallint(5) unsigned DEFAULT NULL,
  `d2017` smallint(5) unsigned DEFAULT NULL,
  `d2018` smallint(5) unsigned DEFAULT NULL,
  `d2019` smallint(5) unsigned DEFAULT NULL,
  `d2020` smallint(5) unsigned DEFAULT NULL,
  `d2021` smallint(5) unsigned DEFAULT NULL,
  `d2022` smallint(5) unsigned DEFAULT NULL,
  `d2023` smallint(5) unsigned DEFAULT NULL,
  `d2024` smallint(5) unsigned DEFAULT NULL,
  `d2025` smallint(5) unsigned DEFAULT NULL,
  `d2026` smallint(5) unsigned DEFAULT NULL,
  `d2027` smallint(5) unsigned DEFAULT NULL,
  `d2028` smallint(5) unsigned DEFAULT NULL,
  `d2029` smallint(5) unsigned DEFAULT NULL,
  `d2030` smallint(5) unsigned DEFAULT NULL,
  `d2031` smallint(5) unsigned DEFAULT NULL,
  `d2032` smallint(5) unsigned DEFAULT NULL,
  `d2080` smallint(5) unsigned DEFAULT NULL COMMENT '事件编号(0-65535)',
  `status` char(1) DEFAULT '0',
  `deleted` char(1) DEFAULT '0',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`param_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备参数表(每个运行设备的参数)';

/*Data for the table `iot_param` */

/*Table structure for table `iot_run` */

DROP TABLE IF EXISTS `iot_run`;

CREATE TABLE `iot_run` (
  `run_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '运行状态id',
  `device_id` bigint(20) DEFAULT NULL COMMENT '设备id',
  `d1002` varchar(16) DEFAULT '0000000000000000' COMMENT '数据格式为: 16位',
  `d1030` mediumint(8) DEFAULT NULL,
  `d1031` mediumint(8) DEFAULT NULL,
  `d1032` mediumint(8) DEFAULT NULL,
  `d1033` mediumint(8) DEFAULT NULL,
  `d1034` mediumint(8) DEFAULT NULL,
  `d1035` mediumint(8) DEFAULT NULL,
  `d1036` mediumint(8) DEFAULT NULL,
  `d1037` mediumint(8) DEFAULT NULL,
  `d1038` mediumint(8) DEFAULT NULL,
  `d1039` mediumint(8) DEFAULT NULL,
  `d1040` mediumint(8) DEFAULT NULL,
  `d1041` mediumint(8) DEFAULT NULL,
  `d1042` mediumint(8) DEFAULT NULL,
  `d1043` mediumint(8) DEFAULT NULL,
  `d1044` mediumint(8) DEFAULT NULL,
  `d1045` mediumint(8) DEFAULT NULL,
  `d1046` mediumint(8) DEFAULT NULL,
  `d1047` mediumint(8) DEFAULT NULL,
  `d1048` mediumint(8) DEFAULT NULL,
  `d1049` mediumint(8) DEFAULT NULL,
  `d1050` mediumint(8) DEFAULT NULL,
  `d1051` mediumint(8) DEFAULT NULL,
  `d1052` mediumint(8) DEFAULT NULL,
  `d1053` mediumint(8) DEFAULT NULL,
  `d1054` mediumint(8) DEFAULT NULL,
  `d1055` mediumint(8) DEFAULT NULL,
  `d1056` mediumint(8) DEFAULT NULL,
  `d1057` mediumint(8) DEFAULT NULL,
  `d1058` mediumint(8) DEFAULT NULL,
  `d1059` mediumint(8) DEFAULT NULL,
  `d1060` mediumint(8) DEFAULT NULL,
  `d1061` mediumint(8) DEFAULT NULL,
  `d1062` mediumint(8) DEFAULT NULL,
  `d1063` mediumint(8) DEFAULT NULL,
  `d1064` mediumint(8) DEFAULT NULL,
  `d1065` mediumint(8) DEFAULT NULL,
  `d1066` mediumint(8) DEFAULT NULL,
  `d1067` mediumint(8) DEFAULT NULL,
  `d1068` mediumint(8) DEFAULT NULL,
  `d1069` mediumint(8) DEFAULT NULL,
  `d1070` mediumint(8) DEFAULT NULL,
  `d1071` mediumint(8) DEFAULT NULL,
  `d1072` mediumint(8) DEFAULT NULL,
  `d1073` mediumint(8) DEFAULT NULL,
  `d1074` mediumint(8) DEFAULT NULL,
  `d1075` mediumint(8) DEFAULT NULL,
  `d1076` mediumint(8) DEFAULT NULL,
  `d1077` mediumint(8) DEFAULT NULL,
  `d1078` mediumint(8) DEFAULT NULL,
  `d1079` mediumint(8) DEFAULT NULL,
  `status` char(1) DEFAULT '0',
  `deleted` char(1) DEFAULT '0',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`run_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备运行状态表(需要定时器定时采集)';

/*Data for the table `iot_run` */

/*Table structure for table `iot_warm` */

DROP TABLE IF EXISTS `iot_warm`;

CREATE TABLE `iot_warm` (
  `warm_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '告警id',
  `device_id` bigint(20) DEFAULT NULL COMMENT '告警设备id',
  `d1020` varchar(16) DEFAULT NULL,
  `d1021` varchar(16) DEFAULT NULL,
  `status` char(1) DEFAULT '0',
  `deleted` char(1) DEFAULT '0',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`warm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='告警表(借助缓存提高告警性能,如果和缓存中数据不同那就存入数据库,相同就不存入)';

/*Data for the table `iot_warm` */

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '菜单id',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单id',
  `menu_name` varchar(30) DEFAULT NULL COMMENT '菜单名称',
  `path` varchar(200) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(120) DEFAULT NULL COMMENT '菜单图标',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态(0使用,1停用)',
  `deleted` char(1) DEFAULT '0' COMMENT '菜单是否删除(0使用,1删除)',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COMMENT='菜单表';

/*Data for the table `sys_menu` */

insert  into `sys_menu`(`menu_id`,`parent_id`,`menu_name`,`path`,`component`,`perms`,`icon`,`status`,`deleted`,`comment`,`create_time`,`update_time`) values (1,0,'设备管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2,0,'用户管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(3,0,'系统管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(101,1,'设备信息管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(102,1,'设备参数管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(103,1,'设备告警管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(201,2,'用户信息管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(202,2,'用户角色管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(203,2,'用户组织管理',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2011,201,'用户增加',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2012,201,'用户删除',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2013,201,'用户修改',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2014,201,'用户查询',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2021,202,'角色增加',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2022,202,'角色删除',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2023,202,'角色修改',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL),(2024,202,'角色查询',NULL,NULL,NULL,NULL,'0','0',NULL,NULL,NULL);

/*Table structure for table `sys_org` */

DROP TABLE IF EXISTS `sys_org`;

CREATE TABLE `sys_org` (
  `org_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '组织id',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级组织id',
  `org_name` varchar(30) DEFAULT NULL COMMENT '组织名字',
  `leader` varchar(30) DEFAULT NULL COMMENT '负责人',
  `phone_number` char(11) DEFAULT NULL COMMENT '组织电话号码',
  `email` varchar(50) DEFAULT NULL COMMENT '组织邮箱',
  `status` char(1) DEFAULT '0' COMMENT '组织状态(0正常,1禁用)',
  `deleted` char(1) DEFAULT '0' COMMENT '组织信息是否删除(0存在,1删除)',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`org_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='组织部门表(与用户表1对1)';

/*Data for the table `sys_org` */

insert  into `sys_org`(`org_id`,`parent_id`,`org_name`,`leader`,`phone_number`,`email`,`status`,`deleted`,`comment`,`create_time`,`update_time`) values (1,0,'长沙集控中心','cat','12345678900','ab@qq.com','0','0','父id为0表示无上级组织','2022-07-27 14:35:30','2022-07-27 14:35:32'),(2,1,'风创风场','greenCat','11223344556','cc@yy.com','0','0',NULL,'2022-07-27 14:41:48','2022-07-27 14:41:50'),(3,1,'沃力特风场','whiteCat','12344556667','dd@ss.com','0','0',NULL,'2022-07-27 14:41:46','2022-07-27 14:41:51'),(4,2,'风创风场运维部','blueCat','13144241441','yw.cc@yy.com','0','0',NULL,'2022-07-27 14:45:21','2022-07-27 14:45:24'),(5,2,'风创风场技术部','redCat','12321445666','js.cc@yy.com','0','0',NULL,'2022-07-27 14:46:30','2022-07-27 14:46:32'),(6,2,'风创风场现场部','blackCat','12332112321','xz.cc@yy.com','0','0',NULL,'2022-07-29 12:53:24','2022-07-29 12:53:26');

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(30) DEFAULT NULL COMMENT '角色名称',
  `status` char(1) DEFAULT '0' COMMENT '角色状态(0正常,1禁用)',
  `deleted` char(1) DEFAULT '0' COMMENT '角色是否删除(0存在,1删除)',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='角色表(与用户表1对1)';

/*Data for the table `sys_role` */

insert  into `sys_role`(`role_id`,`role_name`,`status`,`deleted`,`comment`,`create_time`,`update_time`) values (1,'超级管理员','0','0','修改系统配置(开发人员)','2022-07-27 12:42:52','2022-07-27 12:42:54'),(2,'集控管理员','1','1','管理各个风场(禁用)','2022-07-27 12:55:44','2022-07-27 12:55:49'),(3,'风创管理员','0','0','本风场(假设部署在风创)','2022-07-27 12:55:47','2022-07-27 12:55:50'),(4,'沃力特管理员','1','1','沃力特管理员(禁用)','2022-07-27 12:57:29','2022-07-27 12:57:32'),(5,'普通用户','0','0','值班员工使用','2022-07-27 12:57:30','2022-07-27 12:57:33'),(6,'风创定制角色1','0','0','权限由集控或风创给定','2022-07-27 13:10:42','2022-07-27 13:10:48'),(7,'风创定制角色2','0','0',NULL,'2022-07-27 13:10:44','2022-07-27 13:10:50'),(8,'沃力特定制角色1','1','1','权限由集控或沃力特给定','2022-07-27 13:10:45','2022-07-27 13:10:51'),(9,'沃力特定制角色2','1','1',NULL,'2022-07-27 13:10:47','2022-07-27 13:10:53');

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `role_menu_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色菜单表id',
  `role_id` bigint(20) DEFAULT NULL COMMENT '用户表id',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单表id',
  `status` char(1) DEFAULT '0' COMMENT '本条权限是否被禁用(0没有,1禁用)',
  `deleted` char(1) DEFAULT '0' COMMENT '本条权限是否被删除(0没有,1删除)',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`role_menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf32 COMMENT='角色菜单权限表(属于多多对多关系)';

/*Data for the table `sys_role_menu` */

insert  into `sys_role_menu`(`role_menu_id`,`role_id`,`menu_id`,`status`,`deleted`,`comment`,`create_time`,`update_time`) values (1,1,1,'0','0',NULL,'2022-07-27 14:58:38','2022-07-27 14:58:39'),(2,1,2,'0','0',NULL,'2022-07-27 14:59:40','2022-07-27 14:59:45'),(3,1,3,'0','0',NULL,'2022-07-27 14:59:42','2022-07-27 14:59:47'),(4,1,4,'0','0',NULL,'2022-07-27 14:59:43','2022-07-27 14:59:48');

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(30) DEFAULT NULL COMMENT '用户账号',
  `password` char(60) DEFAULT NULL COMMENT '账号密码,60位加密',
  `nick_name` varchar(30) DEFAULT NULL COMMENT '用户昵称',
  `gender` char(1) DEFAULT NULL COMMENT '用户性别(0男,1女,2未知)',
  `phone_number` char(11) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(50) DEFAULT NULL COMMENT '用户邮箱',
  `avatar` varchar(120) DEFAULT 'https://i0.hdslb.com/bfs/new_dyn/9089af00b113da73685fba2dbdcb1111491524566.png' COMMENT '用户头像',
  `status` char(1) DEFAULT '0' COMMENT '账号状态(0正常,1禁用)',
  `deleted` char(1) DEFAULT '0' COMMENT '账号是否删除(0存在,1删除)',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `org_id` bigint(20) DEFAULT NULL COMMENT '组织id',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色id(角色和用户一对一)',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10011 DEFAULT CHARSET=utf8 COMMENT='用户表';

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`user_name`,`password`,`nick_name`,`gender`,`phone_number`,`email`,`avatar`,`status`,`deleted`,`comment`,`create_time`,`update_time`,`org_id`,`role_id`) values (10010,'admin','$2a$10$v8Mjzfk/IuwCTNrJWJQrp.scUGiO0B9ek505ddwgsippmWxOwq85S','cat','0','12345678900','ab@qq.com','https://i0.hdslb.com/bfs/face/member/noface.jpg@96w_96h_1c_1s.webp','0','0','密码4455','2022-07-27 14:11:31','2022-07-27 14:11:40',NULL,NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
