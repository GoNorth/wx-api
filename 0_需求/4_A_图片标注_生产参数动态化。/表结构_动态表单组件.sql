# 表单组件表（主表）
CREATE TABLE `biz_form_component` (
  # 表主键
  `component_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  # 业务字段-开始
  # 基础信息
  `component_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件类型字典CODE：INPUT-单行输入框，TEXTAREA-多行输入框，SELECT-下拉框，DATE-日期选择器，DATETIME-日期时间选择器，NUMBER-数字输入框，SWITCH-开关，RADIO-单选框，CHECKBOX-多选框，UPLOAD-文件上传，RICH_TEXT-富文本编辑器',
  `component_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件编码（唯一标识，用于前端识别）',
  `component_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件名称',
  `component_label` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '显示标签',
  `component_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件描述',
  
  # 组件配置（JSON格式存储）
  `component_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '组件配置JSON，包含：placeholder占位符、required是否必填、defaultValue默认值、rules验证规则、props扩展属性等',
  
  # 选项配置（下拉框、单选框、多选框等需要）
  `options_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '选项配置JSON，格式：[{"label":"选项1","value":"value1"},{"label":"选项2","value":"value2"}]',
  
  # 排序
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序，数字越小越靠前',
  
  # 状态
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态字典CODE：ACTIVE-启用，INACTIVE-禁用',
  # 业务字段-结束

  # 表控制字段
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`component_id`) USING BTREE,
  UNIQUE KEY `uk_component_code_deleted` (`component_code`, `deleted`) USING BTREE COMMENT '组件编码唯一索引（考虑逻辑删除）',
  KEY `idx_component_type` (`component_type`) USING BTREE,
  KEY `idx_component_code` (`component_code`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='表单组件表';

# 表单场景表（主表）
CREATE TABLE `biz_form_scenario` (
  # 表主键
  `scenario_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  # 业务字段-开始
  # 基础信息
  `scenario_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景编码（唯一标识）',
  `scenario_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景名称',
  `scenario_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '场景描述',
  
  # 排序
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序，数字越小越靠前',
  
  # 状态
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态字典CODE：ACTIVE-启用，INACTIVE-禁用',
  # 业务字段-结束

  # 表控制字段
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`scenario_id`) USING BTREE,
  UNIQUE KEY `uk_scenario_code_deleted` (`scenario_code`, `deleted`) USING BTREE COMMENT '场景编码唯一索引（考虑逻辑删除）',
  KEY `idx_scenario_code` (`scenario_code`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='表单场景表';

# 场景组件关联表（关联表）
CREATE TABLE `biz_form_scenario_component` (
  # 表主键
  `relation_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  # 业务字段-开始
  # 关联字段
  `scenario_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景ID，关联biz_form_scenario表',
  `component_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件ID，关联biz_form_component表',
  
  # 场景内组件配置（可覆盖组件默认配置）
  `component_config_override` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '场景内组件配置覆盖JSON，可覆盖组件默认配置',
  
  # 排序（场景内组件的排序）
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '场景内组件排序顺序，数字越小越靠前',
  
  # 状态
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态字典CODE：ACTIVE-启用，INACTIVE-禁用',
  # 业务字段-结束

  # 表控制字段
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`relation_id`) USING BTREE,
  UNIQUE KEY `uk_scenario_component_deleted` (`scenario_id`, `component_id`, `deleted`) USING BTREE COMMENT '场景与组件关联唯一索引（考虑逻辑删除）',
  KEY `idx_scenario_id` (`scenario_id`) USING BTREE,
  KEY `idx_component_id` (`component_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_scenario_component_scenario` FOREIGN KEY (`scenario_id`) REFERENCES `biz_form_scenario` (`scenario_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_scenario_component_component` FOREIGN KEY (`component_id`) REFERENCES `biz_form_component` (`component_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='场景组件关联表';

