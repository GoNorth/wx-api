package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizFormScenarioComponentMapper;
import com.github.niefy.modules.biz.entity.BizFormScenarioComponent;
import com.github.niefy.modules.biz.service.BizFormScenarioComponentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 场景组件关联表
 *
 * @author niefy
 * @date 2026-01-09
 */
@Service
public class BizFormScenarioComponentServiceImpl extends ServiceImpl<BizFormScenarioComponentMapper, BizFormScenarioComponent> implements BizFormScenarioComponentService {

    @Override
    public IPage<BizFormScenarioComponent> queryPage(Map<String, Object> params) {
        String relationId = (String) params.get("relationId");
        String scenarioId = (String) params.get("scenarioId");
        String componentId = (String) params.get("componentId");
        String status = (String) params.get("status");
        
        QueryWrapper<BizFormScenarioComponent> queryWrapper = new QueryWrapper<BizFormScenarioComponent>()
                .eq(StringUtils.hasText(relationId), "relation_id", relationId)
                .eq(StringUtils.hasText(scenarioId), "scenario_id", scenarioId)
                .eq(StringUtils.hasText(componentId), "component_id", componentId)
                .eq(StringUtils.hasText(status), "status", status)
                .eq("deleted", 0);

        queryWrapper.orderByAsc("sort_order").orderByDesc("create_time");

        return this.page(new Query<BizFormScenarioComponent>().getPage(params), queryWrapper);
    }
}

