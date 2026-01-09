package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizFormScenarioMapper;
import com.github.niefy.modules.biz.entity.BizFormScenario;
import com.github.niefy.modules.biz.service.BizFormScenarioService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 表单场景表
 *
 * @author niefy
 * @date 2026-01-09
 */
@Service
public class BizFormScenarioServiceImpl extends ServiceImpl<BizFormScenarioMapper, BizFormScenario> implements BizFormScenarioService {

    @Override
    public IPage<BizFormScenario> queryPage(Map<String, Object> params) {
        String scenarioId = (String) params.get("scenarioId");
        String scenarioCode = (String) params.get("scenarioCode");
        String scenarioName = (String) params.get("scenarioName");
        String status = (String) params.get("status");
        
        QueryWrapper<BizFormScenario> queryWrapper = new QueryWrapper<BizFormScenario>()
                .eq(StringUtils.hasText(scenarioId), "scenario_id", scenarioId)
                .eq(StringUtils.hasText(scenarioCode), "scenario_code", scenarioCode)
                .like(StringUtils.hasText(scenarioName), "scenario_name", scenarioName)
                .eq(StringUtils.hasText(status), "status", status)
                .eq("deleted", 0);

        queryWrapper.orderByAsc("sort_order").orderByDesc("create_time");

        return this.page(new Query<BizFormScenario>().getPage(params), queryWrapper);
    }
}

