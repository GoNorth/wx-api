-- ----------------------------
-- Alter table wx_material - 添加新字段
-- ----------------------------
ALTER TABLE `wx_material` 
ADD COLUMN `enterprise_id` varchar(50) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '企业ID' AFTER `appid`,
ADD COLUMN `for_date` date NULL DEFAULT NULL COMMENT '图片视频的需求日期' AFTER `enterprise_id`,
ADD COLUMN `media_store` varchar(10) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '素材存储类型：TEMP-临时的；Perm-永久的' AFTER `for_date`;

-- 添加索引
ALTER TABLE `wx_material` 
ADD INDEX `idx_enterprise_id`(`enterprise_id`) USING BTREE COMMENT '企业ID索引';

