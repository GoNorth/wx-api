-- ----------------------------
-- 动态表单组件系统示例数据
-- 执行顺序：先执行组件数据，再执行场景数据，最后执行关联数据
-- ----------------------------

-- 如果表中有数据，建议先执行清空脚本：清空数据_动态表单组件.sql

# biz_form_component 表示例数据

# 1. 单行输入框
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_001',
  'INPUT',
  'dish_name',
  '菜品名称',
  '菜品名称',
  '单行文本输入框，用于输入菜品名称',
  '{"placeholder":"请输入菜品名称","required":true,"maxLength":100,"minLength":1}',
  NULL,
  1,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 2. 多行输入框
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_002',
  'TEXTAREA',
  'dish_desc',
  '菜品描述',
  '菜品描述',
  '多行文本输入框，用于输入菜品描述',
  '{"placeholder":"请输入菜品描述","required":false,"maxLength":500,"rows":4}',
  NULL,
  2,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 3. 下拉框
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_003',
  'SELECT',
  'dish_category',
  '菜品分类',
  '菜品分类',
  '下拉选择框，用于选择菜品分类',
  '{"placeholder":"请选择菜品分类","required":true}',
  '[{"label":"炒菜","value":"炒菜"},{"label":"汤类","value":"汤类"},{"label":"凉菜","value":"凉菜"},{"label":"主食","value":"主食"}]',
  3,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 4. 数字输入框
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_004',
  'NUMBER',
  'price',
  '价格',
  '价格（元）',
  '数字输入框，用于输入价格',
  '{"placeholder":"请输入价格","required":true,"min":0,"max":9999,"precision":2,"step":0.01}',
  NULL,
  4,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 5. 日期选择器
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_005',
  'DATE',
  'promotion_date',
  '促销日期',
  '促销日期',
  '日期选择器，用于选择促销日期',
  '{"placeholder":"请选择促销日期","required":false,"format":"YYYY-MM-DD"}',
  NULL,
  5,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 6. 日期时间选择器
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_006',
  'DATETIME',
  'start_time',
  '开始时间',
  '开始时间',
  '日期时间选择器，用于选择开始时间',
  '{"placeholder":"请选择开始时间","required":true,"format":"YYYY-MM-DD HH:mm:ss"}',
  NULL,
  6,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 7. 单选框
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_007',
  'RADIO',
  'price_display',
  '价格显示',
  '价格显示',
  '单选框，用于选择价格显示方式',
  '{"required":true}',
  '[{"label":"有价格","value":"有价格"},{"label":"无价格","value":"无价格"}]',
  7,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 8. 多选框
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_008',
  'CHECKBOX',
  'marketing_tags',
  '营销标签',
  '营销标签',
  '多选框，用于选择营销标签',
  '{"required":false}',
  '[{"label":"新春特惠","value":"新春特惠"},{"label":"限时优惠","value":"限时优惠"},{"label":"会员专享","value":"会员专享"},{"label":"新品上市","value":"新品上市"}]',
  8,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 9. 开关
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_009',
  'SWITCH',
  'is_hot',
  '是否热门',
  '是否热门',
  '开关组件，用于设置是否热门',
  '{"defaultValue":false}',
  NULL,
  9,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 10. 文件上传
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_010',
  'UPLOAD',
  'product_image',
  '产品图片',
  '产品图片',
  '文件上传组件，用于上传产品图片',
  '{"required":true,"accept":"image/*","maxSize":5242880,"maxCount":1,"listType":"picture-card"}',
  NULL,
  10,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 11. 富文本编辑器
INSERT INTO `biz_form_component` (
  `component_id`,
  `component_type`,
  `component_code`,
  `component_name`,
  `component_label`,
  `component_desc`,
  `component_config`,
  `options_config`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'comp_011',
  'RICH_TEXT',
  'activity_details',
  '活动详情',
  '活动详情',
  '富文本编辑器，用于编辑活动详情',
  '{"required":false,"height":300}',
  NULL,
  11,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# biz_form_scenario 表示例数据

# 1. 图片标注场景
INSERT INTO `biz_form_scenario` (
  `scenario_id`,
  `scenario_code`,
  `scenario_name`,
  `scenario_desc`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'scenario_001',
  'image_annotation',
  '图片标注',
  '图片标注场景，用于标注图片模板和产品图片',
  1,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 2. 产品信息录入场景
INSERT INTO `biz_form_scenario` (
  `scenario_id`,
  `scenario_code`,
  `scenario_name`,
  `scenario_desc`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES (
  'scenario_002',
  'product_info',
  '产品信息录入',
  '产品信息录入场景，用于录入产品基本信息',
  2,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# biz_form_scenario_component 表示例数据

# 图片标注场景的组件关联
INSERT INTO `biz_form_scenario_component` (
  `relation_id`,
  `scenario_id`,
  `component_id`,
  `component_config_override`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES 
('rel_001', 'scenario_001', 'comp_001', NULL, 1, 'ACTIVE', 0, NOW(), NOW()),
('rel_002', 'scenario_001', 'comp_002', NULL, 2, 'ACTIVE', 0, NOW(), NOW()),
('rel_003', 'scenario_001', 'comp_003', NULL, 3, 'ACTIVE', 0, NOW(), NOW()),
('rel_004', 'scenario_001', 'comp_004', NULL, 4, 'ACTIVE', 0, NOW(), NOW()),
('rel_005', 'scenario_001', 'comp_007', NULL, 5, 'ACTIVE', 0, NOW(), NOW()),
('rel_006', 'scenario_001', 'comp_008', NULL, 6, 'ACTIVE', 0, NOW(), NOW()),
('rel_007', 'scenario_001', 'comp_010', NULL, 7, 'ACTIVE', 0, NOW(), NOW());

# 产品信息录入场景的组件关联
INSERT INTO `biz_form_scenario_component` (
  `relation_id`,
  `scenario_id`,
  `component_id`,
  `component_config_override`,
  `sort_order`,
  `status`,
  `deleted`,
  `create_time`,
  `update_time`
) VALUES 
('rel_008', 'scenario_002', 'comp_001', NULL, 1, 'ACTIVE', 0, NOW(), NOW()),
('rel_009', 'scenario_002', 'comp_002', NULL, 2, 'ACTIVE', 0, NOW(), NOW()),
('rel_010', 'scenario_002', 'comp_003', NULL, 3, 'ACTIVE', 0, NOW(), NOW()),
('rel_011', 'scenario_002', 'comp_004', NULL, 4, 'ACTIVE', 0, NOW(), NOW()),
('rel_012', 'scenario_002', 'comp_005', NULL, 5, 'ACTIVE', 0, NOW(), NOW()),
('rel_013', 'scenario_002', 'comp_011', NULL, 6, 'ACTIVE', 0, NOW(), NOW());

