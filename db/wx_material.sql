-- ----------------------------
-- Table structure for wx_material
-- ----------------------------
DROP TABLE IF EXISTS `wx_material`;
CREATE TABLE `wx_material`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `appid` char(20) CHARACTER SET utf8 NOT NULL COMMENT 'appid',
  `enterprise_id` varchar(50) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '企业ID',
  `for_date` date NULL DEFAULT NULL COMMENT '图片视频的需求日期',
  `media_store` varchar(10) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '素材存储类型：TEMP-临时的；Perm-永久的',
  `media_id` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '微信素材ID',
  `media_type` varchar(20) CHARACTER SET utf8 NOT NULL COMMENT '素材类型(image/video/voice/thumb)',
  `file_name` varchar(255) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '文件名',
  `url` varchar(500) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '素材URL(仅图片和视频有)',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_appid_media_id`(`appid`, `media_id`) USING BTREE COMMENT 'appid和media_id唯一索引',
  INDEX `idx_appid`(`appid`) USING BTREE COMMENT 'appid索引',
  INDEX `idx_media_type`(`media_type`) USING BTREE COMMENT '素材类型索引',
  INDEX `idx_enterprise_id`(`enterprise_id`) USING BTREE COMMENT '企业ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COMMENT = '微信素材表' ROW_FORMAT = Dynamic;

