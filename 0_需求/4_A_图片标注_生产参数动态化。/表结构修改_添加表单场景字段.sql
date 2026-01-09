-- ----------------------------
-- Alter table biz_image_template - 添加 form_scenario_id 字段
-- ----------------------------
ALTER TABLE `biz_image_template` 
ADD COLUMN `form_scenario_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单场景ID，关联biz_form_scenario表，用于记录选择了哪个场景控件form' AFTER `recognition_complete_time`;

-- 添加索引
ALTER TABLE `biz_image_template` 
ADD INDEX `idx_form_scenario_id`(`form_scenario_id`) USING BTREE COMMENT '表单场景ID索引';

-- 添加外键约束（可选，如果需要强制关联）
-- ALTER TABLE `biz_image_template` 
-- ADD CONSTRAINT `fk_image_template_scenario` FOREIGN KEY (`form_scenario_id`) REFERENCES `biz_form_scenario` (`scenario_id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- ----------------------------
-- Alter table biz_image_product - 添加 form_scenario_id 和 form_scenario_json 字段
-- ----------------------------
ALTER TABLE `biz_image_product` 
ADD COLUMN `form_scenario_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单场景ID，关联biz_form_scenario表，用于记录选择了哪个场景控件form' AFTER `generate_complete_time`;

-- 添加索引
ALTER TABLE `biz_image_product` 
ADD INDEX `idx_form_scenario_id`(`form_scenario_id`) USING BTREE COMMENT '表单场景ID索引';

-- 添加 form_scenario_json 字段
ALTER TABLE `biz_image_product` 
ADD COLUMN `form_scenario_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单场景JSON，保存控件列表的具体值，JSON格式存储' AFTER `form_scenario_id`;

-- 添加外键约束（可选，如果需要强制关联）
-- ALTER TABLE `biz_image_product` 
-- ADD CONSTRAINT `fk_image_product_scenario` FOREIGN KEY (`form_scenario_id`) REFERENCES `biz_form_scenario` (`scenario_id`) ON DELETE SET NULL ON UPDATE CASCADE;

