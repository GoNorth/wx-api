-- ----------------------------
-- 动态表单组件系统示例数据
-- 执行顺序：先执行组件数据，再执行场景数据，最后执行关联数据
-- ----------------------------

-- 如果表中有数据，建议先执行清空脚本：清空数据_动态表单组件.sql

# biz_form_component 表示例数据

# 1. 产品名称
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
  'INPUT',
  'product_name',
  '产品名称',
  '产品名称',
  '单行文本输入框，用于输入产品名称',
  '{"placeholder":"请输入产品名称","required":false,"maxLength":256}',
  NULL,
  1,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 2. 产品价格（多行格式）
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
  'TEXTAREA',
  'product_price',
  '产品价格',
  '产品价格',
  '多行文本输入框，用于输入产品价格，格式：牛排&79.9换行番茄&3.9',
  '{"placeholder":"请输入产品价格，格式：牛排&79.9换行番茄&3.9","required":true,"maxLength":1024,"rows":4}',
  NULL,
  2,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 3. 促销信息
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
  'INPUT',
  'promotion_input',
  '促销信息',
  '促销信息',
  '单行文本输入框，用于输入促销信息',
  '{"placeholder":"请输入促销信息","required":false,"maxLength":256}',
  NULL,
  3,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 4. 美术风格类型（*Agent模式）
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
  'SELECT',
  'style_type',
  '美术风格类型',
  '美术风格类型（*Agent模式）',
  '下拉选择框，用于选择美术风格类型',
  '{"placeholder":"请选择美术风格类型","required":false}',
  '[{"label":"国风插画","value":"国风插画"},{"label":"3D卡通","value":"3D卡通"},{"label":"写实摄影","value":"写实摄影"}]',
  4,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 5. 节日主题名称
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
  'INPUT',
  'festival_theme',
  '节日主题名称',
  '节日主题名称（以海报类型生效：[节日场景]）',
  '单行文本输入框，用于输入节日主题名称',
  '{"placeholder":"请输入节日主题名称","required":false,"maxLength":256}',
  NULL,
  5,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 6. 节气名称
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
  'INPUT',
  'seasonal_name',
  '节气名称',
  '节气名称',
  '单行文本输入框，用于输入节气名称',
  '{"placeholder":"请输入节气名称","required":false,"maxLength":256}',
  NULL,
  6,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 7. 年代(*Agent模式)
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
  'SELECT',
  'age_level',
  '年代',
  '年代',
  '下拉选择框，用于选择年代',
  '{"placeholder":"请选择年代","required":false}',
  '[{"label":"现代","value":"现代"},{"label":"1980年代","value":"1980年代"}]',
  7,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 8. 人物形象类型
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
  'SELECT',
  'character_type',
  '人物形象类型',
  '人物形象类型',
  '下拉选择框，用于选择人物形象类型',
  '{"placeholder":"请选择人物形象类型","required":false}',
  '[{"label":"3D","value":"3D"},{"label":"真人","value":"真人"},{"label":"卡通","value":"卡通"},{"label":"写实","value":"写实"},{"label":"插画","value":"插画"},{"label":"水彩","value":"水彩"},{"label":"油画","value":"油画"},{"label":"简约","value":"简约"},{"label":"复古","value":"复古"},{"label":"现代","value":"现代"},{"label":"国风","value":"国风"},{"label":"日系","value":"日系"},{"label":"欧美","value":"欧美"}]',
  8,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 9. 人物形象描述
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
  'TEXTAREA',
  'character_desc',
  '人物形象描述',
  '人物形象描述',
  '多行文本输入框，用于输入人物形象描述',
  '{"placeholder":"请输入人物形象描述","required":false,"maxLength":512,"rows":4}',
  NULL,
  9,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# biz_form_scenario 表示例数据

# 1. 生鲜场景
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
  'fresh_food',
  '生鲜',
  '生鲜场景，用于生鲜产品的图片标注',
  1,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 2. 餐饮场景
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
  'catering',
  '餐饮',
  '餐饮场景，用于餐饮产品的图片标注',
  2,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# biz_form_scenario_component 表示例数据

# 组件绑定顺序：1.product_name, 2.product_price, 3.promotion_input, 4.style_type, 5.festival_theme, 6.seasonal_name, 7.age_level, 8.character_type, 9.character_desc

# 生鲜场景的组件关联
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
('rel_001_004', 'scenario_001', 'comp_004', NULL, 1, 'ACTIVE', 0, NOW(), NOW()),
('rel_001_005', 'scenario_001', 'comp_005', NULL, 2, 'ACTIVE', 0, NOW(), NOW()),
('rel_001_006', 'scenario_001', 'comp_006', NULL, 3, 'ACTIVE', 0, NOW(), NOW()),
('rel_001_008', 'scenario_001', 'comp_008', NULL, 4, 'ACTIVE', 0, NOW(), NOW()),
('rel_001_007', 'scenario_001', 'comp_007', NULL, 5, 'ACTIVE', 0, NOW(), NOW()),
('rel_001_002', 'scenario_001', 'comp_002', NULL, 6, 'ACTIVE', 0, NOW(), NOW()),
('rel_001_009', 'scenario_001', 'comp_009', NULL, 7, 'ACTIVE', 0, NOW(), NOW()),
('rel_001_010', 'scenario_001', 'comp_010', NULL, 8, 'ACTIVE', 0, NOW(), NOW()),
('rel_001_011', 'scenario_001', 'comp_011', NULL, 9, 'ACTIVE', 0, NOW(), NOW());

# 餐饮场景的组件关联
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
('rel_002_004', 'scenario_002', 'comp_004', NULL, 1, 'ACTIVE', 0, NOW(), NOW()),
('rel_002_005', 'scenario_002', 'comp_005', NULL, 2, 'ACTIVE', 0, NOW(), NOW()),
('rel_002_006', 'scenario_002', 'comp_006', NULL, 3, 'ACTIVE', 0, NOW(), NOW()),
('rel_002_008', 'scenario_002', 'comp_008', NULL, 4, 'ACTIVE', 0, NOW(), NOW()),
('rel_002_007', 'scenario_002', 'comp_007', NULL, 5, 'ACTIVE', 0, NOW(), NOW()),
('rel_002_002', 'scenario_002', 'comp_002', NULL, 6, 'ACTIVE', 0, NOW(), NOW()),
('rel_002_009', 'scenario_002', 'comp_009', NULL, 7, 'ACTIVE', 0, NOW(), NOW()),
('rel_002_010', 'scenario_002', 'comp_010', NULL, 8, 'ACTIVE', 0, NOW(), NOW()),
('rel_002_011', 'scenario_002', 'comp_011', NULL, 9, 'ACTIVE', 0, NOW(), NOW());
