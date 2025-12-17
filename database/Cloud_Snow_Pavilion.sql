/*
 Navicat Premium Dump SQL

 Source Server         : WSL_MySQL
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44-0ubuntu0.24.04.1)
 Source Host           : 172.31.142.67:3306
 Source Schema         : Cloud_Snow_Pavilion

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44-0ubuntu0.24.04.1)
 File Encoding         : 65001

 Date: 17/12/2025 16:37:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for carousel
-- ----------------------------
DROP TABLE IF EXISTS `carousel`;
CREATE TABLE `carousel`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `img_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `jump_type` tinyint NULL DEFAULT 0 COMMENT '跳转类型：0-不跳转 1-商品详情 2-页面路径 3-外部链接',
  `jump_val` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转值（商品ID/页面路径/外部链接）',
  `sort` int NULL DEFAULT 0 COMMENT '排序值，数值越小越靠前',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_carousel_tenant`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_carousel_status_sort`(`status` ASC, `sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of carousel
-- ----------------------------
INSERT INTO `carousel` VALUES (2, 1, '图片1', '/upload_img/Carousel/a114aff66d65401ab73d4afa670e675e.jpg', 0, '', 0, 1, '2025-12-17 16:26:34', '2025-12-17 16:34:31');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint UNSIGNED NULL DEFAULT 0 COMMENT '父分类ID',
  `sort` int NULL DEFAULT 0 COMMENT '排序值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_tenant`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 1, '招牌柠檬茶', 0, 1);
INSERT INTO `category` VALUES (2, 1, '冰柠茶系列', 0, 2);
INSERT INTO `category` VALUES (3, 1, '周边套餐', 0, 3);
INSERT INTO `category` VALUES (4, 1, '小料加购', 0, 4);
INSERT INTO `category` VALUES (5, 1, '测试', 0, 5);

-- ----------------------------
-- Table structure for content
-- ----------------------------
DROP TABLE IF EXISTS `content`;
CREATE TABLE `content`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `type` tinyint NOT NULL COMMENT '类型：1 Banner 2 金刚区 3 品牌新鲜事',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片地址',
  `jump_type` tinyint NULL DEFAULT NULL COMMENT '跳转类型',
  `jump_val` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转值（URL/ID）',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `publish_time` timestamp NULL DEFAULT NULL COMMENT '发布时间（品牌动态用）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_content_tenant_type`(`tenant_id` ASC, `type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content
-- ----------------------------
INSERT INTO `content` VALUES (1, 1, 1, '首页Banner', 'https://img.linlee.com/banner/invite.png', 1, '/pages/invite/index', 1, '2025-11-22 17:34:06');
INSERT INTO `content` VALUES (2, 1, 2, '堂食点单', '', 2, '/pages/order/take', 1, NULL);
INSERT INTO `content` VALUES (3, 1, 2, '外送到家', '', 2, '/pages/order/delivery', 2, NULL);
INSERT INTO `content` VALUES (4, 1, 2, '林里团餐', '', 2, '/pages/group/index', 3, NULL);
INSERT INTO `content` VALUES (5, 1, 2, '鸭币商城', '', 2, '/pages/coin/index', 4, NULL);
INSERT INTO `content` VALUES (6, 1, 2, '社群专属', '', 2, '/pages/community/index', 5, NULL);
INSERT INTO `content` VALUES (7, 1, 2, '加盟咨询', '', 2, '/pages/join/index', 6, NULL);
INSERT INTO `content` VALUES (8, 1, 3, '夏日在【桃】新品上线', 'https://img.linlee.com/news/peach.jpg', 3, '/pages/news/1', 1, '2025-11-22 17:34:06');
INSERT INTO `content` VALUES (9, 1, 3, '品牌 2 周年庆 全场 8.5 折', 'https://img.linlee.com/news/2nd.jpg', 3, '/pages/news/2', 2, '2025-11-21 17:34:06');
INSERT INTO `content` VALUES (10, 1, 3, '“桃桃乌龙”获国际柠檬茶大赛金奖', 'https://img.linlee.com/news/gold.jpg', 3, '/pages/news/3', 3, '2025-11-20 17:34:06');
INSERT INTO `content` VALUES (11, 1, 3, '武汉 618 外卖节：指定商品 2 杯 8 折', 'https://img.linlee.com/news/618.jpg', 3, '/pages/news/4', 4, '2025-11-19 17:34:06');

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `coupon_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `threshold` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '使用门槛',
  `amount` decimal(10, 2) NOT NULL COMMENT '抵扣金额',
  `start_time` timestamp NOT NULL COMMENT '有效期开始',
  `end_time` timestamp NOT NULL COMMENT '有效期结束',
  `total_count` int NOT NULL COMMENT '发放总量',
  `stock` int NOT NULL COMMENT '剩余库存',
  PRIMARY KEY (`coupon_id`) USING BTREE,
  INDEX `idx_coupon_tenant`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (1, 1, '新注册立减 3 元券', 0.00, 3.00, '2025-11-22 17:34:06', '2025-12-22 17:34:06', 9999, 9999);
INSERT INTO `coupon` VALUES (2, 1, '邀请好友 9.9 饮品券', 0.00, 9.90, '2025-11-22 17:34:06', '2025-12-22 17:34:06', 9999, 9999);
INSERT INTO `coupon` VALUES (3, 1, '满 30 减 5 元券', 30.00, 5.00, '2025-11-22 17:34:06', '2025-12-07 17:34:06', 5000, 5000);
INSERT INTO `coupon` VALUES (4, 1, '会员专属 8 折券', 20.00, 0.00, '2025-11-22 17:34:06', '2025-11-29 17:34:06', 2000, 2000);

-- ----------------------------
-- Table structure for ingredient
-- ----------------------------
DROP TABLE IF EXISTS `ingredient`;
CREATE TABLE `ingredient`  (
  `ingredient_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '原料ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原料名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '计量单位',
  `current_stock` int NULL DEFAULT 0 COMMENT '当前库存',
  `min_stock` int NULL DEFAULT 0 COMMENT '最小警戒库存',
  `status` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '状态',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ingredient_id`) USING BTREE,
  INDEX `idx_ingredient_tenant`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '原料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ingredient
-- ----------------------------
INSERT INTO `ingredient` VALUES (1, 1, '柠檬', '水果', '个', 500, 50, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (2, 1, '鸭屎香单丛茶', '茶叶', '克', 2000, 200, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (3, 1, '五窖茉莉茶', '茶叶', '克', 2000, 200, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (4, 1, '黄桃果肉', '配料', '克', 1000, 100, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (5, 1, '百香果酱', '配料', '克', 1500, 150, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (6, 1, '椰椰浆', '配料', '毫升', 1000, 100, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (7, 1, '白葡萄酱', '配料', '克', 800, 80, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (8, 1, '西瓜酱', '配料', '克', 800, 80, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (9, 1, '一次性 700ml 杯', '包材', '个', 3000, 300, 1, '2025-11-22 17:34:06');
INSERT INTO `ingredient` VALUES (10, 1, '吸管', '包材', '根', 5000, 500, 1, '2025-11-22 17:34:06');

-- ----------------------------
-- Table structure for ingredient_stock_log
-- ----------------------------
DROP TABLE IF EXISTS `ingredient_stock_log`;
CREATE TABLE `ingredient_stock_log`  (
  `log_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `ingredient_id` bigint UNSIGNED NOT NULL COMMENT '原料ID',
  `change_type` tinyint UNSIGNED NOT NULL COMMENT '类型：1入库 2出库',
  `quantity` int NOT NULL COMMENT '数量变化',
  `operator_id` bigint UNSIGNED NOT NULL COMMENT '操作人ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_stock_tenant_ingredient`(`tenant_id` ASC, `ingredient_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '原料库存日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ingredient_stock_log
-- ----------------------------
INSERT INTO `ingredient_stock_log` VALUES (1, 1, 1, 1, 1000, 1, '采购柠檬 1000 个', '2025-11-22 17:34:06');
INSERT INTO `ingredient_stock_log` VALUES (2, 1, 2, 1, 5000, 1, '采购鸭屎香茶叶 5kg', '2025-11-22 17:34:06');
INSERT INTO `ingredient_stock_log` VALUES (3, 1, 9, 1, 5000, 1, '采购 700ml 杯 5000 套', '2025-11-22 17:34:06');

-- ----------------------------
-- Table structure for invite_log
-- ----------------------------
DROP TABLE IF EXISTS `invite_log`;
CREATE TABLE `invite_log`  (
  `invite_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '邀请ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `invite_user_id` bigint UNSIGNED NOT NULL COMMENT '邀请人ID',
  `new_user_id` bigint UNSIGNED NOT NULL COMMENT '被邀请用户ID',
  `reward_coupon_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '奖励券ID',
  `status` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`invite_id`) USING BTREE,
  INDEX `idx_invite_tenant_user`(`tenant_id` ASC, `invite_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邀请记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of invite_log
-- ----------------------------
INSERT INTO `invite_log` VALUES (1, 1, 10, 2, 2, 1, '2025-11-22 17:34:06');
INSERT INTO `invite_log` VALUES (2, 1, 10, 3, 2, 1, '2025-11-22 17:34:06');
INSERT INTO `invite_log` VALUES (3, 1, 10, 4, 2, 1, '2025-11-22 17:34:06');
INSERT INTO `invite_log` VALUES (4, 1, 10, 5, 2, 1, '2025-11-22 17:34:06');
INSERT INTO `invite_log` VALUES (5, 1, 10, 6, 2, 1, '2025-11-22 17:34:06');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `order_id` bigint UNSIGNED NOT NULL COMMENT '订单ID',
  `sku_id` bigint UNSIGNED NOT NULL COMMENT 'SKU ID',
  `quantity` int NOT NULL COMMENT '数量',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_item_tenant_order`(`tenant_id` ASC, `order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 1, 7, 1, 28.90);
INSERT INTO `order_item` VALUES (2, 1, 2, 8, 1, 19.00);
INSERT INTO `order_item` VALUES (3, 1, 3, 1, 2, 16.90);
INSERT INTO `order_item` VALUES (4, 1, 4, 9, 1, 19.00);
INSERT INTO `order_item` VALUES (5, 1, 5, 2, 1, 16.90);
INSERT INTO `order_item` VALUES (6, 1, 6, 1, 1, 16.90);
INSERT INTO `order_item` VALUES (7, 1, 6, 3, 1, 18.90);
INSERT INTO `order_item` VALUES (8, 1, 7, 1, 1, 16.90);
INSERT INTO `order_item` VALUES (9, 1, 8, 3, 1, 18.90);
INSERT INTO `order_item` VALUES (10, 1, 9, 3, 1, 18.90);

-- ----------------------------
-- Table structure for order_main
-- ----------------------------
DROP TABLE IF EXISTS `order_main`;
CREATE TABLE `order_main`  (
  `order_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `order_type` enum('take','delivery') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型：take自取 / delivery外卖',
  `status` tinyint NOT NULL COMMENT '订单状态（状态机）',
  `amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `idx_order_tenant_user`(`tenant_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_main
-- ----------------------------
INSERT INTO `order_main` VALUES (1, 1, 1, 'take', 4, 28.90, '2025-11-22 17:34:06', '2025-11-22 17:34:06');
INSERT INTO `order_main` VALUES (2, 1, 2, 'delivery', 4, 19.00, '2025-11-22 17:34:06', '2025-11-22 17:34:06');
INSERT INTO `order_main` VALUES (3, 1, 3, 'take', 3, 57.80, '2025-11-22 17:34:06', '2025-11-22 17:34:06');
INSERT INTO `order_main` VALUES (4, 1, 8, 'delivery', 3, 19.00, '2025-11-22 17:34:06', '2025-11-22 17:34:06');
INSERT INTO `order_main` VALUES (5, 1, 9, 'take', 3, 16.90, NULL, '2025-11-22 17:34:06');
INSERT INTO `order_main` VALUES (6, 1, 12, 'take', 0, 35.80, NULL, '2025-12-17 10:01:00');
INSERT INTO `order_main` VALUES (7, 1, 12, 'take', 0, 16.90, NULL, '2025-12-17 10:04:15');
INSERT INTO `order_main` VALUES (8, 1, 0, 'take', 0, 18.90, NULL, '2025-12-17 12:35:13');
INSERT INTO `order_main` VALUES (9, 1, 12, 'take', 0, 18.90, NULL, '2025-12-17 12:39:43');

-- ----------------------------
-- Table structure for points_log
-- ----------------------------
DROP TABLE IF EXISTS `points_log`;
CREATE TABLE `points_log`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `points` int NOT NULL COMMENT '积分变化（正负）',
  `type` enum('order','sign','invite') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_points_tenant_user`(`tenant_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_log
-- ----------------------------
INSERT INTO `points_log` VALUES (1, 1, 1, 128, 'sign', '连续签到 7 天', '2025-11-22 17:34:06');
INSERT INTO `points_log` VALUES (2, 1, 3, 520, 'invite', '邀请 5 位好友', '2025-11-22 17:34:06');
INSERT INTO `points_log` VALUES (3, 1, 9, 999, 'order', '累计消费满 999 元', '2025-11-22 17:34:06');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '副标题',
  `price` decimal(10, 2) NOT NULL COMMENT '售价',
  `origin_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `discount` decimal(5, 2) NULL DEFAULT NULL COMMENT '折扣（如9.5 代表95折）',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品主图',
  `stock` int NULL DEFAULT 0 COMMENT '总库存（按SKU汇总）',
  `status` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '状态 1上架 0下架',
  `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '分类ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_tenant`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_product_category`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 1, '招牌手打柠檬茶', '镇店之宝 · 每日现锤', 16.90, 16.90, 10.00, '/upload_img/e869f23f-6785-417b-b1fc-f517772daa70.jpg', 999, 1, 1, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (2, 1, '单丛鸭屎香手打柠檬茶', '茶香浓郁 回甘悠长', 18.90, 18.90, 10.00, '/upload_img/166ab402-c862-4372-a697-66741b2f4bdb.jpg', 999, 1, 1, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (3, 1, '五窖茉莉手打柠檬茶', '茉莉清香 清爽解腻', 18.90, 18.90, 10.00, '/upload_img/770d30a6-eb8f-4394-bf82-2957081e9f3a.jpg', 999, 1, 1, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (4, 1, '超C桃桃单杯周边套餐', '含限定桃桃鸭手机绳', 28.90, 34.00, 8.50, '/upload_img/340876ad-8acb-4624-b73b-171e38e8df66.jpg', 500, 1, 3, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (5, 1, '超爽霸王杯（桃桃乌龙）', '1000ml 大块果肉', 19.00, 19.00, 10.00, '/upload_img/71c510a8-c48c-42ba-9cbc-c51bb6327adb.jpg', 800, 1, 2, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (6, 1, '超C百香桃柠茶', '三重果香 层次分明', 19.00, 19.00, 10.00, '/upload_img/96b19baa-6191-4e90-bc25-45f3ce978ec2.jpg', 800, 1, 2, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (7, 1, '白葡萄冰柠茶', '当季巨峰 甜爽爆汁', 17.90, 17.90, 10.00, '/upload_img/c4ef9281-5426-4f86-8841-88ee1a1d7576.jpg', 800, 1, 2, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (8, 1, '西瓜冰柠茶', '无籽麒麟瓜 清爽一夏', 17.90, 17.90, 10.00, '/upload_img/81ce869a-cceb-4530-955c-2142f48793d1.jpg', 800, 1, 2, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (9, 1, '椰椰冰柠茶', '生椰乳 + 柠檬 独特椰香', 19.90, 19.90, 10.00, '/upload_img/153a25a2-19ee-4430-9973-4dc0ebc72e1b.jpg', 800, 1, 2, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (10, 1, '原叶鲜 · 甄奶茶', '鲜柠撞茶 冷热皆宜', 15.90, 15.90, 10.00, '/upload_img/563d3c90-b5ae-44b4-96bd-3f0633c011df.jpg', 800, 1, 1, '2025-11-22 17:34:06');
INSERT INTO `product` VALUES (12, 1, '1', '1', 0.00, 0.00, NULL, '/upload_img/08d27c78-38b9-4361-b900-c99dfc675c7f.jpg', 0, 1, 1, '2025-11-22 20:19:50');
INSERT INTO `product` VALUES (13, 1, '1', '1', 0.00, 0.00, NULL, '/upload_img/84bd59c1-d514-45c7-9816-20a9fca962c8.jpg', 0, 1, 4, '2025-11-22 20:19:56');
INSERT INTO `product` VALUES (14, 1, '1', '1', 0.00, 0.00, NULL, '/upload_img/d16b8cd6-781d-4a73-b6ae-d4b4cdcb2f19.jpg', 0, 1, 5, '2025-11-22 20:20:01');

-- ----------------------------
-- Table structure for sku
-- ----------------------------
DROP TABLE IF EXISTS `sku`;
CREATE TABLE `sku`  (
  `sku_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `product_id` bigint UNSIGNED NOT NULL COMMENT '商品ID',
  `spec_json` json NOT NULL COMMENT '规格JSON，例如[{ \"杯型\":\"大杯\" },{}]',
  `price` decimal(10, 2) NOT NULL COMMENT 'SKU价格',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`sku_id`) USING BTREE,
  INDEX `idx_sku_tenant_product`(`tenant_id` ASC, `product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品SKU表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sku
-- ----------------------------
INSERT INTO `sku` VALUES (1, 1, 1, '[{\"杯型\": \"大杯\"}, {\"冰度\": \"正常冰\"}]', 16.90, 498, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (2, 1, 1, '[{\"杯型\": \"大杯\"}, {\"冰度\": \"少冰\"}]', 16.90, 499, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (3, 1, 2, '[{\"杯型\": \"大杯\"}, {\"冰度\": \"正常冰\"}]', 18.90, 497, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (4, 1, 2, '[{\"杯型\": \"大杯\"}, {\"冰度\": \"少冰\"}]', 18.90, 499, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (5, 1, 3, '[{\"杯型\": \"大杯\"}, {\"冰度\": \"正常冰\"}]', 18.90, 500, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (6, 1, 3, '[{\"杯型\": \"大杯\"}, {\"冰度\": \"少冰\"}]', 18.90, 499, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (7, 1, 4, '[{\"套餐固定\": \"默认\"}]', 28.90, 300, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (8, 1, 5, '[{\"杯型\": \"霸王杯 1000ml\"}, {\"冰度\": \"正常冰\"}]', 19.00, 400, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (9, 1, 6, '[{\"杯型\": \"大杯\"}, {\"冰度\": \"正常冰\"}]', 19.00, 400, '2025-11-22 17:34:06');
INSERT INTO `sku` VALUES (10, 1, 7, '[{\"杯型\": \"大杯\"}, {\"冰度\": \"正常冰\"}]', 17.90, 400, '2025-11-22 17:34:06');

-- ----------------------------
-- Table structure for staff
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff`  (
  `staff_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID（老板所属的租户）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号（后台管理系统登录用）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '老板姓名',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职位（如：创始人、老板）',
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '状态：1正常 0停用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`staff_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE COMMENT '登录账号唯一',
  INDEX `idx_staff_tenant`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '租户管理员表（店铺老板账号）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of staff
-- ----------------------------
INSERT INTO `staff` VALUES (1, 1, 'admin', '$2a$10$K.0d/w.VtxQ1/UPjBQg1weB6rdC3PQL4LBXP/pVWDmaGUwVo0EIS2', '封灵天', '创始人', '13272796154', 1, '2025-11-22 17:34:06');
INSERT INTO `staff` VALUES (2, 1, 'manager', '$2a$10$K.0d/w.VtxQ1/UPjBQg1weB6rdC3PQL4LBXP/pVWDmaGUwVo0EIS2', '王店长', '联合创始人', '13800138001', 1, '2025-11-22 17:34:06');
INSERT INTO `staff` VALUES (3, 2, 'staff2', '$2a$10$K.0d/w.VtxQ1/UPjBQg1weB6rdC3PQL4LBXP/pVWDmaGUwVo0EIS2', '葫芦', '老板', '13800138002', 1, '2025-11-22 17:34:06');
INSERT INTO `staff` VALUES (4, 2, 'test001', '$2a$10$K.0d/w.VtxQ1/UPjBQg1weB6rdC3PQL4LBXP/pVWDmaGUwVo0EIS2', '张三', '创始人', '13800138888', 1, '2025-11-22 21:45:23');
INSERT INTO `staff` VALUES (5, 3, 'Eighteen', '$2a$10$K.0d/w.VtxQ1/UPjBQg1weB6rdC3PQL4LBXP/pVWDmaGUwVo0EIS2', '封灵天', '创始人', '13272796155', 1, '2025-11-22 21:48:25');

-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '门店ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '门店名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地址',
  `lat` decimal(10, 7) NULL DEFAULT NULL COMMENT '纬度',
  `lng` decimal(10, 7) NULL DEFAULT NULL COMMENT '经度',
  `business_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业时间',
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `status` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_store_tenant`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '门店表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store
-- ----------------------------
INSERT INTO `store` VALUES (1, 1, 'LINLEE·洪山街道口店', '武汉市洪山区街道口珞喻路 33 号', 30.5234000, 114.3600000, '10:00-22:00', '027-87871234', 1);
INSERT INTO `store` VALUES (2, 1, 'LINLEE·光谷世界城店', '武汉市东湖高新区珞瑜路 766 号', 30.5087000, 114.4200000, '10:00-22:30', '027-87451234', 1);
INSERT INTO `store` VALUES (3, 1, '测试1', '测试1', 0.0000000, 0.0000000, '8:00-9：00', '111', 1);

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant`  (
  `tenant_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '租户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户名称',
  `admin_user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '管理员用户ID',
  `status` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`tenant_id`) USING BTREE,
  INDEX `idx_tenant_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '租户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant
-- ----------------------------
INSERT INTO `tenant` VALUES (1, '林里手打柠檬茶总部', 1, 1, '2025-11-22 17:34:06', '2025-11-22 17:34:06');
INSERT INTO `tenant` VALUES (2, '测试奶茶店', NULL, 1, '2025-11-22 21:45:23', '2025-11-22 21:45:23');
INSERT INTO `tenant` VALUES (3, '测试地址1', NULL, 1, '2025-11-22 21:48:25', '2025-11-22 21:48:25');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `tenant_id` bigint UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID',
  `openid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信openid',
  `unionid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信unionid',
  `nick` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `vip_level` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '会员等级',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `status` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_mobile`(`tenant_id` ASC, `mobile` ASC) USING BTREE COMMENT '同一租户下手机号唯一',
  INDEX `idx_user_tenant`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 0, NULL, NULL, '雪阁成员CCuZwBCH5WDo', NULL, '13272796154', 0, 0, 0.00, 1, '2025-11-22 16:58:43');
INSERT INTO `user` VALUES (2, 1, 'oABC2', 'uABC2', '桃桃男孩', 'https://img.linlee.com/avatar/boy2.jpg', '13800000002', 0, 88, 0.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (3, 1, 'oABC3', 'uABC3', '鸭屎香控', 'https://img.linlee.com/avatar/3.jpg', '13800000003', 2, 520, 100.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (4, 1, 'oABC4', 'uABC4', '西瓜星人', 'https://img.linlee.com/avatar/4.jpg', '13800000004', 0, 66, 0.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (5, 1, 'oABC5', 'uABC5', '椰椰椰', 'https://img.linlee.com/avatar/5.jpg', '13800000005', 0, 99, 0.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (6, 2, 'oABC6', 'uABC6', '葡萄甜甜', 'https://img.linlee.com/avatar/6.jpg', '13800000006', 0, 77, 0.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (7, 1, 'oABC7', 'uABC7', '甄奶爱好者', 'https://img.linlee.com/avatar/7.jpg', '13800000007', 0, 55, 0.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (8, 1, 'oABC8', 'uABC8', '霸王杯杀手', 'https://img.linlee.com/avatar/8.jpg', '13800000008', 1, 188, 50.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (9, 1, 'oABC9', 'uABC9', '积分小王子', 'https://img.linlee.com/avatar/9.jpg', '13800000009', 2, 999, 200.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (10, 1, 'oABC10', 'uABC10', '邀请大王', 'https://img.linlee.com/avatar/10.jpg', '13800000010', 3, 2025, 999.00, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (11, 1, 'oABC1', 'uABC1', '柠檬女孩', 'https://img.linlee.com/avatar/girl1.jpg', '13800000001', 1, 128, 68.80, 1, '2025-11-22 17:34:06');
INSERT INTO `user` VALUES (12, 1, NULL, NULL, '测试', '/upload_img/dd4d966c-321d-4de6-bbc7-f09002b51ff7.jpg', '17723976535', 0, 0, 0.00, 1, '2025-12-17 08:38:13');

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区县',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认：1是 0否',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (1, 1, 1, '张三', '13800138001', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座10层1001室', 1, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (2, 1, 1, '张三', '13800138001', '北京市', '北京市', '海淀区', '中关村大街1号中关村创业大厦2208室', 0, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (3, 1, 1, '李四', '13900139002', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号恒生银行大厦28楼', 0, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (4, 1, 2, '王五', '13700137003', '广东省', '深圳市', '南山区', '科技园南区深圳软件园1栋5楼501室', 1, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (5, 1, 2, '王五', '13700137003', '广东省', '广州市', '天河区', '天河路228号广晟大厦2005室', 0, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (6, 1, 3, '赵六', '13600136004', '浙江省', '杭州市', '西湖区', '文三路90号东部软件园科技大厦B座16层', 1, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (7, 1, 3, '赵六', '13600136004', '浙江省', '杭州市', '滨江区', '江南大道3588号恒生大厦北楼14层', 0, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (8, 1, 4, '孙七', '13500135005', '江苏省', '南京市', '鼓楼区', '中山北路200号南京国际广场18楼1805室', 1, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (9, 1, 5, '周八', '13400134006', '四川省', '成都市', '武侯区', '天府大道北段1700号环球中心E1座20层', 1, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (10, 1, 5, '周八', '13400134006', '四川省', '成都市', '高新区', '天府三街88号高新国际广场C座12楼', 0, '2025-11-22 18:39:29', '2025-11-22 18:39:29');
INSERT INTO `user_address` VALUES (11, NULL, 12, 'Eighteen', '13272736154', '广东省', '广州市', '天河区', '广东省广州市天河区测试', 1, '2025-12-17 12:54:29', '2025-12-17 12:54:29');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `user_coupon_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户券ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `coupon_id` bigint UNSIGNED NOT NULL COMMENT '优惠券ID',
  `status` tinyint UNSIGNED NOT NULL COMMENT '状态 1未使用 2已使用 3已过期',
  `used_time` timestamp NULL DEFAULT NULL COMMENT '使用时间',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`user_coupon_id`) USING BTREE,
  INDEX `idx_user_coupon_tenant_user`(`tenant_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------
INSERT INTO `user_coupon` VALUES (1, 1, 1, 1, 1, NULL, '2025-12-22 17:34:06');
INSERT INTO `user_coupon` VALUES (2, 1, 1, 2, 1, NULL, '2025-12-22 17:34:06');
INSERT INTO `user_coupon` VALUES (3, 1, 2, 1, 1, NULL, '2025-12-22 17:34:06');
INSERT INTO `user_coupon` VALUES (4, 1, 3, 4, 1, NULL, '2025-11-29 17:34:06');

SET FOREIGN_KEY_CHECKS = 1;
