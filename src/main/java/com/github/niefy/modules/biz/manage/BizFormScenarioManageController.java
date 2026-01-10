package com.github.niefy.modules.biz.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizFormComponent;
import com.github.niefy.modules.biz.entity.BizFormScenario;
import com.github.niefy.modules.biz.entity.BizFormScenarioComponent;
import com.github.niefy.modules.biz.enums.FormStatusEnum;
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
 * 表单场景表-管理后台
 *
 * @author niefy
 * @date 2026-01-09
 */
@RestController
@RequestMapping("/manage/bizFormScenario")
@Api(tags = {"表单场景表-管理后台"})
public class BizFormScenarioManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizFormScenarioManageController.class);

    @Autowired
    private BizFormScenarioService bizFormScenarioService;

    @Autowired
    private BizFormComponentService bizFormComponentService;

    @Autowired
    private BizFormScenarioComponentService bizFormScenarioComponentService;

    /**
     * 列表（分页查询）
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizformscenario:list")
    @ApiOperation(value = "列表（分页查询）")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizFormScenarioService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息（详情）
     */
    @GetMapping("/info/{scenarioId}")
    // @RequiresPermissions("biz:bizformscenario:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("scenarioId") String scenarioId) {
        BizFormScenario bizFormScenario = bizFormScenarioService.getById(scenarioId);
        return R.ok().put("bizFormScenario", bizFormScenario);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizformscenario:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizFormScenario bizFormScenario) {
        // 设置默认值
        if (bizFormScenario.getScenarioId() == null || bizFormScenario.getScenarioId().isEmpty()) {
            bizFormScenario.setScenarioId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizFormScenario.getStatus() == null || bizFormScenario.getStatus().isEmpty()) {
            bizFormScenario.setStatus(FormStatusEnum.ACTIVE.getValue());
        }
        if (bizFormScenario.getSortOrder() == null) {
            bizFormScenario.setSortOrder(0);
        }
        if (bizFormScenario.getDeleted() == null) {
            bizFormScenario.setDeleted(0);
        }
        if (bizFormScenario.getCreateTime() == null) {
            bizFormScenario.setCreateTime(new Date());
        }
        bizFormScenario.setUpdateTime(new Date());
        
        bizFormScenarioService.save(bizFormScenario);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizformscenario:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizFormScenario bizFormScenario) {
        bizFormScenario.setUpdateTime(new Date());
        bizFormScenarioService.updateById(bizFormScenario);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizformscenario:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] scenarioIds) {
        bizFormScenarioService.removeByIds(Arrays.asList(scenarioIds));
        return R.ok();
    }

    /**
     * 获取场景列表（用于下拉框选择）
     * 只返回启用状态的场景
     */
    @GetMapping("/options")
    @ApiOperation(value = "获取场景列表（用于下拉框选择）")
    public R getScenarioOptions() {
        try {
            QueryWrapper<BizFormScenario> wrapper = new QueryWrapper<BizFormScenario>()
                    .eq("status", FormStatusEnum.ACTIVE.getValue())
                    .eq("deleted", 0)
                    .orderByAsc("sort_order");
            List<BizFormScenario> scenarios = bizFormScenarioService.list(wrapper);
            
            // 转换为下拉框格式：[{value: "scenarioId", label: "scenarioName"}]
            List<Map<String, Object>> options = scenarios.stream()
                    .map(scenario -> {
                        Map<String, Object> option = new HashMap<>();
                        option.put("value", scenario.getScenarioId());
                        option.put("label", scenario.getScenarioName());
                        option.put("scenarioCode", scenario.getScenarioCode());
                        return option;
                    })
                    .collect(Collectors.toList());
            
            return R.ok().put("list", options);
        } catch (Exception e) {
            logger.error("获取场景下拉选项失败", e);
            return R.error("获取场景下拉选项失败: " + e.getMessage());
        }
    }

    /**
     * 根据场景ID获取场景下的组件列表（用于动态表格展示）
     * 
     * @param scenarioId 场景ID（必填）
     * @return 组件配置列表
     */
    @GetMapping("/components/{scenarioId}")
    @ApiOperation(value = "根据场景ID获取场景下的组件列表")
    public R getComponentsByScenarioId(
            @ApiParam(value = "场景ID", required = true) @PathVariable("scenarioId") String scenarioId) {
        try {
            // 1. 查询场景信息
            BizFormScenario scenario = bizFormScenarioService.getById(scenarioId);
            if (scenario == null || scenario.getDeleted() == 1) {
                return R.error("场景不存在: " + scenarioId);
            }

            // 2. 查询该场景下的所有组件关联（按排序）
            QueryWrapper<BizFormScenarioComponent> relationWrapper = new QueryWrapper<BizFormScenarioComponent>()
                    .eq("scenario_id", scenarioId)
                    .eq("deleted", 0)
                    .orderByAsc("sort_order");
            List<BizFormScenarioComponent> relations = bizFormScenarioComponentService.list(relationWrapper);

            if (relations == null || relations.isEmpty()) {
                return R.ok().put("list", Collections.emptyList()).put("scenario", scenario);
            }

            // 3. 批量查询组件信息
            List<String> componentIds = relations.stream()
                    .map(BizFormScenarioComponent::getComponentId)
                    .collect(Collectors.toList());
            List<BizFormComponent> components = bizFormComponentService.listByIds(componentIds);
            
            // 创建组件ID到组件的映射
            Map<String, BizFormComponent> componentMap = components.stream()
                    .filter(c -> c.getDeleted() == 0)
                    .collect(Collectors.toMap(BizFormComponent::getComponentId, c -> c));

            // 4. 组装返回数据（按关联表的排序）
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (BizFormScenarioComponent relation : relations) {
                BizFormComponent component = componentMap.get(relation.getComponentId());
                if (component == null) {
                    continue; // 跳过已删除的组件
                }

                // 构建组件配置对象
                Map<String, Object> componentConfig = new HashMap<>();
                
                // 基础信息
                componentConfig.put("relationId", relation.getRelationId());
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
                
                // 场景内配置
                componentConfig.put("sortOrder", relation.getSortOrder());
                componentConfig.put("status", relation.getStatus());
                componentConfig.put("componentConfigOverride", relation.getComponentConfigOverride());
                
                resultList.add(componentConfig);
            }

            return R.ok()
                    .put("list", resultList)
                    .put("scenario", scenario);
        } catch (Exception e) {
            logger.error("获取场景组件列表失败，scenarioId: {}", scenarioId, e);
            return R.error("获取场景组件列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据场景代码获取场景下的组件列表（用于动态表格展示）
     * 
     * @param scenarioCode 场景代码（必填）
     * @return 组件配置列表
     */
    @GetMapping("/components/by-code")
    @ApiOperation(value = "根据场景代码获取场景下的组件列表")
    public R getComponentsByScenarioCode(
            @ApiParam(value = "场景代码", required = true) @RequestParam String scenarioCode) {
        try {
            // 1. 根据场景代码查询场景
            QueryWrapper<BizFormScenario> scenarioWrapper = new QueryWrapper<BizFormScenario>()
                    .eq("scenario_code", scenarioCode)
                    .eq("deleted", 0);
            BizFormScenario scenario = bizFormScenarioService.getOne(scenarioWrapper);
            
            if (scenario == null) {
                return R.error("场景不存在: " + scenarioCode);
            }

            // 调用根据场景ID获取组件列表的方法
            return getComponentsByScenarioId(scenario.getScenarioId());
        } catch (Exception e) {
            logger.error("获取场景组件列表失败，scenarioCode: {}", scenarioCode, e);
            return R.error("获取场景组件列表失败: " + e.getMessage());
        }
    }
}

