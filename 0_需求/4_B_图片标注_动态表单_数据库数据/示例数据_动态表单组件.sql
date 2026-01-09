-- ----------------------------
-- 动态表单组件系统示例数据
-- 执行顺序：先执行组件数据，再执行场景数据，最后执行关联数据
-- ----------------------------

-- 如果表中有数据，建议先执行清空脚本：清空数据_动态表单组件.sql

# biz_form_component 表示例数据

# 1. 促销信息标题（注：海报主标题）
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
  'sub_title',
  '促销信息标题',
  '促销信息标题（注：海报主标题）',
  '单行文本输入框，用于输入促销信息标题（注：海报主标题）',
  '{"placeholder":"请输入促销信息标题","required":true,"maxLength":256,"minLength":1}',
  NULL,
  1,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 2. 节气名称
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
  '节气名称（以海报类型生效：[节气产品]）',
  '单行文本输入框，用于输入节气名称',
  '{"placeholder":"请输入节气名称","required":false,"maxLength":256}',
  NULL,
  2,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 3. 产品价格（文本格式）
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
  'INPUT',
  'price_str',
  '产品价格',
  '产品价格（如：土豆 0.99元/斤）',
  '单行文本输入框，用于输入产品价格',
  '{"placeholder":"请输入产品价格，如：土豆 0.99元/斤","required":false,"maxLength":256}',
  NULL,
  3,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 4. 产品名称
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
  4,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 5. 产品价格（多行格式）
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
  '产品价格(##格式：牛排&79.9\换行\番茄&3.9)',
  '多行文本输入框，用于输入产品价格，格式：牛排&79.9\换行\番茄&3.9',
  '{"placeholder":"请输入产品价格，格式：牛排&79.9\\换行\\番茄&3.9","required":true,"maxLength":1024,"rows":4}',
  NULL,
  5,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 6. 促销信息
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
  6,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 7. 节日主题名称
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
  7,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 8. 美术风格类型（*Agent模式）
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
  8,
  'ACTIVE',
  0,
  NOW(),
  NOW()
);

# 9. 年代(*Agent模式)
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
  '年代(*Agent模式)',
  '下拉选择框，用于选择年代',
  '{"placeholder":"请选择年代","required":false}',
  '[{"label":"现代","value":"现代"},{"label":"1980年代","value":"1980年代"}]',
  9,
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
('rel_005', 'scenario_001', 'comp_005', NULL, 5, 'ACTIVE', 0, NOW(), NOW()),
('rel_006', 'scenario_001', 'comp_006', NULL, 6, 'ACTIVE', 0, NOW(), NOW()),
('rel_007', 'scenario_001', 'comp_007', NULL, 7, 'ACTIVE', 0, NOW(), NOW()),
('rel_008', 'scenario_001', 'comp_008', NULL, 8, 'ACTIVE', 0, NOW(), NOW()),
('rel_009', 'scenario_001', 'comp_009', NULL, 9, 'ACTIVE', 0, NOW(), NOW());
