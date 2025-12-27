-- ============================================
-- 微信公众号餐饮营销系统数据库建表脚本
-- 参考 ext_resource 表的系统控制字段规范
-- ============================================

-- 1. 门店表
CREATE TABLE `biz_store` (
  `store_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  -- 业务字段开始
  `owner_openid` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店主微信OPENID，关联WX_USER表的OPENID',
  `owner_name` VARCHAR(50) NOT NULL COMMENT '店主姓名',
  `owner_phone` VARCHAR(11) NOT NULL COMMENT '店主手机号',
  `store_name` VARCHAR(100) NOT NULL COMMENT '门店名称',
  `catering_type` VARCHAR(20) NOT NULL COMMENT '餐饮种类字典CODE：CHINESE-中餐，WESTERN-西餐，JAPANESE-日料，HOTPOT-火锅，BARBECUE-烧烤，FASTFOOD-快餐，SNACK-小吃，OTHER-其他',
  `store_address` VARCHAR(255) NOT NULL COMMENT '门店详细地址',
  `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
  `customer_group` VARCHAR(20) NOT NULL COMMENT '客户人群字典CODE：STUDENT-学生，OFFICE_WORKER-上班族，FAMILY-家庭，BUSINESS-商务人士，ELDERLY-老年人，OTHER-其他',
  `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态字典CODE：0-PENDING待审核，1-APPROVED已通过，2-REJECTED已拒绝',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  -- 业务字段结束

  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`store_id`) USING BTREE,
  UNIQUE KEY `uk_owner_phone` (`owner_phone`) USING BTREE,
  UNIQUE KEY `uk_owner_openid` (`owner_openid`) USING BTREE,
  KEY `idx_audit_status` (`audit_status`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_store_wx_user` FOREIGN KEY (`owner_openid`) REFERENCES `wx_user` (`openid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='门店表';

-- 2. 门店人物形象表
CREATE TABLE `biz_store_character` (
  `character_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  -- 业务字段开始
  `store_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '门店ID',
  `character_role` VARCHAR(20) NOT NULL COMMENT '角色字典CODE：OWNER-店主，CHEF-厨师，WAITER-服务员，MANAGER-店长，OTHER-其他',
  `character_photo_url` VARCHAR(500) DEFAULT NULL COMMENT '人物照片URL',
  `character_voice_url` VARCHAR(500) DEFAULT NULL COMMENT '人物声音URL',
  -- 业务字段结束

  `from_type` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UPLOAD' COMMENT '资源来源字典CODE：UPLOAD-用户上传，GENERATE-程序生成',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`character_id`) USING BTREE,
  KEY `idx_store_id` (`store_id`) USING BTREE,
  KEY `idx_character_role` (`character_role`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  CONSTRAINT `fk_store_character_store` FOREIGN KEY (`store_id`) REFERENCES `biz_store` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='门店人物形象表';

-- 3. 门店VI表
CREATE TABLE `biz_store_vi` (
  `vi_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  -- 业务字段开始
  `store_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '门店ID',
  `logo_url` VARCHAR(500) DEFAULT NULL COMMENT '门店LOGO URL',
  `work_uniform_url` VARCHAR(500) DEFAULT NULL COMMENT '工作服照片URL',
  `ip_image_url` VARCHAR(500) DEFAULT NULL COMMENT 'IP形象设计图URL',
  -- 业务字段结束

  `from_type` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UPLOAD' COMMENT '资源来源字典CODE：UPLOAD-用户上传，GENERATE-程序生成',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`vi_id`) USING BTREE,
  UNIQUE KEY `uk_store_id` (`store_id`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  CONSTRAINT `fk_store_vi_store` FOREIGN KEY (`store_id`) REFERENCES `biz_store` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='门店VI表';

-- 4. 计划头部表（计划属于某个门店，通过store_id建立所属关系）
CREATE TABLE `biz_plan_header` (
  `plan_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  -- 业务字段开始
  `store_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '门店ID，所属门店，建立所属关系',
  `plan_type` TINYINT NOT NULL COMMENT '计划类型字典CODE：1-IMAGE图片计划，2-VIDEO视频计划',
  `strategy_type` TINYINT NOT NULL COMMENT '策略类型字典CODE：1-PRIVATE_DOMAIN私域复购，2-PUBLIC_DOMAIN公域获客',
  `plan_name` VARCHAR(100) DEFAULT NULL COMMENT '计划名称，可选',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态字典CODE：0-INIT初始待生成，1-CONFIRMING修改确认中，2-SUBMITTED已提交，3-EXECUTED已执行，4-DELIVERED已下发，5-CANCELLED已取消',
  `submitted_at` DATETIME DEFAULT NULL COMMENT '提交时间',
  `executed_at` DATETIME DEFAULT NULL COMMENT '执行时间',
  `delivered_at` DATETIME DEFAULT NULL COMMENT '下发用户时间',
  -- 业务字段结束

  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`plan_id`) USING BTREE,
  KEY `idx_store_id` (`store_id`) USING BTREE,
  KEY `idx_plan_type` (`plan_type`) USING BTREE,
  KEY `idx_strategy_type` (`strategy_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_plan_header_store` FOREIGN KEY (`store_id`) REFERENCES `biz_store` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='计划头部表';

-- 5. 计划项目表
CREATE TABLE `biz_plan_item` (
  `item_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  -- 业务字段开始
  `plan_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划头部ID',
  `date_key` VARCHAR(20) NOT NULL COMMENT '日期键：图片计划为YYYY-MM-DD，视频计划为WEEK1-WEEK4',
  `time_slot` VARCHAR(20) DEFAULT NULL COMMENT '时段字典CODE：ALL-全天，BREAKFAST-早餐，LUNCH-午餐，AFTERNOON-下午，DINNER-晚餐',
  `marketing_theme` VARCHAR(50) DEFAULT NULL COMMENT '营销主题字典CODE：MEMBER-会员，DISCOUNT-折扣，NEWPRODUCT-新品，GROUPBUY-团购，HOLIDAY-节日，REFERRAL-推荐等',
  `platform` VARCHAR(50) DEFAULT NULL COMMENT '发布平台字典CODE：DOUYIN-抖音，MEITUAN-美团，XIAOHONGSHU-小红书，WECHAT-微信等，公域获客时使用',
  `content_tag` VARCHAR(50) DEFAULT NULL COMMENT '内容类型标签字典CODE：SHORTVIDEO-短视频，GROUPBUY-团购，STOREVISIT-到店，NEWPRODUCT-新品等，公域获客时使用',
  `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `discount_price` DECIMAL(10,2) DEFAULT NULL COMMENT '优惠价',
  `activity_details` TEXT COMMENT '活动详情与规则',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态字典CODE：0-INIT初始待生成，1-CONFIRMING修改确认中，2-SUBMITTED已提交，3-EXECUTED已执行，4-DELIVERED已下发，5-CANCELLED已取消',
  `submitted_at` DATETIME DEFAULT NULL COMMENT '提交时间',
  `executed_at` DATETIME DEFAULT NULL COMMENT '执行时间',
  `delivered_at` DATETIME DEFAULT NULL COMMENT '下发用户时间',
  -- 业务字段结束

  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`item_id`) USING BTREE,
  KEY `idx_plan_id` (`plan_id`) USING BTREE,
  KEY `idx_date_key` (`date_key`) USING BTREE,
  KEY `idx_platform` (`platform`) USING BTREE,
  KEY `idx_content_tag` (`content_tag`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_plan_item_plan` FOREIGN KEY (`plan_id`) REFERENCES `biz_plan_header` (`plan_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='计划项目表';

-- 6. 资源(图片视频)内容表（通过计划项目表间接关联门店，不是直接关联。每个计划项目可以生成多次内容）
CREATE TABLE `biz_resources_content` (
  `content_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  -- 业务字段开始
  `store_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '门店ID，冗余字段用于查询便利，逻辑上通过PLAN_ITEM_ID间接关联',
  `content_type` TINYINT NOT NULL COMMENT '内容类型字典CODE：1-IMAGE图片，2-VIDEO视频',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `thumbnail_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图URL',
  `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL，图片或视频',
  `duration` INT DEFAULT NULL COMMENT '视频时长，单位秒，仅视频类型使用',
  `applicable_scenario` VARCHAR(200) DEFAULT NULL COMMENT '适用场景或用途说明，如：适用于会员充值推广、适用于会员日活动推广等',
  `publish_date` DATE NOT NULL COMMENT '发布日期',
  `plan_item_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联的计划项目ID，主要关联，通过此字段间接关联到门店，每个计划项目可以生成多次内容',
  -- 业务字段结束

  `from_type` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'GENERATE' COMMENT '资源来源：UPLOAD-用户上传，GENERATE-程序生成',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`content_id`) USING BTREE,
  KEY `idx_store_id` (`store_id`) USING BTREE,
  KEY `idx_content_type` (`content_type`) USING BTREE,
  KEY `idx_publish_date` (`publish_date`) USING BTREE,
  KEY `idx_plan_item_id` (`plan_item_id`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_resources_content_store` FOREIGN KEY (`store_id`) REFERENCES `biz_store` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_resources_content_plan_item` FOREIGN KEY (`plan_item_id`) REFERENCES `biz_plan_item` (`item_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='资源(图片视频)内容表';

-- 7. 内容反馈表
CREATE TABLE `biz_content_feedback` (
  `feedback_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  -- 业务字段开始
  `content_id` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容ID',
  `feedback_type` VARCHAR(20) NOT NULL COMMENT '反馈类型字典CODE：CHARACTER-人物错误，PRODUCT-产品错误，TEXT-文字错误',
  `feedback_desc` VARCHAR(500) DEFAULT NULL COMMENT '反馈描述，可选',
  `user_openid` VARCHAR(100) DEFAULT NULL COMMENT '用户微信OPENID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态字典CODE：0-PENDING待处理，1-PROCESSED已处理，2-IGNORED已忽略',
  `handle_remark` VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
  -- 业务字段结束

  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`feedback_id`) USING BTREE,
  KEY `idx_content_id` (`content_id`) USING BTREE,
  KEY `idx_feedback_type` (`feedback_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_content_feedback_content` FOREIGN KEY (`content_id`) REFERENCES `biz_resources_content` (`content_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='内容反馈表';

-- ============================================
-- 初始化数据示例（可选）
-- ============================================

-- 示例：插入一个测试门店（需要先确保wx_user表中存在对应的openid）
-- INSERT INTO `biz_store` (`store_id`, `owner_openid`, `owner_name`, `owner_phone`, `store_name`, `catering_type`, `store_address`, `customer_group`) 
-- VALUES ('STORE_001', 'wx_openid_001', '张三', '13800138000', '测试餐厅', '中餐', '北京市朝阳区测试街道123号', '上班族');
