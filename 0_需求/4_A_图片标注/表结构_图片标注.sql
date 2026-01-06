# 模板表（主表）
CREATE TABLE `biz_image_template` (
  # 表主键
  `template_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  # 业务字段-开始
  # 基础信息
  `poster_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '海报类型，如：爆款招牌',
  
  # 模板图片相关
  `template_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板图片URL',
  `template_image_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板图片文件名',
  `template_image_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '模板图片描述',
  
  # 图片识别相关（多模态图片识别）
  `task_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '识别任务ID',
  `recognition_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '识别状态：pending-待处理，processing-处理中，completed-已完成，failed-失败',
  `recognition_model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '识别模型名称，如：Qwen-VL',
  `recognition_model_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '识别模型版本',
  `recognition_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '多模态识别提示词/文本输入',
  `recognition_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '识别描述/识别结果文本（用于图生图的输入）',
  `recognition_error_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '识别错误信息',
  `recognition_complete_time` datetime DEFAULT NULL COMMENT '识别完成时间',
  # 业务字段-结束

  # 表控制字段
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`template_id`) USING BTREE,
  KEY `idx_poster_type` (`poster_type`) USING BTREE,
  KEY `idx_task_id` (`task_id`) USING BTREE,
  KEY `idx_recognition_status` (`recognition_status`) USING BTREE,
  KEY `idx_recognition_model` (`recognition_model`) USING BTREE,
  KEY `idx_recognition_complete_time` (`recognition_complete_time`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='图片模板表';

# 产品图片表（从表）
CREATE TABLE `biz_image_product` (
  # 表主键
  `product_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  # 业务字段-开始
  # 关联字段
  `template_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板ID，关联biz_image_template表',
  
  # 基础信息
  `dish_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜品名称',
  `image_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片文件名',
  `dish_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜品分类，如：炒菜',
  `price_display` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '价格显示：有价格/无价格',
  `product_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品类型，如：单产品',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格（元）',
  `marketing_theme` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '营销主题，如：新春特惠',
  
  # 产品图片相关
  `product_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品图片URL',
  `product_image_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品图片文件名',
  
  # 图生图相关（基于模板识别结果和产品图片进行图生图）
  `generate_task_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图生图任务ID',
  `generate_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图生图状态：pending-待处理，processing-处理中，completed-已完成，failed-失败',
  `generate_model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图生图模型名称',
  `generate_model_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图生图模型版本',
  `generate_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图生图提示词（基于模板识别结果文本生成）',
  `generate_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图生图参数配置，JSON格式存储',
  `generated_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '生成的图片URL列表，JSON格式存储',
  `generated_image_count` int DEFAULT '0' COMMENT '生成的图片数量',
  `generate_error_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图生图错误信息',
  `generate_complete_time` datetime DEFAULT NULL COMMENT '图生图完成时间',
  # 业务字段-结束

  # 表控制字段
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`product_id`) USING BTREE,
  KEY `idx_template_id` (`template_id`) USING BTREE,
  KEY `idx_dish_name` (`dish_name`) USING BTREE,
  KEY `idx_dish_category` (`dish_category`) USING BTREE,
  KEY `idx_product_type` (`product_type`) USING BTREE,
  KEY `idx_generate_task_id` (`generate_task_id`) USING BTREE,
  KEY `idx_generate_status` (`generate_status`) USING BTREE,
  KEY `idx_generate_complete_time` (`generate_complete_time`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_image_product_template` FOREIGN KEY (`template_id`) REFERENCES `biz_image_template` (`template_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='产品图片表';
