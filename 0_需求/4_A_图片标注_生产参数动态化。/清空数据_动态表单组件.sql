-- ----------------------------
-- 清空动态表单组件相关表的数据
-- 注意：需要按照外键依赖关系的逆序删除
-- ----------------------------

-- 方案1：使用主键条件删除（推荐，符合安全更新模式）
-- 1. 先删除关联表的数据（最底层）
DELETE FROM `biz_form_scenario_component` WHERE `relation_id` IS NOT NULL;

-- 2. 删除场景表的数据
DELETE FROM `biz_form_scenario` WHERE `scenario_id` IS NOT NULL;

-- 3. 删除组件表的数据（组件表没有被其他表引用，可以随时删除）
DELETE FROM `biz_form_component` WHERE `component_id` IS NOT NULL;

-- ----------------------------
-- 方案2：禁用外键检查后使用 TRUNCATE（如果方案1不工作，可以使用此方案）
-- ----------------------------
-- 禁用外键检查
-- SET FOREIGN_KEY_CHECKS = 0;

-- 清空表数据
-- TRUNCATE TABLE `biz_form_scenario_component`;
-- TRUNCATE TABLE `biz_form_scenario`;
-- TRUNCATE TABLE `biz_form_component`;

-- 启用外键检查
-- SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 方案3：如果需要在 MySQL Workbench 中执行，可以临时禁用安全更新模式
-- 菜单：Edit -> Preferences -> SQL Editor -> 取消勾选 "Safe Updates"
-- 然后重新连接数据库
-- ----------------------------

-- 注意：由于这些表使用的是 varchar(128) 作为主键，不是自增ID，所以不需要重置AUTO_INCREMENT

