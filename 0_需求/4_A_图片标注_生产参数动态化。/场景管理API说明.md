# 场景管理API说明

## 概述

场景管理API提供了场景的CRUD操作，以及获取场景组件列表的功能。主要用于管理后台的场景配置和前端动态表单的组件获取。

## API接口列表

### 1. 场景列表（分页查询）

**接口地址：** `GET /manage/bizFormScenario/list`

**请求参数：**
- `page` - 页码（可选，默认1）
- `limit` - 每页数量（可选，默认10）
- `scenarioId` - 场景ID（可选，精确查询）
- `scenarioCode` - 场景代码（可选，精确查询）
- `scenarioName` - 场景名称（可选，模糊查询）
- `status` - 状态（可选，ACTIVE/INACTIVE）

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "page": {
    "totalCount": 2,
    "pageSize": 10,
    "totalPage": 1,
    "currPage": 1,
    "list": [
      {
        "scenarioId": "scenario_001",
        "scenarioCode": "image_annotation",
        "scenarioName": "图片标注",
        "scenarioDesc": "图片标注场景，用于标注图片模板和产品图片",
        "sortOrder": 1,
        "status": "ACTIVE",
        "deleted": 0,
        "createTime": "2026-01-09 10:00:00",
        "updateTime": "2026-01-09 10:00:00"
      }
    ]
  }
}
```

### 2. 场景详情

**接口地址：** `GET /manage/bizFormScenario/info/{scenarioId}`

**请求参数：**
- `scenarioId` - 场景ID（路径参数）

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "bizFormScenario": {
    "scenarioId": "scenario_001",
    "scenarioCode": "image_annotation",
    "scenarioName": "图片标注",
    "scenarioDesc": "图片标注场景，用于标注图片模板和产品图片",
    "sortOrder": 1,
    "status": "ACTIVE",
    "deleted": 0,
    "createTime": "2026-01-09 10:00:00",
    "updateTime": "2026-01-09 10:00:00"
  }
}
```

### 3. 保存场景

**接口地址：** `POST /manage/bizFormScenario/save`

**请求体示例：**
```json
{
  "scenarioCode": "new_scenario",
  "scenarioName": "新场景",
  "scenarioDesc": "新场景描述",
  "sortOrder": 1,
  "status": "ACTIVE"
}
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "success"
}
```

**说明：**
- 如果不传 `scenarioId`，系统会自动生成UUID
- 如果不传 `status`，默认为 `ACTIVE`
- 如果不传 `sortOrder`，默认为 `0`
- `createTime` 和 `updateTime` 会自动设置

### 4. 修改场景

**接口地址：** `POST /manage/bizFormScenario/update`

**请求体示例：**
```json
{
  "scenarioId": "scenario_001",
  "scenarioCode": "image_annotation",
  "scenarioName": "图片标注（已修改）",
  "scenarioDesc": "修改后的描述",
  "sortOrder": 2,
  "status": "ACTIVE"
}
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "success"
}
```

### 5. 删除场景

**接口地址：** `POST /manage/bizFormScenario/delete`

**请求体示例：**
```json
["scenario_001", "scenario_002"]
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "success"
}
```

### 6. 获取场景下拉选项（用于下拉框选择）

**接口地址：** `GET /manage/bizFormScenario/options`

**说明：** 只返回启用状态的场景，用于前端下拉框选择

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "list": [
    {
      "value": "scenario_001",
      "label": "图片标注",
      "scenarioCode": "image_annotation"
    },
    {
      "value": "scenario_002",
      "label": "产品信息录入",
      "scenarioCode": "product_info"
    }
  ]
}
```

**前端使用示例：**
```javascript
// 获取场景下拉选项
fetch('/manage/bizFormScenario/options')
  .then(res => res.json())
  .then(data => {
    // data.list 就是下拉框的选项数组
    // [{value: "scenario_001", label: "图片标注", scenarioCode: "image_annotation"}, ...]
  });
