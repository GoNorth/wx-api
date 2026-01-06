-- ----------------------------
-- Alter table biz_image_template - 添加 status 字段
-- ----------------------------
ALTER TABLE `biz_image_template` 
ADD COLUMN `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态：INIT-新建阶段，RECOG-识别阶段，TEST-产品图片测试，PUBLISH-发布使用阶段，INVALID-作废阶段' AFTER `poster_type`;

-- 添加索引
ALTER TABLE `biz_image_template` 
ADD INDEX `idx_status`(`status`) USING BTREE COMMENT '状态索引';

