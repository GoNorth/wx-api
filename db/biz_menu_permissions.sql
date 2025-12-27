-- ============================================
-- 微信公众号餐饮营销系统 - 菜单权限配置SQL
-- ============================================
-- 执行前请先查询当前最大 menu_id：SELECT MAX(menu_id) FROM sys_menu;
-- 然后根据实际情况调整下面的 menu_id 起始值（这里假设从 200 开始）

-- 1. 创建 biz 模块的父菜单（目录）
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (200, 0, '餐饮营销管理', NULL, NULL, 0, 'el-icon-s-shop', 100);

-- 2. 门店管理菜单
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (201, 200, '门店管理', 'biz/store', NULL, 1, 'el-icon-office-building', 1);

-- 门店管理权限按钮
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (202, 201, '查看', NULL, 'biz:bizstore:list', 2, NULL, 1),
       (203, 201, '详情', NULL, 'biz:bizstore:info', 2, NULL, 2),
       (204, 201, '新增', NULL, 'biz:bizstore:save', 2, NULL, 3),
       (205, 201, '修改', NULL, 'biz:bizstore:update', 2, NULL, 4),
       (206, 201, '删除', NULL, 'biz:bizstore:delete', 2, NULL, 5);

-- 3. 门店人物形象管理菜单
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (210, 200, '门店人物形象', 'biz/storeCharacter', NULL, 1, 'el-icon-user', 2);

INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (211, 210, '查看', NULL, 'biz:bizstorecharacter:list', 2, NULL, 1),
       (212, 210, '详情', NULL, 'biz:bizstorecharacter:info', 2, NULL, 2),
       (213, 210, '新增', NULL, 'biz:bizstorecharacter:save', 2, NULL, 3),
       (214, 210, '修改', NULL, 'biz:bizstorecharacter:update', 2, NULL, 4),
       (215, 210, '删除', NULL, 'biz:bizstorecharacter:delete', 2, NULL, 5);

-- 4. 门店VI管理菜单
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (220, 200, '门店VI', 'biz/storeVi', NULL, 1, 'el-icon-picture', 3);

INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (221, 220, '查看', NULL, 'biz:bizstorevi:list', 2, NULL, 1),
       (222, 220, '详情', NULL, 'biz:bizstorevi:info', 2, NULL, 2),
       (223, 220, '新增', NULL, 'biz:bizstorevi:save', 2, NULL, 3),
       (224, 220, '修改', NULL, 'biz:bizstorevi:update', 2, NULL, 4),
       (225, 220, '删除', NULL, 'biz:bizstorevi:delete', 2, NULL, 5);

-- 5. 计划头部管理菜单
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (230, 200, '计划管理', 'biz/planHeader', NULL, 1, 'el-icon-document', 4);

INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (231, 230, '查看', NULL, 'biz:bizplanheader:list', 2, NULL, 1),
       (232, 230, '详情', NULL, 'biz:bizplanheader:info', 2, NULL, 2),
       (233, 230, '新增', NULL, 'biz:bizplanheader:save', 2, NULL, 3),
       (234, 230, '修改', NULL, 'biz:bizplanheader:update', 2, NULL, 4),
       (235, 230, '删除', NULL, 'biz:bizplanheader:delete', 2, NULL, 5);

-- 6. 计划项目管理菜单
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (240, 200, '计划项目', 'biz/planItem', NULL, 1, 'el-icon-tickets', 5);

INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (241, 240, '查看', NULL, 'biz:bizplanitem:list', 2, NULL, 1),
       (242, 240, '详情', NULL, 'biz:bizplanitem:info', 2, NULL, 2),
       (243, 240, '新增', NULL, 'biz:bizplanitem:save', 2, NULL, 3),
       (244, 240, '修改', NULL, 'biz:bizplanitem:update', 2, NULL, 4),
       (245, 240, '删除', NULL, 'biz:bizplanitem:delete', 2, NULL, 5);

-- 7. 资源内容管理菜单
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (250, 200, '资源内容', 'biz/resourcesContent', NULL, 1, 'el-icon-video-camera', 6);

INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (251, 250, '查看', NULL, 'biz:bizresourcescontent:list', 2, NULL, 1),
       (252, 250, '详情', NULL, 'biz:bizresourcescontent:info', 2, NULL, 2),
       (253, 250, '新增', NULL, 'biz:bizresourcescontent:save', 2, NULL, 3),
       (254, 250, '修改', NULL, 'biz:bizresourcescontent:update', 2, NULL, 4),
       (255, 250, '删除', NULL, 'biz:bizresourcescontent:delete', 2, NULL, 5);

-- 8. 内容反馈管理菜单
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (260, 200, '内容反馈', 'biz/contentFeedback', NULL, 1, 'el-icon-chat-line-round', 7);

INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) 
VALUES (261, 260, '查看', NULL, 'biz:bizcontentfeedback:list', 2, NULL, 1),
       (262, 260, '详情', NULL, 'biz:bizcontentfeedback:info', 2, NULL, 2),
       (263, 260, '新增', NULL, 'biz:bizcontentfeedback:save', 2, NULL, 3),
       (264, 260, '修改', NULL, 'biz:bizcontentfeedback:update', 2, NULL, 4),
       (265, 260, '删除', NULL, 'biz:bizcontentfeedback:delete', 2, NULL, 5);

-- ============================================
-- 将 biz 模块的所有菜单权限分配给超级管理员角色（role_id=1）
-- ============================================
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `menu_id` FROM `sys_menu` WHERE `menu_id` >= 200 AND `menu_id` <= 265;

-- ============================================
-- 执行说明：
-- 1. 执行上述SQL后，超级管理员（role_id=1）将拥有所有 biz 模块的权限
-- 2. 如果要将权限分配给其他角色，请修改最后一行的 role_id
-- 3. 也可以在管理后台的"角色管理"页面中手动分配菜单权限
-- ============================================
