-- ----------------------------
-- Alter table biz_image_product - 删除字段 dish_category, price_display, product_type
-- ----------------------------

-- 先删除基于这些字段的索引
ALTER TABLE `biz_image_product` 
DROP INDEX `idx_dish_category`;

ALTER TABLE `biz_image_product` 
DROP INDEX `idx_product_type`;

-- 删除字段
ALTER TABLE `biz_image_product` 
DROP COLUMN `dish_category`;

ALTER TABLE `biz_image_product` 
DROP COLUMN `price_display`;

ALTER TABLE `biz_image_product` 
DROP COLUMN `product_type`;

