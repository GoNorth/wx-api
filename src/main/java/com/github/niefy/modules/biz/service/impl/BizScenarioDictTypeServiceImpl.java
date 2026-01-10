package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizScenarioDictTypeMapper;
import com.github.niefy.modules.biz.entity.BizScenarioDictType;
import com.github.niefy.modules.biz.service.BizScenarioDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 表单场景-字典类型绑定表
 *
 * @author niefy
 * @date 2025-01-09
 */
@Service
public class BizScenarioDictTypeServiceImpl extends ServiceImpl<BizScenarioDictTypeMapper, BizScenarioDictType> implements BizScenarioDictTypeService {

    @Override
    public IPage<BizScenarioDictType> queryPage(Map<String, Object> params) {
        String relationId = (String) params.get("relationId");
        String scenarioId = (String) params.get("scenarioId");
        String dictTypeCode = (String) params.get("dictTypeCode");
        String status = (String) params.get("status");
        
        QueryWrapper<BizScenarioDictType> queryWrapper = new QueryWrapper<BizScenarioDictType>()
                .eq(StringUtils.hasText(relationId), "relation_id", relationId)
                .eq(StringUtils.hasText(scenarioId), "scenario_id", scenarioId)
                .eq(StringUtils.hasText(dictTypeCode), "dict_type_code", dictTypeCode)
                .eq(StringUtils.hasText(status), "status", status)
                .eq("deleted", 0);

        queryWrapper.orderByAsc("sort_order").orderByDesc("create_time");

        return this.page(new Query<BizScenarioDictType>().getPage(params), queryWrapper);
    }

    @Override
    public boolean isBound(String scenarioId, String dictTypeCode) {
        if (!StringUtils.hasText(scenarioId) || !StringUtils.hasText(dictTypeCode)) {
            return false;
        }
        
        BizScenarioDictType binding = this.getOne(
            new QueryWrapper<BizScenarioDictType>()
                .eq("scenario_id", scenarioId)
                .eq("dict_type_code", dictTypeCode)
                .eq("status", "ACTIVE")
                .eq("deleted", 0)
        );
        
        return binding != null;
    }
}

