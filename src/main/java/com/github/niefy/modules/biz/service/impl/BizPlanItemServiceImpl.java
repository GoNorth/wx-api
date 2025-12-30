package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.DateKeyUtils;
import com.github.niefy.common.utils.PlatformUtils;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizPlanItemMapper;
import com.github.niefy.modules.biz.entity.BizPlanItem;
import com.github.niefy.modules.biz.service.BizPlanItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
        String storeId = (String) params.get("storeId");
        String dateKey = (String) params.get("dateKey");
        String timeSlot = (String) params.get("timeSlot");
        String marketingTheme = (String) params.get("marketingTheme");
        String platform = (String) params.get("platform");
        String contentTag = (String) params.get("contentTag");
        String status = (String) params.get("status");
        
        // 获取 strategy_type 参数（支持 strategy_type 和 strategyType 两种格式）
        Object strategyTypeObj = params.get("strategy_type");
        if (strategyTypeObj == null) {
            strategyTypeObj = params.get("strategyType");
        }
        Integer strategyType = null;
        if (strategyTypeObj != null) {
            if (strategyTypeObj instanceof Integer) {
                strategyType = (Integer) strategyTypeObj;
            } else if (strategyTypeObj instanceof String) {
                try {
                    strategyType = Integer.parseInt((String) strategyTypeObj);
                } catch (NumberFormatException e) {
                    // 忽略解析错误
                }
            }
        }

        QueryWrapper<BizPlanItem> queryWrapper = new QueryWrapper<BizPlanItem>()
                .eq(StringUtils.hasText(itemId), "item_id", itemId)
                .eq(StringUtils.hasText(planId), "plan_id", planId)
                .eq(StringUtils.hasText(timeSlot), "time_slot", timeSlot)
                .eq(StringUtils.hasText(marketingTheme), "marketing_theme", marketingTheme)
                .eq(StringUtils.hasText(platform), "platform", platform)
                .eq(StringUtils.hasText(contentTag), "content_tag", contentTag)
                .eq(StringUtils.hasText(status), "status", status)
                .eq("deleted", 0);

        // 处理 dateKey：如果 strategy_type=2，需要将 dateKey 转换为周格式
        if (StringUtils.hasText(dateKey)) {
            String finalDateKey = dateKey;
            // 如果 strategy_type=2（周计划表），将日期转换为周格式
            if (strategyType != null && strategyType == 2) {
                try {
                    finalDateKey = DateKeyUtils.convertDateToWeek(dateKey);
                } catch (IllegalArgumentException e) {
                    // 如果转换失败（可能已经是周格式），使用原值
                    finalDateKey = dateKey;
                }
            }
            queryWrapper.eq("date_key", finalDateKey);
        }
        
        // 如果传入了 strategy_type，也作为查询条件
        if (strategyType != null) {
            queryWrapper.eq("strategy_type", strategyType);
        }

        // 处理 storeId：需要通过子查询关联 biz_plan_header 表
        if (StringUtils.hasText(storeId)) {
            queryWrapper.inSql("plan_id", 
                "SELECT plan_id FROM biz_plan_header WHERE store_id = '" + storeId + "' AND deleted = 0");
        }

        queryWrapper.orderByDesc("create_time");

        IPage<BizPlanItem> page = this.page(new Query<BizPlanItem>().getPage(params), queryWrapper);
        
        // 设置 platformDesc 和 contentTagDesc
        if (page != null && page.getRecords() != null) {
            for (BizPlanItem item : page.getRecords()) {
                item.setPlatformDesc(PlatformUtils.getPlatformDesc(item.getPlatform()));
                item.setContentTagDesc(PlatformUtils.getContentTagDesc(item.getContentTag()));
            }
        }
        
        return page;
    }

    @Override
    public void saveOrUpdateItem(BizPlanItem bizPlanItem) {
        // 如果传入了itemId，先查询数据库
        if (bizPlanItem.getItemId() != null && !bizPlanItem.getItemId().isEmpty()) {
            String itemId = bizPlanItem.getItemId();
            BizPlanItem existingItem = this.getById(itemId);
            if (existingItem != null) {
                // 存在记录，用传入参数覆盖，但保留创建时间
                Date originalCreateTime = existingItem.getCreateTime();
                BeanUtils.copyProperties(bizPlanItem, existingItem);
                existingItem.setCreateTime(originalCreateTime);
                existingItem.setUpdateTime(new Date());
                bizPlanItem = existingItem;
            } else {
                // 不存在记录，设置默认值后保存
                setItemDefaultValues(bizPlanItem);
            }
        } else {
            // 生成itemId（如果为空）
            bizPlanItem.setItemId(UUID.randomUUID().toString().replace("-", ""));
            setItemDefaultValues(bizPlanItem);
        }
        
        // 保存或更新
        this.saveOrUpdate(bizPlanItem);
    }

    /**
     * 设置计划项目默认值
     * @param bizPlanItem 计划项目信息
     */
    private void setItemDefaultValues(BizPlanItem bizPlanItem) {
        if (bizPlanItem.getDeleted() == null) {
            bizPlanItem.setDeleted(0);
        }
        if (bizPlanItem.getStatus() == null) {
            bizPlanItem.setStatus(0);
        }
        if (bizPlanItem.getCreateTime() == null) {
            bizPlanItem.setCreateTime(new Date());
        }
        bizPlanItem.setUpdateTime(new Date());
    }
}

