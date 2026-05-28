/*
 Navicat Premium Data Transfer

 Source Server         : mysql8.0
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : rijie

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 27/12/2025 10:20:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attendance
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `seeker_id` bigint(0) NOT NULL,
  `job_id` bigint(0) NOT NULL,
  `work_date` date NOT NULL,
  `sign_in_time` datetime(0) NULL DEFAULT NULL,
  `sign_out_time` datetime(0) NULL DEFAULT NULL,
  `attendance_status` tinyint(0) NULL DEFAULT 0 COMMENT '0-正常，1-迟到，2-早退，3-旷工',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `seeker_id`(`seeker_id`) USING BTREE,
  INDEX `job_id`(`job_id`) USING BTREE,
  CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`seeker_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `attendance_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `part_time_job` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考勤表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attendance
-- ----------------------------
INSERT INTO `attendance` VALUES (2, 8, 11, '2025-12-17', '2025-12-17 10:20:00', '2025-12-17 20:00:00', 2);
INSERT INTO `attendance` VALUES (3, 13, 24, '2025-12-16', '2025-12-17 10:20:00', '2025-12-17 20:00:00', 1);

-- ----------------------------
-- Table structure for employer_info
-- ----------------------------
DROP TABLE IF EXISTS `employer_info`;
CREATE TABLE `employer_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `company_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `business_license` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_person` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `employer_info_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '雇主信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employer_info
-- ----------------------------
INSERT INTO `employer_info` VALUES (1, 2, 'XX餐饮管理有限公司', '北京市朝阳区建国路88号', NULL, '张三');
INSERT INTO `employer_info` VALUES (2, 3, 'XX快递服务有限公司', '上海市浦东新区张江路100号', NULL, '李四');
INSERT INTO `employer_info` VALUES (3, 4, 'XX促销策划有限公司', '广州市天河区天河路385号', NULL, '王五');

-- ----------------------------
-- Table structure for job_application
-- ----------------------------
DROP TABLE IF EXISTS `job_application`;
CREATE TABLE `job_application`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `seeker_id` bigint(0) NOT NULL,
  `job_id` bigint(0) NOT NULL,
  `apply_status` tinyint(0) NULL DEFAULT 0 COMMENT '0-待审核，1-已通过，2-已拒绝，3-已入职，4-已离职',
  `apply_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `seeker_id`(`seeker_id`) USING BTREE,
  INDEX `job_id`(`job_id`) USING BTREE,
  CONSTRAINT `job_application_ibfk_1` FOREIGN KEY (`seeker_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `job_application_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `part_time_job` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '求职申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_application
-- ----------------------------
INSERT INTO `job_application` VALUES (8, 8, 11, 3, '2025-12-17 03:24:52');
INSERT INTO `job_application` VALUES (9, 13, 24, 3, '2025-12-17 04:02:30');
INSERT INTO `job_application` VALUES (10, 13, 24, 3, '2025-12-17 04:03:47');
INSERT INTO `job_application` VALUES (12, 5, 11, 0, '2025-12-21 10:00:02');
INSERT INTO `job_application` VALUES (13, 8, 12, 0, '2025-12-21 11:22:41');

-- ----------------------------
-- Table structure for job_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `job_evaluation`;
CREATE TABLE `job_evaluation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(0) NOT NULL,
  `seeker_id` bigint(0) NOT NULL,
  `employer_id` bigint(0) NOT NULL,
  `score` tinyint(0) NOT NULL COMMENT '1-5星',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价标签，逗号分隔',
  `is_anonymous` tinyint(0) NULL DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '1正常 0禁用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `job_id`(`job_id`) USING BTREE,
  INDEX `seeker_id`(`seeker_id`) USING BTREE,
  INDEX `employer_id`(`employer_id`) USING BTREE,
  CONSTRAINT `job_evaluation_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `part_time_job` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `job_evaluation_ibfk_2` FOREIGN KEY (`seeker_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `job_evaluation_ibfk_3` FOREIGN KEY (`employer_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for job_seeker_info
-- ----------------------------
DROP TABLE IF EXISTS `job_seeker_info`;
CREATE TABLE `job_seeker_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `gender` tinyint(0) NULL DEFAULT NULL COMMENT '0-女，1-男，2-未知',
  `age` tinyint(0) NULL DEFAULT NULL,
  `education` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `skills` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '技能标签，逗号分隔',
  `bank_account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `bank_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `job_seeker_info_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '求职者信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_seeker_info
-- ----------------------------

-- ----------------------------
-- Table structure for part_time_job
-- ----------------------------
DROP TABLE IF EXISTS `part_time_job`;
CREATE TABLE `part_time_job`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `employer_id` bigint(0) NOT NULL,
  `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `work_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `work_start_time` time(0) NOT NULL,
  `work_end_time` time(0) NOT NULL,
  `daily_salary` decimal(10, 2) NOT NULL,
  `recruit_num` int(0) NOT NULL,
  `job_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `job_require` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `publish_status` tinyint(0) NULL DEFAULT 0 COMMENT '0-草稿，1-已发布，2-已结束',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `employer_id`(`employer_id`) USING BTREE,
  CONSTRAINT `part_time_job_ibfk_1` FOREIGN KEY (`employer_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '日结兼职岗位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of part_time_job
-- ----------------------------
INSERT INTO `part_time_job` VALUES (11, 2, '餐饮服务员（日结）', '餐饮', '北京市朝阳区建国路88号XX餐厅', '10:00:00', '20:00:00', 150.00, 5, '负责餐厅的点餐、上菜、收拾餐桌等工作，工作轻松，包午餐', '18-35岁，有餐饮服务经验优先，吃苦耐劳', 1, '2025-12-16 02:56:12', '2025-12-16 02:56:12');
INSERT INTO `part_time_job` VALUES (12, 2, '后厨帮工（日结）', '餐饮', '北京市朝阳区建国路88号XX餐厅', '09:00:00', '19:00:00', 160.00, 3, '负责食材清洗、切配、餐具消毒等后厨辅助工作', '18-40岁，无经验可教，手脚麻利', 1, '2025-12-16 02:56:12', '2025-12-16 02:56:12');
INSERT INTO `part_time_job` VALUES (13, 3, '快递分拣员（日结）', '快递', '上海市浦东新区张江路100号快递仓库', '18:00:00', '02:00:00', 200.00, 20, '负责快递包裹的分拣、扫描、打包工作，夜班岗位', '18-45岁，能适应夜班，无经验可培训', 1, '2025-12-16 02:56:12', '2025-12-16 02:56:12');
INSERT INTO `part_time_job` VALUES (14, 3, '快递配送员（日结）', '快递', '上海市浦东新区张江路100号周边区域', '08:00:00', '18:00:00', 220.00, 10, '负责区域内的快递配送工作，自带电动车优先', '18-40岁，熟悉上海浦东路况，有配送经验优先', 1, '2025-12-16 02:56:12', '2025-12-16 02:56:12');
INSERT INTO `part_time_job` VALUES (15, 3, '仓库理货员（日结）', '快递', '上海市浦东新区张江路100号快递仓库', '09:00:00', '18:00:00', 180.00, 8, '负责仓库货物的整理、上架、盘点工作', '18-45岁，吃苦耐劳，有仓库工作经验优先', 1, '2025-12-16 02:56:12', '2025-12-16 02:56:12');
INSERT INTO `part_time_job` VALUES (18, 4, '展会协助（日结）', '促销', '广州市琶洲国际采购中心', '08:30:00', '17:30:00', 200.00, 6, '负责展会的布展、观众引导、展品讲解等工作', '18-35岁，有展会协助经验优先，普通话标准', 0, '2025-12-16 02:56:12', '2025-12-21 13:30:39');
INSERT INTO `part_time_job` VALUES (19, 2, '家教老师（小学辅导）', '家教', '北京市海淀区中关村南大街5号', '14:00:00', '17:00:00', 80.00, 4, '负责小学语数英作业辅导，讲解知识点', '本科及以上学历，有家教经验，师范专业优先', 1, '2025-12-16 02:56:12', '2025-12-16 02:56:12');
INSERT INTO `part_time_job` VALUES (20, 3, '超市理货员（日结）', '零售', '上海市浦东新区张江路100号XX超市', '09:00:00', '17:00:00', 140.00, 5, '负责超市商品的上架、整理、补货工作', '18-45岁，能适应站立工作，有超市工作经验优先', 1, '2025-12-16 02:56:12', '2025-12-16 02:56:12');
INSERT INTO `part_time_job` VALUES (21, 2, '奶茶店店员（日结）', '餐饮', '北京市朝阳区三里屯', '10:00:00', '20:00:00', 160.00, 3, '负责奶茶制作和顾客点单', '18-30岁，有奶茶店工作经验优先', 1, '2025-12-16 03:39:05', '2025-12-16 03:39:05');
INSERT INTO `part_time_job` VALUES (22, 11, '餐饮服务员（日结）', '餐饮', '北京市朝阳区三里屯', '10:00:00', '20:00:00', 150.00, 3, '负责点餐和收拾餐桌', '18-30岁，能吃苦耐劳', 1, '2025-12-17 03:23:49', '2025-12-17 03:23:49');
INSERT INTO `part_time_job` VALUES (24, 12, '卖猪肉服务员（日结）', '餐饮', '北京市朝阳区三里屯', '10:00:00', '20:00:00', 150.00, 3, '负责点餐和收拾餐桌', '18-30岁，能吃苦耐劳', 1, '2025-12-17 03:57:08', '2025-12-17 03:57:08');
INSERT INTO `part_time_job` VALUES (25, 2, '奶茶店店员', '餐饮', '北京市朝阳区三里屯', '10:00:00', '20:00:00', 160.00, 3, '负责奶茶制作和顾客点单', '18-30岁，有奶茶店工作经验优先', 1, '2025-12-21 12:26:43', '2025-12-21 12:26:43');
INSERT INTO `part_time_job` VALUES (26, 2, '奶茶店店员（日结）', '餐饮', '北京市朝阳区三里屯', '10:00:00', '20:00:00', 160.00, 3, '负责奶茶制作和顾客点单', '18-30岁，有奶茶店工作经验优先', NULL, '2025-12-21 12:28:17', '2025-12-21 12:28:17');
INSERT INTO `part_time_job` VALUES (27, 2, '奶茶店店员（日结）', '餐饮', '北京市朝阳区三里屯', '10:00:00', '20:00:00', 160.00, 3, '负责奶茶制作和顾客点单', NULL, NULL, '2025-12-21 12:28:25', '2025-12-21 12:28:25');
INSERT INTO `part_time_job` VALUES (28, 2, '奶茶店店员（日结）', '餐饮', '北京市朝阳区三里屯', '10:00:00', '20:00:00', 160.00, 3, '负责奶茶制作和顾客点单', '18-30岁，有奶茶店工作经验优先', 1, '2025-12-21 12:29:48', '2025-12-21 12:29:48');

-- ----------------------------
-- Table structure for salary
-- ----------------------------
DROP TABLE IF EXISTS `salary`;
CREATE TABLE `salary`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `seeker_id` bigint(0) NOT NULL,
  `job_id` bigint(0) NOT NULL,
  `work_date` date NOT NULL,
  `salary_amount` decimal(10, 2) NOT NULL,
  `pay_status` tinyint(0) NULL DEFAULT 0 COMMENT '0-待发放，1-已发放',
  `pay_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `seeker_id`(`seeker_id`) USING BTREE,
  INDEX `job_id`(`job_id`) USING BTREE,
  CONSTRAINT `salary_ibfk_1` FOREIGN KEY (`seeker_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `salary_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `part_time_job` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '薪资表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of salary
-- ----------------------------
INSERT INTO `salary` VALUES (1, 13, 24, '2025-12-16', 130.00, 0, NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `real_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role_type` tinyint(0) NOT NULL COMMENT '0-管理员，1-雇主，2-求职者',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '0-禁用，1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `skill_tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '技能标签（逗号分隔，如餐饮,收银）',
  `resident_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '常住地址',
  `reputation_score` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '信誉分(0-5分)',
  `total_ratings` int(0) NULL DEFAULT 0 COMMENT '总评价数',
  `positive_ratings` int(0) NULL DEFAULT 0 COMMENT '好评数(4-5星)',
  `negative_ratings` int(0) NULL DEFAULT 0 COMMENT '差评数(1-2星)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `phone`(`phone`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '系统管理员', '13800138000', 'admin@ptj.com', 0, 1, '2025-12-16 02:55:56', '2025-12-16 02:55:56', NULL, NULL);
INSERT INTO `sys_user` VALUES (2, 'employer01', '123456', '张三', '13800138001', 'employer01@ptj.com', 1, 1, '2025-12-16 02:55:56', '2025-12-16 02:55:56', NULL, NULL);
INSERT INTO `sys_user` VALUES (3, 'employer02', '123456', '李四', '13800138002', 'employer02@ptj.com', 1, 1, '2025-12-16 02:55:56', '2025-12-16 02:55:56', NULL, NULL);
INSERT INTO `sys_user` VALUES (4, 'employer03', '123456', '王五', '13800138003', 'employer03@ptj.com', 1, 1, '2025-12-16 02:55:56', '2025-12-16 02:55:56', NULL, NULL);
INSERT INTO `sys_user` VALUES (5, 'seeker01', '123456', '小明', '13800138004', 'seeker01@ptj.com', 2, 1, '2025-12-16 04:32:04', '2025-12-16 04:32:04', NULL, NULL);
INSERT INTO `sys_user` VALUES (8, 'seeker02', '123456', '小刚', '13800138005', 'seeker02@ptj.com', 2, 1, '2025-12-17 03:19:03', '2025-12-17 03:19:03', NULL, NULL);
INSERT INTO `sys_user` VALUES (11, 'employer04', '123456', '餐饮老板', '13800138012', 'employer04@ptj.com', 1, 1, '2025-12-17 03:21:19', '2025-12-17 03:21:19', NULL, NULL);
INSERT INTO `sys_user` VALUES (12, 'employer05', '123456', '卖猪肉老板', '13800138015', 'employer05@ptj.com', 1, 1, '2025-12-17 03:56:39', '2025-12-17 03:56:39', NULL, NULL);
INSERT INTO `sys_user` VALUES (13, 'seeker03', '123456', '小王', '13800138016', 'seeker03@ptj.com', 2, 1, '2025-12-17 03:56:52', '2025-12-17 03:56:52', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
