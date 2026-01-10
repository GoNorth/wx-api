package com.github.niefy.modules.common.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizFormComponent;
import com.github.niefy.modules.biz.entity.BizFormScenario;
import com.github.niefy.modules.biz.entity.BizFormScenarioComponent;
import com.github.niefy.modules.biz.service.BizFormComponentService;
import com.github.niefy.modules.biz.service.BizFormScenarioComponentService;
import com.github.niefy.modules.biz.service.BizFormScenarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态表单组件Controller
 * 提供前端动态组件配置的API
 *
 * @author niefy
 * @date 2026-01-09
 */
@RestController
@RequestMapping("/common/form-components")
@Api(tags = {"动态表单组件"})
public class FormComponentController {
    private static final Logger logger = LoggerFactory.getLogger(FormComponentController.class);

    @Autowired
    private BizFormScenarioService bizFormScenarioService;

    @Autowired
    private BizFormComponentService bizFormComponentService;

    @Autowired
    private BizFormScenarioComponentService bizFormScenarioComponentService;

    /**
     * 根据场景代码获取动态组件列表
     * 
     * @param scenarioCode 场景代码（必填）
     * @return 组件配置列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "根据场景代码获取动态组件列表")
    public R getComponentsByScenario(
            @ApiParam(value = "场景代码", required = true) @RequestParam String scenarioCode) {
        try {
            // 1. 根据场景代码查询场景
            QueryWrapper<BizFormScenario> scenarioWrapper = new QueryWrapper<BizFormScenario>()
                    .eq("scenario_code", scenarioCode)
                    .eq("status", "ACTIVE")
                    .eq("deleted", 0);
            BizFormScenario scenario = bizFormScenarioService.getOne(scenarioWrapper);
            
            if (scenario == null) {
                return R.error("场景不存在或已禁用: " + scenarioCode);
            }

            // 2. 查询该场景下的所有组件关联（按排序）
            QueryWrapper<BizFormScenarioComponent> relationWrapper = new QueryWrapper<BizFormScenarioComponent>()
                    .eq("scenario_id", scenario.getScenarioId())
                    .eq("status", "ACTIVE")
                    .eq("deleted", 0)
                    .orderByAsc("sort_order");
            List<BizFormScenarioComponent> relations = bizFormScenarioComponentService.list(relationWrapper);

            if (relations == null || relations.isEmpty()) {
                return R.ok().put("list", Collections.emptyList());
            }

            // 3. 批量查询组件信息
            List<String> componentIds = relations.stream()
                    .map(BizFormScenarioComponent::getComponentId)
                    .collect(Collectors.toList());
            List<BizFormComponent> components = bizFormComponentService.listByIds(componentIds);
            
            // 创建组件ID到组件的映射
            Map<String, BizFormComponent> componentMap = components.stream()
                    .filter(c -> c.getDeleted() == 0 && "ACTIVE".equals(c.getStatus()))
                    .collect(Collectors.toMap(BizFormComponent::getComponentId, c -> c));

            // 4. 组装返回数据（按关联表的排序）
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (BizFormScenarioComponent relation : relations) {
                BizFormComponent component = componentMap.get(relation.getComponentId());
                if (component == null) {
                    continue; // 跳过已删除或禁用的组件
                }

                // 构建组件配置对象
                Map<String, Object> componentConfig = new HashMap<>();
                
                // 基础信息
                componentConfig.put("componentId", component.getComponentId());
                componentConfig.put("componentType", component.getComponentType());
                componentConfig.put("componentCode", component.getComponentCode());
                componentConfig.put("componentName", component.getComponentName());
                componentConfig.put("componentLabel", StringUtils.hasText(component.getComponentLabel()) 
                        ? component.getComponentLabel() : component.getComponentName());
                componentConfig.put("componentDesc", component.getComponentDesc());
                
                // 合并组件配置（默认配置 + 场景覆盖配置）
                JSONObject defaultConfig = new JSONObject();
                if (StringUtils.hasText(component.getComponentConfig())) {
                    try {
                        defaultConfig = JSON.parseObject(component.getComponentConfig());
                    } catch (Exception e) {
                        logger.warn("解析组件默认配置失败，componentId: {}, config: {}", 
                                component.getComponentId(), component.getComponentConfig(), e);
                        defaultConfig = new JSONObject();
                    }
                }
                
                JSONObject overrideConfig = new JSONObject();
                if (StringUtils.hasText(relation.getComponentConfigOverride())) {
                    try {
                        overrideConfig = JSON.parseObject(relation.getComponentConfigOverride());
                    } catch (Exception e) {
                        logger.warn("解析组件覆盖配置失败，relationId: {}, config: {}", 
                                relation.getRelationId(), relation.getComponentConfigOverride(), e);
                        overrideConfig = new JSONObject();
                    }
                }
                
                // 合并配置（覆盖配置优先）
                JSONObject mergedConfig = new JSONObject();
                mergedConfig.putAll(defaultConfig);
                mergedConfig.putAll(overrideConfig);
                componentConfig.put("config", mergedConfig);
                
                // 选项配置（下拉框、单选框、多选框等）
                if (StringUtils.hasText(component.getOptionsConfig())) {
                    try {
                        List<Object> optionsRaw = JSON.parseArray(component.getOptionsConfig());
                        List<Map<String, Object>> options = new ArrayList<>();
                        for (Object item : optionsRaw) {
                            if (item instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> mapItem = (Map<String, Object>) item;
                                options.add(mapItem);
                            }
                        }
                        componentConfig.put("options", options);
                    } catch (Exception e) {
                        logger.warn("解析组件选项配置失败，componentId: {}", component.getComponentId(), e);
                        componentConfig.put("options", Collections.emptyList());
                    }
                } else {
                    componentConfig.put("options", Collections.emptyList());
                }
                
                // 场景内排序
                componentConfig.put("sortOrder", relation.getSortOrder());
                
                resultList.add(componentConfig);
            }

            return R.ok().put("list", resultList);
        } catch (Exception e) {
            logger.error("获取动态组件列表失败，scenarioCode: {}", scenarioCode, e);
            return R.error("获取动态组件列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有启用的场景列表
     * 
     * @return 场景列表
     */
    @GetMapping("/scenarios")
    @ApiOperation(value = "获取所有启用的场景列表")
    public R getScenarios() {
        try {
            QueryWrapper<BizFormScenario> wrapper = new QueryWrapper<BizFormScenario>()
                    .eq("status", "ACTIVE")
                    .eq("deleted", 0)
                    .orderByAsc("sort_order");
            List<BizFormScenario> scenarios = bizFormScenarioService.list(wrapper);
            return R.ok().put("list", scenarios);
        } catch (Exception e) {
            logger.error("获取场景列表失败", e);
            return R.error("获取场景列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有启用的组件列表（不区分场景）
     * 
     * @return 组件列表
     */
    @GetMapping("/components")
    @ApiOperation(value = "获取所有启用的组件列表")
    public R getAllComponents() {
        try {
            QueryWrapper<BizFormComponent> wrapper = new QueryWrapper<BizFormComponent>()
                    .eq("status", "ACTIVE")
                    .eq("deleted", 0)
                    .orderByAsc("sort_order");
            List<BizFormComponent> components = bizFormComponentService.list(wrapper);
            return R.ok().put("list", components);
        } catch (Exception e) {
            logger.error("获取组件列表失败", e);
            return R.error("获取组件列表失败: " + e.getMessage());
        }
    }
}