```

### 7. 根据场景ID获取组件列表（用于动态表格展示）

**接口地址：** `GET /manage/bizFormScenario/components/{scenarioId}`

**请求参数：**
- `scenarioId` - 场景ID（路径参数）

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "scenario": {
    "scenarioId": "scenario_001",
    "scenarioCode": "image_annotation",
    "scenarioName": "图片标注",
    "scenarioDesc": "图片标注场景，用于标注图片模板和产品图片",
    "sortOrder": 1,
    "status": "ACTIVE"
  },
  "list": [
    {
      "relationId": "rel_001",
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
      "sortOrder": 1,
      "status": "ACTIVE",
      "componentConfigOverride": null
    },
    {
      "relationId": "rel_003",
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
      "sortOrder": 3,
      "status": "ACTIVE",
      "componentConfigOverride": null
    }
  ]
}
```

**说明：**
- 返回的组件列表按照 `sortOrder` 排序
- 包含场景信息和组件列表
- 组件配置已合并（默认配置 + 场景覆盖配置）
- 包含关联关系ID（`relationId`），可用于后续的关联关系管理

### 8. 根据场景代码获取组件列表

**接口地址：** `GET /manage/bizFormScenario/components/by-code?scenarioCode={scenarioCode}`

**请求参数：**
- `scenarioCode` - 场景代码（查询参数）

**响应格式：** 与接口7相同

**说明：** 此接口是接口7的便捷版本，通过场景代码查询，内部会先根据场景代码查询场景ID，然后调用接口7的逻辑。

## 使用流程

### 前端动态表单展示流程

1. **获取场景下拉选项**
   ```
   GET /manage/bizFormScenario/options
   ```
   用户在下拉框中选择场景

2. **根据选择的场景获取组件列表**
   ```
   GET /manage/bizFormScenario/components/{scenarioId}
   ```
   根据返回的组件列表动态渲染表单

3. **动态渲染表单**
   前端根据返回的组件配置，动态生成表单控件：
   - `componentType` 决定渲染什么类型的组件
   - `config` 包含组件的配置信息（placeholder、required等）
   - `options` 包含选项数据（用于SELECT、RADIO、CHECKBOX）

### 管理后台场景管理流程

1. **查看场景列表**
   ```
   GET /manage/bizFormScenario/list
   ```

2. **查看场景详情**
   ```
   GET /manage/bizFormScenario/info/{scenarioId}
   ```

3. **查看场景下的组件列表**
   ```
   GET /manage/bizFormScenario/components/{scenarioId}
   ```

4. **创建/修改/删除场景**
   ```
   POST /manage/bizFormScenario/save
   POST /manage/bizFormScenario/update
   POST /manage/bizFormScenario/delete
   ```

## 注意事项

1. **场景下拉选项**：只返回启用状态（`status=ACTIVE`）的场景
2. **组件列表**：返回所有状态的组件（包括禁用的），但会过滤已删除的组件
3. **排序**：组件列表按照场景关联表中的 `sort_order` 排序
4. **配置合并**：组件配置会合并默认配置和场景覆盖配置，场景覆盖配置优先
5. **关联关系**：返回的组件列表中包含 `relationId`，可用于后续管理场景与组件的关联关系

## 与 Common API 的区别

| 功能 | 管理后台API | Common API |
|------|------------|-----------|
| 场景列表 | `/manage/bizFormScenario/list`（分页，包含所有状态） | `/common/form-components/scenarios`（只返回启用状态） |
| 场景下拉选项 | `/manage/bizFormScenario/options`（格式化为下拉选项） | `/common/form-components/scenarios`（原始数据） |
| 组件列表 | `/manage/bizFormScenario/components/{scenarioId}`（包含关联信息） | `/common/form-components/list?scenarioCode=xxx`（只返回启用组件） |
| 用途 | 管理后台配置和管理 | 前端动态表单渲染 |

