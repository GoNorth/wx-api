package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizPlanItemMapper;
import com.github.niefy.modules.biz.entity.BizPlanItem;
import com.github.niefy.modules.biz.service.BizPlanItemService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 计划项目表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Service
public class BizPlanItemServiceImpl extends ServiceImpl<BizPlanItemMapper, BizPlanItem> implements BizPlanItemService {

    @Override
    public IPage<BizPlanItem> queryPage(Map<String, Object> params) {
        String itemId = (String) params.get("itemId");
        String planId = (String) params.get("planId");
        String dateKey = (String) params.get("dateKey");
        String timeSlot = (String) params.get("timeSlot");
        String marketingTheme = (String) params.get("marketingTheme");
        String platform = (String) params.get("platform");
        String contentTag = (String) params.get("contentTag");
        String status = (String) params.get("status");

        return this.page(
            new Query<BizPlanItem>().getPage(params),
            new QueryWrapper<BizPlanItem>()
                .eq(StringUtils.hasText(itemId), "item_id", itemId)
                .eq(StringUtils.hasText(planId), "plan_id", planId)
                .eq(StringUtils.hasText(dateKey), "date_key", dateKey)
                .eq(StringUtils.hasText(timeSlot), "time_slot", timeSlot)
                .eq(StringUtils.hasText(marketingTheme), "marketing_theme", marketingTheme)
                .eq(StringUtils.hasText(platform), "platform", platform)
                .eq(StringUtils.hasText(contentTag), "content_tag", contentTag)
                .eq(StringUtils.hasText(status), "status", status)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
    }
}

