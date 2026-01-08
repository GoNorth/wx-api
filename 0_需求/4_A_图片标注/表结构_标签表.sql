# 标签表（主表）
CREATE TABLE `biz_tag` (
  # 表主键
  `tag_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  # 业务字段-开始
  `tag_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `tag_count` int NOT NULL DEFAULT '0' COMMENT '当前标签的引用个数（关联的模板数量）',
  `tag_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签描述',
  # 业务字段-结束

  # 表控制字段
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE KEY `uk_tag_name_deleted` (`tag_name`, `deleted`) USING BTREE COMMENT '标签名称唯一索引（考虑逻辑删除）',
  KEY `idx_tag_name` (`tag_name`) USING BTREE,
  KEY `idx_tag_count` (`tag_count`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='标签表';

# 标签映射表（关联表）
CREATE TABLE `biz_tag_map` (
  # 表主键
  `map_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  
  # 业务字段-开始
  # 关联字段
  `tag_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签ID，关联biz_tag表',
  `template_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板ID，关联biz_image_template表',
  # 业务字段-结束

  # 表控制字段
  `deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`map_id`) USING BTREE,
  UNIQUE KEY `uk_tag_template_deleted` (`tag_id`, `template_id`, `deleted`) USING BTREE COMMENT '标签与模板关联唯一索引（考虑逻辑删除）',
  KEY `idx_tag_id` (`tag_id`) USING BTREE,
  KEY `idx_template_id` (`template_id`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_tag_map_tag` FOREIGN KEY (`tag_id`) REFERENCES `biz_tag` (`tag_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_tag_map_template` FOREIGN KEY (`template_id`) REFERENCES `biz_image_template` (`template_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='标签映射表（标签与模板的关联表）';

