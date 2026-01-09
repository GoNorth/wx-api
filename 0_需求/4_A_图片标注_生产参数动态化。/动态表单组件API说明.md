# 动态表单组件API说明

## 概述

动态表单组件系统提供了灵活的组件配置能力，支持通过数据库动态配置前端表单组件。系统包含三个核心表：
- `biz_form_component` - 表单组件表（定义所有可用的组件）
- `biz_form_scenario` - 表单场景表（定义不同的使用场景）
- `biz_form_scenario_component` - 场景组件关联表（定义场景包含哪些组件）

## API接口

### 1. 根据场景代码获取动态组件列表

**接口地址：** `GET /common/form-components/list`

**请求参数：**
- `scenarioCode` (必填) - 场景代码，如：`image_annotation`

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "list": [
    {
      "componentId": "comp_001",
      "componentType": "INPUT",
      "componentCode": "dish_name",
      "componentName": "菜品名称",
      "componentLabel": "菜品名称",
      "componentDesc": "单行文本输入框，用于输入菜品名称",
      "config": {
        "placeholder": "请输入菜品名称",
        "required": true,
        "maxLength": 100,
        "minLength": 1
      },
      "options": [],
      "sortOrder": 1
    },
    {
      "componentId": "comp_003",
      "componentType": "SELECT",
      "componentCode": "dish_category",
      "componentName": "菜品分类",
      "componentLabel": "菜品分类",
      "componentDesc": "下拉选择框，用于选择菜品分类",
      "config": {
        "placeholder": "请选择菜品分类",
        "required": true
      },
      "options": [
        {"label": "炒菜", "value": "炒菜"},
        {"label": "汤类", "value": "汤类"},
        {"label": "凉菜", "value": "凉菜"},
        {"label": "主食", "value": "主食"}
      ],
      "sortOrder": 3
    }
  ]
}
```

### 2. 获取所有启用的场景列表

**接口地址：** `GET /common/form-components/scenarios`

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "list": [
    {
      "scenarioId": "scenario_001",
      "scenarioCode": "image_annotation",
      "scenarioName": "图片标注",
      "scenarioDesc": "图片标注场景，用于标注图片模板和产品图片",
      "sortOrder": 1,
      "status": "ACTIVE"
    }
  ]
}
```

### 3. 获取所有启用的组件列表

**接口地址：** `GET /common/form-components/components`

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "list": [
    {
      "componentId": "comp_001",
      "componentType": "INPUT",
      "componentCode": "dish_name",
      "componentName": "菜品名称",
      "componentLabel": "菜品名称",
      "componentDesc": "单行文本输入框，用于输入菜品名称",
      "componentConfig": "{\"placeholder\":\"请输入菜品名称\",\"required\":true,\"maxLength\":100}",
      "optionsConfig": null,
      "sortOrder": 1,
      "status": "ACTIVE"
    }
  ]
}
```

## 组件类型说明

系统支持以下组件类型：

| 组件类型 | 说明 | 适用场景 |
|---------|------|---------|
| INPUT | 单行输入框 | 文本输入 |
| TEXTAREA | 多行输入框 | 长文本输入 |
| SELECT | 下拉框 | 单选选项 |
| DATE | 日期选择器 | 日期选择 |
| DATETIME | 日期时间选择器 | 日期时间选择 |
| NUMBER | 数字输入框 | 数字输入 |
| SWITCH | 开关 | 布尔值选择 |
| RADIO | 单选框 | 单选选项（横向排列） |
| CHECKBOX | 多选框 | 多选选项 |
| UPLOAD | 文件上传 | 文件上传 |
| RICH_TEXT | 富文本编辑器 | 富文本编辑 |

## 配置说明

### componentConfig 配置项

组件配置JSON支持以下通用配置项：

```json
{
  "placeholder": "占位符文本",
  "required": true,           // 是否必填
  "defaultValue": "默认值",   // 默认值
  "maxLength": 100,          // 最大长度（文本类组件）
  "minLength": 1,            // 最小长度（文本类组件）
  "max": 9999,              // 最大值（数字类组件）
  "min": 0,                 // 最小值（数字类组件）
  "precision": 2,           // 小数位数（数字类组件）
  "step": 0.01,            // 步长（数字类组件）
  "rows": 4,               // 行数（多行输入框）
  "format": "YYYY-MM-DD",  // 日期格式（日期类组件）
  "accept": "image/*",     // 文件类型（文件上传）
  "maxSize": 5242880,      // 最大文件大小（字节，文件上传）
  "maxCount": 1,           // 最大文件数量（文件上传）
  "listType": "picture-card", // 列表类型（文件上传）
  "height": 300            // 高度（富文本编辑器）
}
```

### optionsConfig 配置项

选项配置JSON格式（用于SELECT、RADIO、CHECKBOX）：

```json
[
  {"label": "选项1", "value": "value1"},
  {"label": "选项2", "value": "value2"}
]
```

## 使用流程

1. **创建组件**：在 `biz_form_component` 表中创建组件定义
2. **创建场景**：在 `biz_form_scenario` 表中创建场景定义
3. **关联组件**：在 `biz_form_scenario_component` 表中将组件关联到场景
4. **前端调用**：前端通过场景代码调用API获取组件列表
5. **动态渲染**：前端根据返回的组件配置动态渲染表单

## 场景覆盖配置

在 `biz_form_scenario_component` 表中，可以通过 `component_config_override` 字段覆盖组件的默认配置。例如：

- 组件默认配置：`{"required": false, "maxLength": 100}`
- 场景覆盖配置：`{"required": true}`
- 最终配置：`{"required": true, "maxLength": 100}` （覆盖配置优先）

## 注意事项

1. 只有状态为 `ACTIVE` 且未删除的记录才会被返回
2. 组件列表按照 `sort_order` 字段排序
3. 如果场景不存在或已禁用，API会返回错误信息
4. 组件配置和选项配置都是JSON格式字符串，需要前端解析

