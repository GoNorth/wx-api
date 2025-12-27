package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizPlanHeaderMapper;
import com.github.niefy.modules.biz.entity.BizPlanHeader;
import com.github.niefy.modules.biz.service.BizPlanHeaderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 计划头部表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Service
public class BizPlanHeaderServiceImpl extends ServiceImpl<BizPlanHeaderMapper, BizPlanHeader> implements BizPlanHeaderService {

    @Override
    public IPage<BizPlanHeader> queryPage(Map<String, Object> params) {
        String planId = (String) params.get("planId");
        String storeId = (String) params.get("storeId");
        String planType = (String) params.get("planType");
        String strategyType = (String) params.get("strategyType");
        String status = (String) params.get("status");
        String planName = (String) params.get("planName");

        return this.page(
            new Query<BizPlanHeader>().getPage(params),
            new QueryWrapper<BizPlanHeader>()
                .eq(StringUtils.hasText(planId), "plan_id", planId)
                .eq(StringUtils.hasText(storeId), "store_id", storeId)
                .eq(StringUtils.hasText(planType), "plan_type", planType)
                .eq(StringUtils.hasText(strategyType), "strategy_type", strategyType)
                .eq(StringUtils.hasText(status), "status", status)
                .like(StringUtils.hasText(planName), "plan_name", planName)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
    }
}

